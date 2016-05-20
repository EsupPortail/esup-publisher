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
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

/**
 * Created by jgribonvald on 04/06/15.
 */
@AllArgsConstructor
@Slf4j
public class GroupServiceWS implements IGroupService {

    @Inject
    private IPermissionService permissionService;

    @Inject
    private TreeJSDTOFactory treeJSDTOFactory;

    @Inject
    private UserDTOFactory userDTOFactory;

    @Inject
    private IExternalGroupDao externalGroupDao;

    @Inject
    private SubscriberService subscriberService;

    @Inject
    private FilterRepository filterRepository;

    @Inject
    private ContextService contextService;


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
            if (rootCtx != null){
                Filter filter = filterRepository.findOne(FilterPredicates.ofTypeOfOrganization(rootCtx.getKeyId(), FilterType.GROUP));
                if (filter != null) {
                    IExternalGroup group = externalGroupDao.getGroupById(filter.getPattern(),false);
                    if (group != null) {
                        groupIds.add(group.getId());
                    }
                }
            }
            return treeJSDTOFactory.asDTOList(externalGroupDao.getGroupsById(groupIds, true));
        }

        if (PermissionType.CONTRIBUTOR.getMask() <= perms.getFirst().getMask()) {
            PermissionDTO perm = perms.getSecond();
            if (perm != null && perm instanceof PermOnClassifWSubjDTO) {
                Set<SubjectKeyDTO> authorizedSubjects = ((PermOnClassifWSubjDTO) perm).getAuthorizedSubjects();
                Set<String> authorizedGroups = Sets.newHashSet();
                for (SubjectKeyDTO subjDto : authorizedSubjects){
                    if (SubjectType.GROUP.equals(subjDto.getKeyType())) {
                        authorizedGroups.add(subjDto.getKeyId());
                    }
                }
                return treeJSDTOFactory.asDTOList(externalGroupDao.getGroupsById(authorizedGroups, true));
            } else if (perm instanceof PermOnCtxDTO) {
                List<Subscriber> subscribers = subscriberService.getDefaultsSubscribersOfContext(contextKey);
                Set<String> groupIds = Sets.newHashSet();
                for (Subscriber subscriber: subscribers) {
                    if (SubjectType.GROUP.equals(subscriber.getSubjectCtxId().getSubject().getKeyType())) {
                        groupIds.add(subscriber.getSubjectCtxId().getSubject().getKeyId());
                    }
                }
                final ContextKey rootCtx = contextService.getOrganizationCtxOfCtx(contextKey);
                if (rootCtx != null){
                    Filter filter = filterRepository.findOne(FilterPredicates.ofTypeOfOrganization(rootCtx.getKeyId(), FilterType.GROUP));
                    if (filter != null) {
                        IExternalGroup group = externalGroupDao.getGroupById(filter.getPattern(), false);
                        if (group != null) {
                                groupIds.add(group.getId());
                        }
                    }
                }

                return treeJSDTOFactory.asDTOList(externalGroupDao.getGroupsById(groupIds, true));
            } else
                throw new IllegalStateException(String.format("Management of %s type is not yet implemented", perm.getClass()));

        }
        return Lists.newArrayList();
    }

    public List<TreeJS> getGroupMembers(final String id) {
        List<IExternalGroup> groups = externalGroupDao.getDirectGroupMembers(id, true);
        List<IExternalGroup> toDTOs = Lists.newArrayList();
        for (IExternalGroup group: groups){
            boolean found = false;
            for (IExternalGroup members: groups) {
                if (members.hasMembers() && members.getGroupMembers().contains(group.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) toDTOs.add(group);
        }

        return treeJSDTOFactory.asDTOList(toDTOs);
    }

    @Override
    public List<UserDTO> getUserMembers(String id) {
        List<IExternalUser> users = externalGroupDao.getDirectUserMembers(id);
        if (users == null || users.isEmpty()) return Lists.newArrayList();

        return userDTOFactory.asDTOList(users, false);
    }
}
