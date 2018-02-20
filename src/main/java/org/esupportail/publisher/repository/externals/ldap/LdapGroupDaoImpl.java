/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.repository.externals.ldap;

import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalGroupDisplayNameFormatter;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.externals.IExternalGroupDao;
import org.esupportail.publisher.repository.externals.IExternalUserDao;
import org.esupportail.publisher.repository.externals.IGroupMemberDesigner;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.filter.WhitespaceWildcardsFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;

/**
 *
 * @author GIP RECIA - Julien Gribonvald 11 juil. 2014
 */
@Data
@AllArgsConstructor
@Slf4j
public class LdapGroupDaoImpl implements IExternalGroupDao {

    /**
     * Spring template used to perform search in the ldap.
     */
    //@Autowired
    private LdapTemplate ldapTemplate;

    //@Autowired
    private ExternalGroupHelper externalGroupHelper;

    //@Autowired
    private List<IExternalGroupDisplayNameFormatter> groupDisplayNameFormatters;

    //@Autowired
    private IExternalUserDao externalUserDao;

    //@Autowired
    private List<IGroupMemberDesigner> groupMemberDesigners;

    /**
     * constructor.
     */
    public LdapGroupDaoImpl() {
        super();
    }


    @Override
    //@Cacheable(value = "ExternalGroups", key = "#id")
    public IExternalGroup getGroupById(@NotNull final String id, final boolean withMembers) {
        AndFilter filter = new AndFilter();
        filter.append(new EqualsFilter(externalGroupHelper.getGroupIdAttribute(), id));
        if (log.isDebugEnabled()) {
            log.debug("LDAP filter applied : {}", filter);
        }
        ContextMapper<IExternalGroup> mapper;
        if (withMembers) {
            mapper = new LdapGroupContextMapper(this.externalGroupHelper, this.groupDisplayNameFormatters);
        } else {
            mapper = new LdapGroupWithoutMembersContextMapper(this.externalGroupHelper, this.groupDisplayNameFormatters);
        }
        // SearchControls constraints = new SearchControls();
        // constraints.setReturningAttributes((String[])
        // ldapUserHelper.getAttributes().toArray());
        LdapQuery query = LdapQueryBuilder.query()
            .attributes((String[]) externalGroupHelper.getAttributes().toArray(new String[externalGroupHelper.getAttributes().size()]))
            .base(externalGroupHelper.getGroupDNSubPath()).filter(filter);
        IExternalGroup group;
        try {
            group = ldapTemplate.searchForObject(query, mapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            group = null;
        }
        if (withMembers) {
            for (IGroupMemberDesigner gpDesigner: groupMemberDesigners) {
                group = gpDesigner.designe(group, this);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("LDAP group found : {}", group);
        }
        return group;
    }

    @Override
    //@Cacheable(value = "ExternalGroups", key = "#id")
    public List<IExternalGroup> getGroupsById(@NotNull final Collection<String> ids, final boolean withMembers) {
        if (ids == null || ids.isEmpty())
            return Lists.newArrayList();
        AndFilter filter = new AndFilter();
        OrFilter list = new OrFilter();
        for (String id: ids) {
            list.append(new EqualsFilter(externalGroupHelper.getGroupIdAttribute(), id));
        }
        filter.append(list);
        if (log.isDebugEnabled()) {
            log.debug("LDAP filter applied {} and looking for members {} ", filter, withMembers);
        }
        ContextMapper<IExternalGroup> mapper;
        if (withMembers) {
            mapper = new LdapGroupContextMapper(this.externalGroupHelper, this.groupDisplayNameFormatters);
        } else {
            mapper = new LdapGroupWithoutMembersContextMapper(this.externalGroupHelper, this.groupDisplayNameFormatters);
        }
        // SearchControls constraints = new SearchControls();
        // constraints.setReturningAttributes((String[])
        // ldapUserHelper.getAttributes().toArray());
        LdapQuery query = LdapQueryBuilder.query()
            .base(externalGroupHelper.getGroupDNSubPath()).filter(filter);
        List<IExternalGroup> groups = ldapTemplate.search(query, mapper);
        if (withMembers) {
            List<IExternalGroup> tmp = Lists.newArrayList();
            for (IExternalGroup group : groups) {
                for (IGroupMemberDesigner gpDesigner: groupMemberDesigners) {
                    tmp.add(gpDesigner.designe(group, this));
                }
            }
            groups = tmp;
        }
        return groups;
    }

    @Override
    //@Cacheable(value = "ExternalGroups", key = "#id")
    public List<IExternalGroup> getGroupsByIdStartWith(@NotNull final Collection<String> ids, final boolean withMembers) {
        if (ids == null || ids.isEmpty())
            return Lists.newArrayList();
        AndFilter filter = new AndFilter();
        OrFilter list = new OrFilter();
        for (String id: ids) {
            list.append(new EqualsFilter(externalGroupHelper.getGroupIdAttribute(), id));
            list.append(new LikeFilter(externalGroupHelper.getGroupIdAttribute(), id.trim() + ":*"));
        }
        filter.append(list);
        if (log.isDebugEnabled()) {
            log.debug("LDAP filter applied {} and looking for members {} ", filter, withMembers);
        }
        ContextMapper<IExternalGroup> mapper;
        if (withMembers) {
            mapper = new LdapGroupContextMapper(this.externalGroupHelper, this.groupDisplayNameFormatters);
        } else {
            mapper = new LdapGroupWithoutMembersContextMapper(this.externalGroupHelper, this.groupDisplayNameFormatters);
        }
        // SearchControls constraints = new SearchControls();
        // constraints.setReturningAttributes((String[])
        // ldapUserHelper.getAttributes().toArray());
        LdapQuery query = LdapQueryBuilder.query()
            .base(externalGroupHelper.getGroupDNSubPath()).filter(filter);
        List<IExternalGroup> groups = ldapTemplate.search(query, mapper);
        if (withMembers) {
            List<IExternalGroup> tmp = Lists.newArrayList();
            for (IExternalGroup group : groups) {
                for (IGroupMemberDesigner gpDesigner: groupMemberDesigners) {
                    tmp.add(gpDesigner.designe(group, this));
                }
            }
            groups = tmp;
        }
        return groups;
    }

    @Override
    public List<IExternalGroup> getDirectGroupMembers(@NotNull final String id, final boolean withMembers) {
        IExternalGroup group = getGroupById(id, true);
        if (group != null && group.hasMembers()) {
            return getGroupsById(group.getGroupMembers(), withMembers);
        }
        return Lists.newArrayList();
    }

    @Override
    // @Cacheable(value = "ExternalGroups")
    public List<IExternalGroup> getGroupsWithFilter(@NotNull String stringFilter, String token, final boolean withMembers) {
        log.debug("Search with filter {}, token {}, withMembers {}", stringFilter, token, withMembers);
        AndFilter filter = new AndFilter()
            .and(new HardcodedFilter(stringFilter));
        if (token != null && !token.isEmpty())
            filter.and(new WhitespaceWildcardsFilter(externalGroupHelper.getGroupSearchAttribute(), token));

        List<IExternalGroup> groups = searchWithFilter(filter, withMembers);
        if (withMembers) {
            List<IExternalGroup> tmp = Lists.newArrayList();
            for (IExternalGroup group : groups) {
                for (IGroupMemberDesigner gpDesigner: groupMemberDesigners) {
                    tmp.add(gpDesigner.designe(group, this));
                }
            }
            groups = tmp;
        }
        return groups;
    }

    @Override
    public List<IExternalUser> getDirectUserMembers(@NotNull final String id) {
        if (!externalGroupHelper.isGroupResolveUserMember()) return Lists.newArrayList();
        if (externalGroupHelper.isGroupResolveUserMemberByUserAttributes()) {
            return externalUserDao.getUsersByGroupId(id);
        }
        //else
        IExternalGroup group = getGroupById(id, true);
        if (group != null && group.hasMembers() && !group.getUserMembers().isEmpty())
            return externalUserDao.getUsersByUids(group.getUserMembers());
        return Lists.newArrayList();
    }

    @Override
    public boolean isGroupMemberOfGroup(@NotNull final String member, @NotNull final String parent) {
        if (externalGroupHelper.getGroupKeyMemberRegex() == null) return false;
        // direct resolution when using grouper subpath is in parent path, so do only char comparison.
        if (member.startsWith(parent)) return true;
        // else if not subpath checking in ldap
        AndFilter filter = new AndFilter().and(new LikeFilter(externalGroupHelper.getGroupSearchAttribute(), parent + "*"));
        filter.and(new EqualsFilter(externalGroupHelper.getGroupMembersAttribute(), externalGroupHelper.getGroupKeyMemberRegex().toString().replace("(.*)", member)));
        if (log.isDebugEnabled()) {
            log.debug("isGroupMemberOfGroup LDAP filter applied : {}", filter);
        }
        return !searchWithFilter(filter, false).isEmpty();
    }

    @Override
    public boolean isGroupMemberOfGroupFilter(@NotNull String stringFilter, @NotNull final String member) {
        log.debug("Search with filter {} and group {}", stringFilter, member);
        AndFilter filter = new AndFilter()
            .and(new HardcodedFilter(stringFilter));
        OrFilter or = new OrFilter();
        or.append(new EqualsFilter(externalGroupHelper.getGroupMembersAttribute(), externalGroupHelper.getGroupKeyMemberRegex().toString().replace("(.*)", member)));
        or.append(new EqualsFilter(externalGroupHelper.getGroupSearchAttribute(), member));
        filter.and(or);

        return !searchWithFilter(filter, false).isEmpty();

    }

    @Override
    public boolean isUserMemberOfGroup(@NotNull final String uid, @NotNull final String group) {
        if (externalGroupHelper.getUserKeyMemberRegex() == null) return false;
        AndFilter filter = new AndFilter().and(new LikeFilter(externalGroupHelper.getGroupSearchAttribute(), group + "*"));
        filter.and(new EqualsFilter(externalGroupHelper.getGroupMembersAttribute(), externalGroupHelper.getUserKeyMemberRegex().toString().replace("(.*)", uid)));
        if (log.isDebugEnabled()) {
            log.debug("isUserMemberOfGroup LDAP filter applied : {}", filter);
        }
        return !searchWithFilter(filter, false).isEmpty();
    }

    @Override
    public boolean isGroupMemberOfAtLeastOneGroup(@NotNull final String member, @NotNull final Iterable<String> parents) {
        if (externalGroupHelper.getGroupKeyMemberRegex() == null) return false;
        // direct resolution when using grouper subpath is in parent path, so do only char comparison.
        if (OptimContainsGroup(member, parents)) return true;
        // else if not subpath checking in ldap
        OrFilter orFilter = new OrFilter();
        for (final String group : parents) {
            orFilter.append(new LikeFilter(externalGroupHelper.getGroupSearchAttribute(), group + "*"));
        }
        AndFilter filter = new AndFilter().and(orFilter);
        filter.and(new EqualsFilter(externalGroupHelper.getGroupMembersAttribute(), externalGroupHelper.getGroupKeyMemberRegex().toString().replace("(.*)", member)));
        if (log.isDebugEnabled()) {
            log.debug("isGroupMemberOfGroup LDAP filter applied : {}", filter);
        }
        return !searchWithFilter(filter, false).isEmpty();
    }

    @Override
    public boolean isUserMemberOfAtLeastOneGroup(@NotNull final String uid, @NotNull final Iterable<String> groups) {
        if (externalGroupHelper.getUserKeyMemberRegex() == null) return false;
        OrFilter orFilter = new OrFilter();
        for (final String group : groups) {
            orFilter.append(new LikeFilter(externalGroupHelper.getGroupSearchAttribute(), group + "*"));
        }
        AndFilter filter = new AndFilter().and(orFilter);
        filter.and(new EqualsFilter(externalGroupHelper.getGroupMembersAttribute(), externalGroupHelper.getUserKeyMemberRegex().toString().replace("(.*)", uid)));
        if (log.isDebugEnabled()) {
            log.debug("isUserMemberOfAtLeastOneGroup LDAP filter applied : {}", filter);
        }
        return !searchWithFilter(filter, false).isEmpty();
    }

    // @Cacheable(value = "ExternalGroups", key = "#filter")
    private List<IExternalGroup> searchWithFilter(@NotNull final Filter filter, final boolean withMembers) {
        final String filterAsStr = filter.encode();
        if (log.isDebugEnabled()) {
            log.debug("LDAP filter applied : {}", filterAsStr);
        }
        ContextMapper<IExternalGroup> mapper;
        if (withMembers) {
            mapper = new LdapGroupContextMapper(this.externalGroupHelper, this.groupDisplayNameFormatters);
        } else {
            mapper = new LdapGroupWithoutMembersContextMapper(this.externalGroupHelper, this.groupDisplayNameFormatters);
        }
        // SearchControls constraints = new SearchControls();
        // constraints.setReturningAttributes((String[])
        // ldapUserHelper.getAttributes().toArray());
        LdapQuery query = LdapQueryBuilder.query()
            .base(externalGroupHelper.getGroupDNSubPath()).filter(filter);
        return ldapTemplate.search(query, mapper);
    }

    private boolean OptimContainsGroup(@NotNull final String member, @NotNull final Iterable<String> parents) {
        for (String group : parents) {
            if (member.startsWith(group)) return true;
        }
        return false;
    }

    /*private OrFilter orFilterOnIds(@NotNull final Iterable<String> ids) {
        // needed since empty OrFilter() is true instead of false
        // (https://jira.springsource.org/browse/LDAP-226)
        if (Iterables.isEmpty(ids))
            return null;

        final OrFilter filter = new OrFilter();
        for (String id : ids) {
            filter.or(new EqualsFilter(externalGroupHelper.getGroupIdAttribute(),id));
        }
        return filter;
    }*/

}
