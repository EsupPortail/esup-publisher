package org.esupportail.publisher.repository.externals.ws;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.portal.ws.client.PortalGroup;
import org.esupportail.portal.ws.client.PortalService;
import org.esupportail.portal.ws.client.exceptions.PortalErrorException;
import org.esupportail.portal.ws.client.exceptions.PortalGroupNotFoundException;
import org.esupportail.publisher.domain.externals.ExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalGroupDisplayNameFormatter;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.externals.IExternalGroupDao;
import org.hibernate.cfg.NotYetImplementedException;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 * Created by jgribonvald on 10/06/15.
 */
@AllArgsConstructor
@Slf4j
public class WSEsupGroupDaoImpl implements IExternalGroupDao {

    @Inject
    private PortalService portalService;

    @Inject
    private IExternalGroupDisplayNameFormatter externalGroupDisplayNameFormatter;

    @Override
    public IExternalGroup getGroupById(@NotNull String id, boolean withMembers) {
        IExternalGroup external;
        try {
            external = toExternal(portalService.getGroupById(id));
            if (external != null && withMembers) {
                List<PortalGroup> grps = portalService.getSubGroupsById(id);
                for (PortalGroup portalGroup : grps) {
                    external.getGroupMembers().add(portalGroup.getId());
                }
            }
        } catch (PortalErrorException e) {
            log.error("PortalService::getSubGroupsById::principal='{}', PortalErrorException : {}", id, e.getMessage());
            throw e;
        } catch (PortalGroupNotFoundException e) {
            log.warn("PortalService::getSubGroupsById:: Group '{}' not found : {}", id, e.getMessage());
            return null;
        }

        return external;
    }

    @Override
    public List<IExternalGroup> getGroupsById(@NotNull Collection<String> ids, boolean withMembers) {
        List<IExternalGroup> groups = Lists.newArrayList();
        for (String id: ids) {
            IExternalGroup group = getGroupById(id, withMembers);
            if (group!=null){
                groups.add(group);
            }
        }
        return groups;
    }

    @Override
    public List<IExternalGroup> getGroupsByIdStartWith(@NotNull Collection<String> ids, boolean withMembers) {
        return getGroupsById(ids,withMembers);
    }


    @Override
    public List<IExternalGroup> getDirectGroupMembers(@NotNull String id, boolean withMembers) {
        List<IExternalGroup> externals = Lists.newArrayList();
        try {
            List<PortalGroup> grps = portalService.getSubGroupsById(id);
            if (withMembers) {
                for (PortalGroup portalGroup : grps) {
                    IExternalGroup externalGroup = toExternal(portalGroup);
                    List<PortalGroup> portalGroups = portalService.getSubGroupsById(portalGroup.getId());
                    if (portalGroups != null && !portalGroups.isEmpty()) {
                        for (PortalGroup pg : portalGroups) {
                            externalGroup.getGroupMembers().add(pg.getId());
                        }
                    }
                    externals.add(externalGroup);
                }
            }
        } catch (PortalErrorException e) {
            log.error(e.getLocalizedMessage());
            throw e;
        } catch (PortalGroupNotFoundException e) {
            log.warn(e.getLocalizedMessage());
            return null;
        }

        return externals;
    }

    @Override
    public List<IExternalGroup> getGroupsWithFilter(@NotNull String stringFilter, String token, boolean withMembers) {
        List<PortalGroup> grps = Lists.newArrayList();

        try {
            if (log.isDebugEnabled()) {
                log.debug("Searching groups with filter '{}' and token '{}' ", stringFilter, token);
            }
            List<PortalGroup> tmp_grps = portalService.searchGroupsByName(stringFilter);
            if (token != null && !token.isEmpty()) {
                for (PortalGroup pg : tmp_grps) {
                    if (pg.getName().toLowerCase().matches(".*" + token.toLowerCase().replaceAll("\\*+", ".*") + ".*")) {
                        grps.add(pg);
                    }
                }
            } else {
                grps = tmp_grps;
            }
        } catch (PortalErrorException e) {
            log.error(e.getLocalizedMessage());
            throw e;
        } catch (PortalGroupNotFoundException e) {
            log.warn(e.getLocalizedMessage());
            return null;
        }
        return toExternal(grps);
    }

    @Override
    public List<IExternalUser> getDirectUserMembers(@NotNull String id) {
       throw new NotYetImplementedException("Method not implemented");
    }

    private ExternalGroup toExternal(final PortalGroup portalGroup) {
        if (portalGroup == null) return null;
        return externalGroupDisplayNameFormatter.format(new ExternalGroup(portalGroup.getId(), portalGroup.getName(), null));
    }

    private List<IExternalGroup> toExternal(final List<PortalGroup> portalGroups) {
        List<IExternalGroup> groups = Lists.newArrayList();
        if (portalGroups != null && !portalGroups.isEmpty()) {
            for (PortalGroup portalGroup: portalGroups) {
                groups.add(toExternal(portalGroup));
            }
        }
        return groups;
    }

}
