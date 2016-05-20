/**
 *
 */
package org.esupportail.publisher.service.bean;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mysema.commons.lang.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.service.factories.CompositeKeyDTOFactory;
import org.esupportail.publisher.web.rest.dto.PermOnCtxDTO;
import org.esupportail.publisher.web.rest.dto.PermissionDTO;
import org.esupportail.publisher.web.rest.dto.SubjectKeyDTO;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald
 *
 *         Should be used as session bean
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class UserContextTree {

    private Map<ContextKey, UserContextInfos> contexts = Maps
        .newConcurrentMap();

    @Inject
    @Named("subjectKeyDTOFactoryImpl")
    private CompositeKeyDTOFactory<SubjectKeyDTO, SubjectKey, String, SubjectType> subjectKeyConverter;

    @Setter
    private Boolean superAdmin;

    @Setter
    private boolean userTreeLoaded;

    public UserContextTree() {
        super();
    }

    /**
     * To construct the tree use this add on all context.
     *
     * @param ctx
     *            the context to add discovered from the parent.
     * @param parent
     *            The parent from where the context was loaded
     * @param childs
     *            All childs of the context,
     * @param perms
     *            The upper Role that the user has.
     */
    public synchronized void addCtx(@NotNull final ContextKey ctx,final boolean isLastNode,
                                    final ContextKey parent, final Set<ContextKey> childs,
                                    final PermissionDTO perms, final PermissionType permType) {
        if (!isCtxLoaded(ctx)) {
            UserContextInfos current = new UserContextInfos();
            if (ctx instanceof OwnerContextKey && ((OwnerContextKey) ctx).getOwner() != null) {
                current.setOwner(subjectKeyConverter.convertToDTOKey(((OwnerContextKey) ctx).getOwner()));
            }
            if (childs != null) {
                current.setChilds(Sets.newConcurrentHashSet(childs));
            }
            current.setPerms(perms, permType);
            current.setLastLeaf(isLastNode);
            if (!ctx.getKeyType().equals(ContextType.ORGANIZATION)) {
                Assert.isTrue(parent != null && isCtxLoaded(parent),
                    "Context " + ctx.toString() + " doesn't have a parent = '" + parent.toString() + "' or parent isn't loaded !");
                contexts.put(ctx, current);
                this.linkToParent(ctx, parent);
            } else {
                contexts.put(ctx, current);
            }
        } else if (!ctx.getKeyType().equals(ContextType.ORGANIZATION)) {
            Assert.isTrue(parent != null && isCtxLoaded(parent),
                "Context " + ctx.toString() + " doesn't have a parent = '" + parent.toString() + "' or parent isn't loaded !");
            this.linkToParent(ctx, parent);
        }
    }
    public void addCtx(@NotNull final ContextKey ctx,final boolean isLastNode,
                                    final ContextKey parent, final Set<ContextKey> childs,
                                    final PermOnCtxDTO perms) {
        if (perms != null) {
            this.addCtx(ctx, isLastNode, parent, childs, perms, perms.getRole());
        } else {
            this.addCtx(ctx, isLastNode, parent, childs, null, null);
        }
    }

    /**
     * Must be used when a user create a new context, and to be able to see it
     * @param ctx
     * @param isLastNode
     * @param parent
     */
    public void addCreatedCtx(@NotNull final ContextKey ctx,final boolean isLastNode,
                       final ContextKey parent) {
        final Pair<PermissionType, PermissionDTO> perms = this.getPermsFromContextTree(parent);
        this.addCtx(ctx, isLastNode, parent, null, null, null);
    }

    /**
     * There mustn't have childs in the context to remove.
     *
     * @param ctx
     *              the context to remove.
     */
    public synchronized void removeCtx(final ContextKey ctx) {
        if (contexts.containsKey(ctx)) {
            UserContextInfos current = contexts.get(ctx);
            Assert.isTrue(current.getChilds() == null
                    || current.getChilds().isEmpty(),
                "Can't remove context as it contains childs !");
            contexts.remove(ctx);
            for (ContextKey parent : current.getParents()) {
                contexts.get(parent).getChilds().remove(ctx);
            }
        }
    }

    public synchronized void removeLinkToParent(final ContextKey ctx,
                                                final ContextKey parent) {
        Assert.isTrue(isCtxLoaded(ctx), "Context wasn't loaded !");
        Assert.isTrue(isCtxLoaded(parent), "Context parent wasn't loaded !");
        UserContextInfos currentInfos = contexts.get(ctx);
        UserContextInfos parentInfos = contexts.get(parent);
        if (currentInfos.getParents().contains(parent)) {
            Assert.isTrue(currentInfos.getParents().size() > 1,
                "Context will become orphaned !");
            currentInfos.getParents().remove(parent);
            parentInfos.getChilds().remove(ctx);
            // process parent role to update after removing parent
            currentInfos.setParentsPerms(null);
            for (ContextKey parentKey : currentInfos.getParents()) {
                UserContextInfos uci = contexts.get(parentKey);
                currentInfos.setParentsPerms(uci.getPermsObject(), uci.getPermsType());
            }
        }
    }

    /**
     * Return the upper role after checking recursively the parent tree. All the
     * tree must be loaded.
     *
     * @param ctx
     * @return
     */
    public PermissionType getRoleFromContextTree(@NotNull final ContextKey ctx) {
        if (userTreeLoaded) {
            if (this.superAdmin) return PermissionType.ADMIN;
            if (contexts.containsKey(ctx)) return contexts.get(ctx).getPermsType();
            log.warn("Call getRoleFromContextTree - ContextKey {} not found in User Tree !", ctx);
        } else {
            log.warn("Call getRoleFromContextTree - User Tree is not loaded !");
        }
        return null;
    }

    public Pair<PermissionType, PermissionDTO> getPermsFromContextTree(@NotNull final ContextKey ctx) {
        if (userTreeLoaded) {
            if (this.superAdmin) return new Pair<>(PermissionType.ADMIN, null);
            if (contexts.containsKey(ctx)) {
                UserContextInfos infos = contexts.get(ctx);
                return new Pair<>(infos.getPermsType(), infos.getPermsObject());
            }
            log.warn("Call getPermsFromContextTree - ContextKey {} not found in User Tree !", ctx);
        } else {
            log.warn("Call getPermsFromContextTree - User Tree is not loaded !");
        }
        return null;
    }

    public boolean isItemOwner(final long itemId, final SubjectKeyDTO userId) {
        final ContextKey ctx = new ContextKey(itemId, ContextType.ITEM);
        if (userTreeLoaded && userId != null) {
            UserContextInfos infos = contexts.get(ctx);
            if (infos != null && userId.equals(infos.getOwner())) return true;
        } else {
            log.warn("Call isItemOwner - User Tree is not loaded or userId null !");
        }
        return false;
    }

    public Set<ContextKey> getChildsOfContext(@NotNull final ContextKey ctx) {
        if (userTreeLoaded && contexts.containsKey(ctx)) {
            return Sets.newHashSet(contexts.get(ctx).getChilds());
        } else {
            log.warn("Call getChildsOfContext - User Tree is not loaded or context is unknown !");
        }
        return null;
    }

    public Set<ContextKey> getParentsOfContext(@NotNull final ContextKey ctx) {
        if (userTreeLoaded && contexts.containsKey(ctx)) {
            return Sets.newHashSet(contexts.get(ctx).getParents());
        } else {
            log.warn("Call getParentsOfContext - User Tree is not loaded or context is unknown !");
        }
        return null;
    }

    public Set<Long> getAllAuthorizedContextIdsOfType(@NotNull final ContextType contextType,@NotNull final PermissionType permissionType) {
        log.debug("Call getAllAuthorizedContextIdsOfType with params ctxType {}, permType {}", contextType, permissionType);
        if (userTreeLoaded) {
            Set<Long> ctxs = Sets.newHashSet();
            for (Map.Entry<ContextKey, UserContextInfos> entry : this.contexts.entrySet()) {
                log.debug("watch for ctx {} with perms {}", entry.getKey(), entry.getValue());
                if (contextType.equals(entry.getKey().getKeyType()) && entry.getValue().getPermsType() != null
                    && permissionType.getMask() <= entry.getValue().getPermsType().getMask()) {
                    log.debug("Authorized context!");
                    ctxs.add(entry.getKey().getKeyId());
                }
            }
            return ctxs;
        }   else {
            log.warn("Call getAllAuthorizedContextIdsOfType - User Tree is not loaded !");
        }
        return null;
    }

    public Pair<ContextType, Set<Long>> getChildsOfContext(@NotNull final ContextKey ctxKey,@NotNull final PermissionType permissionType) {
        if (userTreeLoaded && contexts.containsKey(ctxKey)) {
            Set<Long> ctxs = Sets.newHashSet();
            ContextType type = null;
            log.debug("getChildsOfContext with params ctxKey {}, permissionType {}", ctxKey, permissionType );
            for (ContextKey ctx : contexts.get(ctxKey).getChilds()) {
                Assert.notNull(ctx.getKeyId(), "A context must not have a null ID");
                Assert.notNull(ctx.getKeyType(), "A context must not have a null Type");
                // case of unclassified items associated to Organization we go to next iteration
                if (ContextType.ITEM.equals(ctx.getKeyType()) && ContextType.ORGANIZATION.equals(ctxKey.getKeyType())){
                    continue;
                }
                UserContextInfos infos = contexts.get(ctx);
                log.debug("Checking on ctx {} with contextInfos {}", ctx, infos);
                if (type == null) {
                    type = ctx.getKeyType();
                } else if (!ctx.getKeyType().equals(type)) {
                    log.warn ("Context {} has childs of different ContextType !", ctxKey);
                    return null;
                }
                if (infos.getPermsType() != null && permissionType.getMask() <= infos.getPermsType().getMask()){
                    ctxs.add(ctx.getKeyId());
                    log.debug("Adding child !");
                }
            }
            return new Pair<ContextType, Set<Long>>(type, ctxs);
        } else {
            log.warn("Call getChildsOfContext - User Tree is not loaded or context is unknown !");
        }
        return null;
    }

    public boolean hasChildsOnContext(@NotNull final ContextKey ctxKey,final PermissionType permissionType) {
        PermissionType perm = permissionType;
        if (permissionType == null) perm = PermissionType.LOOKOVER;
        if (userTreeLoaded && contexts.containsKey(ctxKey)) {
            Set<Long> ctxs = Sets.newHashSet();
            for (ContextKey ctx : contexts.get(ctxKey).getChilds()) {
                Assert.notNull(ctx.getKeyId(), "A context must not have a null ID");
                Assert.notNull(ctx.getKeyType(), "A context must not have a null Type");
                UserContextInfos infos = contexts.get(ctx);
                if (infos.getPermsType() != null && perm.getMask() <= infos.getPermsType().getMask()){
                    ctxs.add(ctx.getKeyId());
                }
            }
            if (!ctxs.isEmpty()) return true;
        } else {
            log.warn("Call hasChildsOnContext - User Tree is not loaded or context is unknown !");
        }
        return false;
    }

    public Boolean contextContainsItems(@NotNull final ContextKey ctxKey) {
        if (userTreeLoaded && contexts.containsKey(ctxKey)) {
            UserContextInfos infos = contexts.get(ctxKey);
            return infos.isLastLeaf();
        }
        return null;
    }

    private void linkToParent(final ContextKey ctx, final ContextKey parent) {
        Assert.isTrue(isCtxLoaded(ctx), "Context wasn't loaded !");
        Assert.isTrue(isCtxLoaded(parent), "Context parent wasn't loaded !");
        UserContextInfos currentInfos = contexts.get(ctx);
        UserContextInfos parentInfos = contexts.get(parent);
        currentInfos.getParents().add(parent);
        parentInfos.getChilds().add(ctx);
        currentInfos.setParentsPerms(parentInfos.getPermsObject(), parentInfos.getPermsType());
    }

    public boolean isCtxLoaded(final ContextKey ctx) {
        return contexts.containsKey(ctx) && contexts.get(ctx) != null;
    }

    public boolean isFilteredTree() {
        return superAdmin != null && !superAdmin;
    }

    public boolean isTreeLoaded() {
        return superAdmin != null && userTreeLoaded;
    }

    public void cleanup() {
        contexts.clear();
        superAdmin = null;
        userTreeLoaded = false;
    }

    private class UserContextInfos {
        private PermissionDTO perms;
        private PermissionDTO parentsPerms;
        //@Setter
        private PermissionType permType;
        private PermissionType parentsPermType;
        @Setter
        @Getter
        private SubjectKeyDTO owner;

        @Getter
        @Setter
        private Set<ContextKey> parents = Sets.newConcurrentHashSet();
        @Getter
        @Setter
        private Set<ContextKey> childs = Sets.newConcurrentHashSet();

        // helper to know if the node is the last, and contains directly items
        @Getter
        @Setter
        private boolean isLastLeaf = false;

        public UserContextInfos() {
            super();
        }

        public PermissionType getPermsType() {
            if (parentsPermType == null || permType != null && permType.getMask() > parentsPermType.getMask()) {
                return permType;
            }
            return parentsPermType;
        }

        /**
         * warning permObject can be null when permissions are obtained from childs
         * @return
         */
        public PermissionDTO getPermsObject() {
            if (parentsPermType == null || permType != null && permType.getMask() > parentsPermType.getMask()) {
                return perms;
            }
            return parentsPerms;
        }

        public void setPerms(final PermOnCtxDTO perms) {
            this.perms = perms;
            this.permType = perms.getRole();
        }

        public void setPerms(final PermissionDTO perms, final PermissionType permType) {
            this.perms = perms;
            this.permType = permType;
        }

        /*public void setParentsPerms(final PermissionType parentsPerms) {
                    if (parentsPerms != null && (this.parentsPerms == null || parentsPerms.getMask() > this.parentsPerms.getMask())) {
                        this.parentsPerms = parentsPerms;
                    }
                }*/
        public void setParentsPerms(final PermOnCtxDTO parentsPerms) {
            if (parentsPerms != null && (this.parentsPerms == null || parentsPerms.getRole().getMask() > this.parentsPermType.getMask())) {
                this.parentsPerms = parentsPerms;
                this.permType = parentsPerms.getRole();
            }
        }

        public void setParentsPerms(final PermissionDTO parentsPerms, final PermissionType permType) {
            if (parentsPerms != null && (this.parentsPerms == null || permType.getMask() > this.parentsPermType.getMask())) {
                this.parentsPerms = parentsPerms;
                this.parentsPermType = permType;
            }
        }

        @Override
        public String toString() {
              return "UserContextInfos{" +
                  "permsType=" + permType +
                  ", parentsPermType=" + parentsPermType +
                  ", isLastLeaf=" + isLastLeaf +
                  ", owner='" + owner + '\'' +
                  ", perms=" + ((perms != null) ? perms.toStringLite() : null) +
                  ", parentsPerms=" + ((parentsPerms != null) ? parentsPerms.toStringLite() : null) +
                  '}';
        }

        // private boolean childsLoaded;
        //
        // private boolean permsLoaded;
    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("UserContextTree{" +
            "superAdmin=" + superAdmin +
            ", userTreeLoaded=" + userTreeLoaded +
            ", contexts= [\n");

        for (Map.Entry<ContextKey, UserContextInfos> entry : contexts.entrySet()) {
            str.append("{").append(entry.getKey()).append("=").append(entry.getValue()).append("}").append("\n");
        }
        str.append("}");

        return str.toString();
    }
}
