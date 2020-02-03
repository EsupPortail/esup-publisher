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
package org.esupportail.publisher.config;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.config.bean.GroupDesignerProperties;
import org.esupportail.publisher.config.bean.GroupRegexProperties;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
import org.esupportail.publisher.domain.externals.IExternalGroupDisplayNameFormatter;
import org.esupportail.publisher.repository.FilterRepository;
import org.esupportail.publisher.repository.externals.EmptyGroupDaoImpl;
import org.esupportail.publisher.repository.externals.IExternalGroupDao;
import org.esupportail.publisher.repository.externals.IExternalUserDao;
import org.esupportail.publisher.repository.externals.IGroupMemberDesigner;
import org.esupportail.publisher.repository.externals.ldap.LdapGroupAttachMemberDesignerImpl;
import org.esupportail.publisher.repository.externals.ldap.LdapGroupDaoImpl;
import org.esupportail.publisher.repository.externals.ldap.LdapGroupRegexpDisplayNameFormatter;
import org.esupportail.publisher.repository.externals.ldap.LdapGroupRegexpDisplayNameFormatterESCO;
import org.esupportail.publisher.repository.externals.ldap.LdapGroupRegexpDisplayNameFormatterESCOReplace;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.ContextService;
import org.esupportail.publisher.service.GroupService;
import org.esupportail.publisher.service.GroupServiceEmpty;
import org.esupportail.publisher.service.IGroupService;
import org.esupportail.publisher.service.SubscriberService;
import org.esupportail.publisher.service.factories.TreeJSDTOFactory;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 *
 * @author GIP RECIA - Julien Gribonvald 10 juil. 2014
 */
@Configuration
@Slf4j
public class GroupConfiguration {

    private static final String PROP_GROUPDN_SUBPATH = "ldap.DNsubpath.group";
    private static final String DEFAULT_GROUPDN_SUBPATH = "ou=groups";
    private static final String PROP_GROUP_ID = "ldap.groupIdAttribute";
    private static final String DEFAULT_GROUP_ID = "cn";
    private static final String PROP_GROUP_DISPLAYNAME = "ldap.groupDisplayNameAttribute";
    private static final String DEFAULT_GROUP_DISPLAYNAME = "cn";
    private static final String PROP_GROUP_MEMBERS = "ldap.groupMembersAttribute";
    private static final String DEFAULT_GROUP_MEMBERS = "member";
    private static final String PROP_GROUP_SEARCHON = "ldap.groupSearchAttribute";
    private static final String DEFAULT_GROUP_SEARCHON = "cn";
    private static final String PROP_GROUP_DISPLAYEDATTR = "ldap.otherGroupDisplayedAttributes";
    private static final String PROP_GROUP_GROUPMEMBER_REGEX = "ldap.groupKeyMemberRegex";
    private static final String PROP_GROUP_GROUPMEMBER_INDEX = "ldap.groupKeyMemberIndex";
    private static final String PROP_GROUP_USERMEMBER_REGEX = "ldap.userKeyMemberRegex";
    private static final String PROP_GROUP_USERMEMBER_INDEX = "ldap.userKeyMemberIndex";
    private static final String PROP_GROUP_DISPLAYNAME_REGEX = "ldap.groupDisplayNameRegex";
    private static final String PROP_GROUP_DISPLAYNAME_IN_DN = "ldap.groupDNContainsDisplayName";
    private static final String PROP_GROUP_RESOLVE_USERMEMBER = "ldap.groupResolveUserMember";
    private static final String PROP_GROUP_RESOLVE_USERMEMBER_BYUSER = "ldap.groupResolveUserMemberByUserAttributes";

    @Inject
    public LdapContextSource contextSource;

    @Inject
    public LdapTemplate ldapTemplate;

    @Inject
    public IExternalUserDao externalUserDao;

    @Configuration
    static class init {

        @Inject
        private Environment environment;

        @Inject
        public LdapTemplate ldapTemplate;

        @Inject
        public IExternalUserDao externalUserDao;

        @Inject
        public GroupRegexConfiguration groupRegexConfiguration;

        @Inject
        public GroupDesignerConfiguration groupDesignerConfiguration;


        @Bean
        @Profile(Constants.SPRING_PROFILE_LDAP_GROUP)
        public ExternalGroupHelper externalGroupHelper() {
            log.debug("Configure bean ExternalGroupHelper with LDAP attributes");
//            this.environment.containsProperty(ENV_LDAP)
            final String groupIdAttribute = environment.getProperty(PROP_GROUP_ID, DEFAULT_GROUP_ID);
            final String groupDisplayNameAttribute = environment.getProperty(PROP_GROUP_DISPLAYNAME, DEFAULT_GROUP_DISPLAYNAME);
            final String groupSearchAttribute = environment.getProperty(PROP_GROUP_SEARCHON, DEFAULT_GROUP_SEARCHON);
            final String groupMembersAttribute = environment.getProperty(PROP_GROUP_MEMBERS, DEFAULT_GROUP_MEMBERS);
            final String groupDisplayedAttributes = environment.getProperty(PROP_GROUP_DISPLAYEDATTR);
            Set<String> otherGroupDisplayedAttributes = Sets.newHashSet();
            if (groupDisplayedAttributes != null && !groupDisplayedAttributes.isEmpty()) {
                String[] attrs = groupDisplayedAttributes.trim().replaceAll("\\s", "").split(",");
                otherGroupDisplayedAttributes = Sets.newHashSet(attrs);
            }
            final String groupDNSubPath = environment.getProperty(PROP_GROUPDN_SUBPATH, DEFAULT_GROUPDN_SUBPATH);

            final String pGroupKeyMemberRegex = environment.getRequiredProperty(PROP_GROUP_GROUPMEMBER_REGEX);
            final Pattern groupKeyMemberRegex = Pattern.compile(pGroupKeyMemberRegex);
            final int groupKeyMemberIndex = environment.getProperty(PROP_GROUP_GROUPMEMBER_INDEX, Integer.class, 0);

            final String pUserKeyMemberRegex = environment.getRequiredProperty(PROP_GROUP_USERMEMBER_REGEX);
            final Pattern userKeyMemberRegex = Pattern.compile(pUserKeyMemberRegex);
            final int userKeyMemberIndex = environment.getProperty(PROP_GROUP_USERMEMBER_INDEX, Integer.class, 0);

            final String pGroupDisplayNameRegex = environment.getProperty(PROP_GROUP_DISPLAYNAME_REGEX);
            final Pattern groupDisplayNameRegex =
                pGroupDisplayNameRegex != null && !pGroupDisplayNameRegex.isEmpty() ?
                    Pattern.compile(pGroupDisplayNameRegex) : null;

            final boolean groupDNContainsDisplayName = environment.getProperty(PROP_GROUP_DISPLAYNAME_IN_DN, Boolean.class, false);
            final boolean groupResolveUserMember = environment.getProperty(PROP_GROUP_RESOLVE_USERMEMBER, Boolean.class, false);
            final boolean groupResolveUserMemberByUserAttributes = environment.getProperty(PROP_GROUP_RESOLVE_USERMEMBER_BYUSER, Boolean.class, true);

            ExternalGroupHelper ldapUh = new ExternalGroupHelper(groupIdAttribute, groupDisplayNameAttribute,
                groupSearchAttribute, groupMembersAttribute, groupKeyMemberRegex, groupKeyMemberIndex, userKeyMemberRegex,
                userKeyMemberIndex, groupDisplayNameRegex, groupDNContainsDisplayName, groupResolveUserMember,
                groupResolveUserMemberByUserAttributes, otherGroupDisplayedAttributes, groupDNSubPath);
            log.debug("LdapAttributes for Groups configured : {}", ldapUh);
            return ldapUh;
        }

        @Bean
        @Profile("!" + Constants.SPRING_PROFILE_LDAP_GROUP)
        public ExternalGroupHelper emptyExternalGroupHelper() {
            log.debug("Configure bean ExternalGroupHelper without attributes");
            return new ExternalGroupHelper();
        }

        @Bean
        @Profile(Constants.SPRING_PROFILE_LDAP_GROUP)
        public IExternalGroupDao ldapExternalGroupDao() {
            log.debug("Configuring IExternalGroupDao with LDAP DAO");
            List<IExternalGroupDisplayNameFormatter> formatters = Lists.newLinkedList();
            // should be run firstly !
            formatters.add(new LdapGroupRegexpDisplayNameFormatter(externalGroupHelper()));
            for (GroupRegexProperties grp: groupRegexConfiguration.getProperties()) {
                formatters.add(new LdapGroupRegexpDisplayNameFormatterESCO(externalGroupHelper(), grp.getGroupMatcher(),
                    grp.getGroupNameRegex(), grp.getGroupNameIndex(), grp.getGroupRecomposerSeparator()));
            }
            //should be run at final
            formatters.add(new LdapGroupRegexpDisplayNameFormatterESCOReplace());

            List<IGroupMemberDesigner> designers = Lists.newArrayList();
            for (GroupDesignerProperties grp: groupDesignerConfiguration.getProperties()) {
                 designers.add(new LdapGroupAttachMemberDesignerImpl(externalGroupHelper(), grp.getGroupRootPattern(),
                     grp.getGroupAttachEndMatch(), grp.getGroupToAttachEndPattern()));
            }
            return new LdapGroupDaoImpl(ldapTemplate, externalGroupHelper(), formatters, externalUserDao, designers);
        }

        @Bean
        @ConditionalOnMissingClass("org.esupportail.publisher.repository.externals.ldap.LdapGroupDaoImpl")
        public IExternalGroupDao emptyExternalGroupDao() {
            log.debug("Configuring emtpy IExternalGroupDao");
            return new EmptyGroupDaoImpl();
        }
    }

    @Inject
    private IPermissionService permissionService;

    @Inject
    private TreeJSDTOFactory treeJSDTOFactory;

    @Inject
    private UserDTOFactory userDTOFactory;

    @Inject
    private SubscriberService subscriberService;

    @Inject
    private FilterRepository filterRepository;

    @Inject
    private ContextService contextService;

    @Inject
    private IExternalGroupDao externalGroupDao;

    @Bean
    @ConditionalOnMissingClass("org.esupportail.publisher.repository.externals.ldap.LdapGroupDaoImpl")
    public IGroupService emptyGroupService() {
        return new GroupServiceEmpty();
    }

    @Bean
    @Profile(Constants.SPRING_PROFILE_LDAP_GROUP)
    public IGroupService ldapGroupService() {
        return new GroupService(permissionService, treeJSDTOFactory, userDTOFactory, externalGroupDao, subscriberService, filterRepository, contextService);
    }
}
