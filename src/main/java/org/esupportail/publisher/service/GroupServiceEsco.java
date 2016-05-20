package org.esupportail.publisher.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mysema.commons.lang.Pair;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.Filter;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.FilterType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.FilterRepository;
import org.esupportail.publisher.repository.externals.IExternalGroupDao;
import org.esupportail.publisher.repository.predicates.FilterPredicates;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.factories.TreeJSDTOFactory;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.service.util.SubscriberService;
import org.esupportail.publisher.web.rest.dto.*;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jgribonvald on 04/06/15.
 */
@AllArgsConstructor
@Slf4j
public class GroupServiceEsco implements IGroupService {

    @Inject
    private IPermissionService permissionService;

    @Inject
    private TreeJSDTOFactory treeJSDTOFactory;

    @Inject
    private UserDTOFactory userDTOFactory;

    @Inject
    private IExternalGroupDao externalGroupDao;

    @Inject
    private ExternalGroupHelper externalGroupHelper;

    @Inject
    private SubscriberService subscriberService;

    @Inject
    private FilterRepository filterRepository;

    @Inject
    private ContextService contextService;

    private Pattern pattern = Pattern.compile("([a-zA-Z0-9_ ]+:[a-zA-Z0-9_ ]+:[a-zA-Z0-9_ ]+:).*");

    @Override
    public List<TreeJS> getRootNodes(final ContextKey contextKey, final List<ContextKey> subContextKeys) {
        if (contextKey.getKeyType() == null || contextKey.getKeyId() == null) {
            return Lists.newArrayList();
        }
        Pair<PermissionType, PermissionDTO> perms = permissionService.getPermsOfUserInContext(SecurityContextHolder.getContext().getAuthentication(), contextKey);
        if ((perms == null || PermissionType.LOOKOVER.equals(perms.getFirst())) && subContextKeys != null) {
            Pair<PermissionType, PermissionDTO> lowerPerm = null;
            // we need to get the lower perm to apply rules on lower context to avoid problems of rights !
            for (ContextKey ctxKey: subContextKeys) {
                perms = permissionService.getPermsOfUserInContext(SecurityContextHolder.getContext().getAuthentication(), ctxKey);
                if (perms != null && (lowerPerm == null || perms.getFirst().getMask() < lowerPerm.getFirst().getMask())) {
                    lowerPerm = perms;
                    // if contributor if found we have the lower rights !
                    if (lowerPerm.getFirst().getMask() == PermissionType.CONTRIBUTOR.getMask()) break;
                }
            }
        }
        log.debug("getRootNodes for ctx {}, with permsType {} and permsDTO {}", contextKey, perms.getFirst(), perms.getSecond());
        if (perms == null || perms.getFirst() == null || !PermissionType.ADMIN.equals(perms.getFirst()) && perms.getSecond() == null) {
            return Lists.newArrayList();
        }

        // if ADMIN perms.getSecond() is null as all is authorized
        if (PermissionType.ADMIN.equals(perms.getFirst())) {
            final ContextKey rootCtx = contextService.getOrganizationCtxOfCtx(contextKey);
            Set<String> groupIds = Sets.newHashSet();
            Set<TreeJS> tree = Sets.newHashSet();
            if (rootCtx != null){
                Filter filter = filterRepository.findOne(FilterPredicates.ofTypeOfOrganization(rootCtx.getKeyId(), FilterType.GROUP));
                if (filter != null) {
                    List<IExternalGroup> groups = externalGroupDao.getGroupsWithFilter(filter.getPattern(), null, false);
                    if (groups != null) {
                        /*for (IExternalGroup group : groups) {
                            groupIds.add(group.getId());
                        }*/
                        tree.addAll(getGroupMembers(groups));
                    }
                }
            }
            //return treeJSDTOFactory.asDTOList(externalGroupDao.getGroupsById(groupIds, true));
            return Lists.newArrayList(tree);
        }

        if (PermissionType.CONTRIBUTOR.getMask() <= perms.getFirst().getMask()) {
            PermissionDTO perm = perms.getSecond();
            if (perm != null) {
                if (perm instanceof PermOnClassifWSubjDTO) {
                    Set<SubjectKeyDTO> authorizedSubjects = ((PermOnClassifWSubjDTO) perm).getAuthorizedSubjects();
                    Set<String> authorizedGroups = Sets.newHashSet();
                    for (SubjectKeyDTO subjDto : authorizedSubjects) {
                        if (SubjectType.GROUP.equals(subjDto.getKeyType())) {
                            authorizedGroups.add(subjDto.getKeyId() + "*");
                        }
                    }
                    log.debug("PermOnClassifWSubjDTO with groups {}", authorizedGroups);
                    return treeJSDTOFactory.asDTOList(externalGroupDao.getGroupsByIdStartWith(authorizedGroups, true));
                } else if (perm instanceof PermOnCtxDTO) {
                    log.debug("PermOnCtxDTO");
                    List<Subscriber> subscribers = subscriberService.getDefaultsSubscribersOfContext(contextKey);
                    Set<String> groupIds = Sets.newHashSet();
                    for (Subscriber subscriber : subscribers) {
                        if (SubjectType.GROUP.equals(subscriber.getSubjectCtxId().getSubject().getKeyType())) {
                            groupIds.add(subscriber.getSubjectCtxId().getSubject().getKeyId() + "*");
                        }
                    }
                    final ContextKey rootCtx = contextService.getOrganizationCtxOfCtx(contextKey);
                    Set<TreeJS> tree = Sets.newHashSet();
                    if (rootCtx != null) {
                        Filter filter = filterRepository.findOne(FilterPredicates.ofTypeOfOrganization(rootCtx.getKeyId(), FilterType.GROUP));
                        if (filter != null) {
                            List<IExternalGroup> groups = externalGroupDao.getGroupsWithFilter(filter.getPattern(), null, false);
                            if (groups != null) {
                            /*for (IExternalGroup group : groups) {
                                groupIds.add(group.getId());
                            }*/
                                tree.addAll(getGroupMembers(groups));
                            }
                        }
                    }

                    return Lists.newArrayList(tree);
                    //return treeJSDTOFactory.asDTOList(externalGroupDao.getGroupsById(groupIds, true));
                } else
                    throw new IllegalStateException(String.format("Management of %s type is not yet implemented", perm.getClass()));
            }

        }
        return Lists.newArrayList();
    }

    @Override
    public List<TreeJS> getGroupMembers(final String id) {
        List<TreeJS> tree = Lists.newArrayList();
        org.springframework.ldap.filter.Filter filter = new EqualsFilter(externalGroupHelper.getGroupSearchAttribute(), id + ":*");
        List<IExternalGroup> groups = externalGroupDao.getGroupsWithFilter(filter.encode(),null, false);
        for (IExternalGroup group: groups){
            String[] splitted = group.getId().replaceFirst(id, "").split(":");
            if (splitted.length > 0) {
                String currentPath = id;
                for (int i =0; i<splitted.length-1; i++){
                    String parent = currentPath;
                    currentPath += ":" + splitted[i];
                    tree.add(from(currentPath, splitted[i], i < splitted.length-1, parent));
                }
            }
        }

        return tree;
        //return treeJSDTOFactory.asDTOList(toDTOs);
    }

    public List<UserDTO> getUserMembers(final String id) {
        List<IExternalUser> users = externalGroupDao.getDirectUserMembers(id);
        if (users == null || users.isEmpty()) return Lists.newArrayList();

        return userDTOFactory.asDTOList(users, false);
    }

    private List<TreeJS> getGroupMembers(final List<IExternalGroup> groups) {
        List<TreeJS> tree = Lists.newArrayList();
        for (IExternalGroup group: groups){
            log.debug("Group to Decompose : {}", group.getId());
            Matcher idmatch = pattern.matcher(group.getId());
            String id = group.getId();
            String subpath = group.getId();
            if (idmatch.find()) {
                id = idmatch.group(1).substring(0,idmatch.group(1).length()-1);
                subpath = group.getId().replace(id+":", "");
            }
            log.debug("Groupe Decomposition : {}, {}", id, subpath);

            String[] splitted = subpath.split(":");
            if (splitted.length > 0) {
                String currentPath = id.equals(subpath) ? splitted[0] : id;
                for (int i = 0; i < splitted.length - 1; i++) {
                    String parent = currentPath;
                    currentPath += ":" + splitted[i];
                    tree.add(from(currentPath, splitted[i], i < splitted.length - 1, parent));
                }
            }
        }

        return tree;
        //return treeJSDTOFactory.asDTOList(toDTOs);
    }

    private TreeJS from(@NotNull final String id, @NotNull final String displayName, final boolean hasMembers, @NotNull final String parent){
        TreeJS node = new TreeJS();
        node.setId(id);
        node.setText(displayName);
        node.setParent(parent);
        node.getLi_attr().setLeaf(!hasMembers);
        node.getLi_attr().setBase(id);
        node.getA_attr().setTitle(id);
        log.debug("{}", node);
        return node;
    }



}
