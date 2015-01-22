package org.esupportail.publisher.repository.externals.ldap;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.repository.externals.IExternalGroupDao;
import org.esupportail.publisher.repository.externals.IGroupMemberDesigner;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jgribonvald on 11/06/15.
 */
@Slf4j
public class LdapGroupMemberDesignerImpl implements IGroupMemberDesigner {

    private ExternalGroupHelper externalGroupHelper;

    public LdapGroupMemberDesignerImpl(ExternalGroupHelper externalGroupHelper) {
        this.externalGroupHelper = externalGroupHelper;
    }

    private static final String endMatch = ":Profs";

    private static final String endRequestPattern = ":*:Profs_*";

    private static final String rootMatch = "((esco)|(cfa)|(clg37)|(agri)):Etablissements:[^:]*";

    private final static Pattern patternProfs = Pattern.compile(rootMatch + endMatch);

    public IExternalGroup designe(IExternalGroup group, IExternalGroupDao externalGroupDao) {
        if (group == null) return group;

        Matcher profMatcher = patternProfs.matcher(group.getId());
        log.debug("Designe for group id {} with matcher {}, and is matching {}", group.getId(), profMatcher.toString(), profMatcher.matches());
        if (profMatcher.matches()) {
            final String filter = "(" + externalGroupHelper.getGroupSearchAttribute() + "=" + group.getId().replace(endMatch, endRequestPattern) + ")";
            log.debug(" ldap filter that will be used : {}", filter);
            List<IExternalGroup> members = externalGroupDao.getGroupsWithFilter(filter, null, false);
            if (members!=null) {
                for (IExternalGroup externalGroup : members) {
                    group.getGroupMembers().add(externalGroup.getId());
                }
            }
        }
        return group;
    }
}
