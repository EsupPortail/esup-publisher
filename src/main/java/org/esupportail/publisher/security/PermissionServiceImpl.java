package org.esupportail.publisher.security;

import com.google.common.collect.Sets;
import com.mysema.commons.lang.Pair;
import com.mysema.query.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.repository.predicates.ClassificationPredicates;
import org.esupportail.publisher.service.bean.UserContextTree;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.PermissionDTO;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

@Service(value = "permissionService")
@Lazy
@Scope(proxyMode = ScopedProxyMode.INTERFACES)
@Slf4j
public class PermissionServiceImpl implements IPermissionService {

    @Inject
    public UserDTOFactory userDTOFactory;
    @Inject
    public UserContextTree userSessionTree;
    @Inject
    public UserContextLoaderService userSessionTreeLoader;

    public PermissionServiceImpl() {
        super();
    }

    @Override
    public PermissionType getRoleOfUserInContext(@NotNull Authentication authentication,
                                                 @NotNull final ContextKey contextKey) {
        return getRoleOfUserInContext(
            ((CustomUserDetails) authentication.getPrincipal()).getUser(),
            ((CustomUserDetails) authentication.getPrincipal()).getAuthorities(),
            contextKey);
    }

    @Override
    public Pair<PermissionType, PermissionDTO> getPermsOfUserInContext(Authentication authentication, ContextKey contextKey) {
        if (contextKey.getKeyId() == null || contextKey.getKeyType() == null) return null;
        if (!userSessionTree.isTreeLoaded()) {
            userSessionTreeLoader.loadUserTree(((CustomUserDetails) authentication.getPrincipal()).getUser(),
                ((CustomUserDetails) authentication.getPrincipal()).getAuthorities());
        }
        return userSessionTree.getPermsFromContextTree(contextKey);
    }

    private PermissionType getRoleOfUserInContext(final UserDTO user,
                                                  final Collection<? extends GrantedAuthority> authorities,
                                                  final ContextKey contextKey) {
        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.ADMIN))) {
            return PermissionType.ADMIN;
        }

        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.USER)) && contextKey != null && contextKey.getKeyId() != null && contextKey.getKeyType() != null) {
            if (!userSessionTree.isTreeLoaded()) {
                userSessionTreeLoader.loadUserTree(user, authorities);
            }
            return userSessionTree.getRoleFromContextTree(contextKey);
        }

        return null;
    }

    @Override
    public Predicate filterAuthorizedAllOfContextType(@NotNull Authentication authentication, @NotNull final ContextType contextType,
                                                      @NotNull final PermissionType permissionType, @NotNull final Predicate predicate) {
        final UserDTO user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        final Collection<? extends GrantedAuthority> authorities = ((CustomUserDetails) authentication.getPrincipal()).getAuthorities();

        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.ADMIN))) {
            return predicate;
        }

        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.USER))) {
            if (!userSessionTree.isTreeLoaded()) {
                userSessionTreeLoader.loadUserTree(user, authorities);
            }
            Set<Long> ids = userSessionTree.getAllAuthorizedContextIdsOfType(contextType, permissionType);

            switch (contextType) {
                case ORGANIZATION : return QOrganization.organization.id.in(ids).and(predicate);
                case PUBLISHER : return QPublisher.publisher.id.in(ids).and(predicate);
                case CATEGORY : return QAbstractClassification.abstractClassification.id.in(ids).and(predicate).and(ClassificationPredicates.CategoryClassification());
                case FEED : return QAbstractFeed.abstractFeed.id.in(ids).and(predicate);
                case ITEM : return QAbstractItem.abstractItem.id.in(ids).and(predicate);
                default: break;
            }
        }
        throw new AccessDeniedException(String.format("Access is denied for ContextType %s with upper PermissionType %s", contextType, permissionType));
    }

    @Override
    public Predicate filterAuthorizedChildsOfContext(@NotNull Authentication authentication, @NotNull final ContextKey contextKey,
                                                     @NotNull final PermissionType permissionType, @NotNull final Predicate predicate) {
        final UserDTO user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        final Collection<? extends GrantedAuthority> authorities = ((CustomUserDetails) authentication.getPrincipal()).getAuthorities();

        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.ADMIN))) {
            return predicate;
        }

        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.USER))) {
            if (!userSessionTree.isTreeLoaded()) {
                userSessionTreeLoader.loadUserTree(user, authorities);
            }
            Pair<ContextType, Set<Long>> p = userSessionTree.getChildsOfContext(contextKey, permissionType);
            if (p != null) {
                switch (p.getFirst()) {
                    // there is n't parent of orgnaization so this case doesn't exist
                    //case ORGANIZATION : filter = QOrganization.organization.id.in(p.getSecond()).and(predicate); break;
                    case PUBLISHER : return QPublisher.publisher.id.in(p.getSecond()).and(predicate);
                    case CATEGORY : return QCategory.category.id.in(p.getSecond()).and(predicate);
                    case FEED : return QAbstractFeed.abstractFeed.id.in(p.getSecond()).and(predicate);
                    case ITEM :
                        PermissionType ctxPerm = getRoleOfUserInContext(user, authorities, contextKey);
                        // return all item if Role is greater and not equals than CONTRIBUTOR
                        if (ctxPerm != null && ctxPerm.getMask() > PermissionType.CONTRIBUTOR.getMask()) {
                            return QItemClassificationOrder.itemClassificationOrder.itemClassificationId.abstractItem.id.in(p.getSecond()).and(predicate);
                        }
                        // else we filter on items owned
                        Set<Long> ids = Sets.newHashSet();
                        for (Long id : p.getSecond()) {
                            if (canEditCtx(user, authorities, new ContextKey(id, ContextType.ITEM))) {
                                ids.add(id);
                            }
                        }
                        return QItemClassificationOrder.itemClassificationOrder.itemClassificationId.abstractItem.id.in(ids).and(predicate);
                    default : break;
                }
            }
        }
        throw new AccessDeniedException(String.format("Access is denied for %s with upper PermissionType %s", contextKey.toString(), permissionType));
    }


    @Override
    public boolean canCreateInCtx(@NotNull Authentication authentication, @NotNull ContextKey contextKey) {
        final UserDTO user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        final Collection<? extends GrantedAuthority> authorities = ((CustomUserDetails) authentication.getPrincipal()).getAuthorities();

        log.debug("Testing canCreateInCtx in context {} for  user {}", contextKey, user);
        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.ADMIN))) {
            return true;
        }

        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.USER))) {
            if (!userSessionTree.isTreeLoaded()) {
                userSessionTreeLoader.loadUserTree(user, authorities);
            }
            PermissionType perm = null;
            switch (contextKey.getKeyType()) {
                case ORGANIZATION : return false;
                case PUBLISHER :
                    perm = userSessionTree.getRoleFromContextTree(contextKey);
                    return perm != null && perm.getMask() >=  PermissionType.MANAGER.getMask();
                case CATEGORY :
                    perm = userSessionTree.getRoleFromContextTree(contextKey);
                    // if a category as items or feeds rights are not the sames
                    Boolean hasItemAsChilds = userSessionTree.contextContainsItems(contextKey);
                    if (hasItemAsChilds == null) {
                        return false;
                    } else if (hasItemAsChilds) {
                        return perm != null && perm.getMask() >=  PermissionType.CONTRIBUTOR.getMask();
                    }
                    return perm != null && perm.getMask() >=  PermissionType.MANAGER.getMask();
                case FEED :
                    perm = userSessionTree.getRoleFromContextTree(contextKey);
                    return perm != null && perm.getMask() >=  PermissionType.CONTRIBUTOR.getMask();
                default: return false;
            }

        }
        return false;
    }

    @Override
    public boolean canCreateInCtx(@NotNull Authentication authentication, @NotNull final long contextId, @NotNull final ContextType contextType) {
        return canCreateInCtx(authentication, new ContextKey(contextId, contextType));
    }

    private boolean canEditCtx(@NotNull UserDTO user,@NotNull Collection<? extends GrantedAuthority> authorities , @NotNull ContextKey contextKey) {
        log.debug("Testing canEditCtx in context {} for  user {}", contextKey, user);
        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.ADMIN))) {
            return true;
        }

        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.USER))) {
            if (!userSessionTree.isTreeLoaded()) {
                userSessionTreeLoader.loadUserTree(user, authorities);
            }
            PermissionType perm = null;
            switch (contextKey.getKeyType()) {
                case ORGANIZATION :
                case PUBLISHER :
                case CATEGORY :
                case FEED :
                    perm = userSessionTree.getRoleFromContextTree(contextKey);
                    return perm != null && perm.getMask() >=  PermissionType.MANAGER.getMask();
                case ITEM :
                    perm = userSessionTree.getRoleFromContextTree(contextKey);
                    final boolean isOwner = userSessionTree.isItemOwner(contextKey.getKeyId(), user.getModelId());
                    return perm != null && (perm.getMask() >=  PermissionType.MANAGER.getMask() || perm.getMask() >=  PermissionType.CONTRIBUTOR.getMask() && isOwner) || isOwner;
                default: return false;
            }

        }
        return false;
    }

    @Override
    public boolean canEditCtx(@NotNull Authentication authentication, @NotNull ContextKey contextKey) {
        final UserDTO user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        final Collection<? extends GrantedAuthority> authorities = ((CustomUserDetails) authentication.getPrincipal()).getAuthorities();

        return canEditCtx(user, authorities, contextKey);
    }

    @Override
    public boolean canEditCtx(@NotNull Authentication authentication, @NotNull final long contextId, @NotNull final ContextType contextType) {
        return canEditCtx(authentication, new ContextKey(contextId, contextType));
    }

    @Override
    public boolean canDeleteCtx(@NotNull Authentication authentication, @NotNull ContextKey contextKey) {
        final UserDTO user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        final Collection<? extends GrantedAuthority> authorities = ((CustomUserDetails) authentication.getPrincipal()).getAuthorities();

        log.debug("Testing canDeleteCtx in context {} for  user {}", contextKey, user);
        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.ADMIN))) {
            return true;
        }

        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.USER))) {
            if (!userSessionTree.isTreeLoaded()) {
                userSessionTreeLoader.loadUserTree(user, authorities);
            }
            PermissionType perm = null;
            switch (contextKey.getKeyType()) {
                case ORGANIZATION : return false;
                case PUBLISHER : return false;
                case CATEGORY :
                case FEED :
                    perm = userSessionTree.getRoleFromContextTree(contextKey);
                    return perm != null && perm.getMask() >=  PermissionType.MANAGER.getMask();
                case ITEM :
                    perm = userSessionTree.getRoleFromContextTree(contextKey);
                    boolean isOwner = userSessionTree.isItemOwner(contextKey.getKeyId(), user.getModelId());
                    return perm != null && (perm.getMask() >=  PermissionType.MANAGER.getMask() || perm.getMask() >=  PermissionType.CONTRIBUTOR.getMask() && isOwner) || isOwner;
                default: return false;
            }
        }
        return false;
    }

    @Override
    public boolean canDeleteCtx(@NotNull Authentication authentication, @NotNull final long contextId, @NotNull final ContextType contextType) {
        return canDeleteCtx(authentication, new ContextKey(contextId, contextType));
    }

    @Override
    public boolean canEditCtxPerms (@NotNull Authentication authentication, @NotNull ContextKey contextKey) {
        final UserDTO user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        final Collection<? extends GrantedAuthority> authorities = ((CustomUserDetails) authentication.getPrincipal()).getAuthorities();

        log.debug("Testing canEditCtxPerm in context {} for  user {}", contextKey, user);

        // Permissions are autorized only for ADMIN on ORGANIZATIONS
        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.ADMIN))) {
            return true;
        } else if (contextKey.getKeyType().equals(ContextType.ORGANIZATION)) {
            return false;
        }
        return canEditCtx(user, authorities, contextKey);
    }

    @Override
    public boolean canEditCtxTargets (@NotNull Authentication authentication, @NotNull ContextKey contextKey) {
        log.debug("Testing canEditCtxTargets in context {} for  user {}", contextKey);
        // rights are the sames than in perms
        return this.canEditCtxPerms(authentication, contextKey);
    }

    @Override
    public boolean hasAuthorizedChilds(@NotNull Authentication authentication, @NotNull ContextKey contextKey) {
        final UserDTO user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        final Collection<? extends GrantedAuthority> authorities = ((CustomUserDetails) authentication.getPrincipal()).getAuthorities();

        log.debug("Testing hasAuthorizedChilds in context {} for  user {}", contextKey, user);

        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.ADMIN))) {
            return !ContextType.ITEM.equals(contextKey.getKeyType());
        }

        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.USER))) {
            if (!userSessionTree.isTreeLoaded()) {
                userSessionTreeLoader.loadUserTree(user, authorities);
            }
            return !ContextType.ITEM.equals(contextKey.getKeyType()) && userSessionTree.hasChildsOnContext(contextKey, PermissionType.LOOKOVER);

        }

        return false;
    }
}
