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

import org.esupportail.publisher.config.bean.CustomLdapProperties;
import org.esupportail.publisher.config.bean.GroupDesignerProperties;
import org.esupportail.publisher.config.bean.GroupRegexProperties;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
import org.esupportail.publisher.domain.externals.ExternalUserHelper;
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

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.util.Assert;

/**
 *
 * @author GIP RECIA - Julien Gribonvald 10 juil. 2014
 */
@Configuration
@Slf4j
public class LDAPConfiguration {

    private final CustomLdapProperties ldapProperties;

    public LDAPConfiguration(ESUPPublisherProperties esupPublisherProperties) {
        this.ldapProperties = esupPublisherProperties.getLdap();
    }

    @Bean
    public LdapContextSource contextSource() {
        log.debug("Configuring LdapContextSource");
        final LdapContextSource ldapCtx = new LdapContextSource();
        ldapCtx.setAnonymousReadOnly(ldapProperties.getContextSource().isAnonymousReadOnly());
        ldapCtx.setBase(ldapProperties.getContextSource().getBase());
        ldapCtx.setUrls(ldapProperties.getContextSource().getUrls());
        ldapCtx.setUserDn(ldapProperties.getContextSource().getUsername());
        ldapCtx.setPassword(ldapProperties.getContextSource().getPassword());
        ldapCtx.setPooled(ldapProperties.getContextSource().isNativePooling());
        log.debug("LDAPContext is configured with properties {}", ldapProperties.getContextSource());
        return ldapCtx;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        final LdapTemplate ldapTpl = new LdapTemplate();
        ldapTpl.setContextSource(contextSource());
        ldapTpl.setDefaultCountLimit(ldapProperties.getLdapTemplate().getCountLimit());
        ldapTpl.setDefaultTimeLimit(ldapProperties.getLdapTemplate().getTimeLimit());
        ldapTpl.setDefaultSearchScope(ldapProperties.getLdapTemplate().getSearchScope());
        ldapTpl.setIgnoreNameNotFoundException(ldapProperties.getLdapTemplate().isIgnoreNameNotFoundException());
        ldapTpl.setIgnorePartialResultException(ldapProperties.getLdapTemplate().isIgnorePartialResultException());
        ldapTpl.setIgnoreSizeLimitExceededException(ldapProperties.getLdapTemplate().isIgnoreSizeLimitExceededException());
        return ldapTpl;
    }

    @Bean
    public ExternalUserHelper externalUserHelper() {
        final ExternalUserHelper ldapUh = new ExternalUserHelper(
                ldapProperties.getUserBranch().getIdAttribute(),
                ldapProperties.getUserBranch().getDisplayNameAttribute(),
                ldapProperties.getUserBranch().getMailAttribute(),
                ldapProperties.getUserBranch().getSearchAttribute(),
                ldapProperties.getUserBranch().getGroupAttribute(),
                ldapProperties.getUserBranch().getOtherBackendAttributes(),
                ldapProperties.getUserBranch().getOtherDisplayedAttributes(),
                ldapProperties.getUserBranch().getBaseDN());
        log.debug("LdapAttributes for Users configured : {}", ldapUh);
        return ldapUh;
    }

    @Bean
    @Profile(Constants.SPRING_PROFILE_LDAP_GROUP)
    public ExternalGroupHelper externalGroupHelper() {
        log.debug("Configure bean ExternalGroupHelper with LDAP attributes");
        Assert.notNull(ldapProperties.getGroupBranch(), "Use of profile " + Constants.SPRING_PROFILE_LDAP_GROUP + " require 'app.ldap.groupBranch.*' properties configured !");
        ExternalGroupHelper ldapUh = new ExternalGroupHelper(
                ldapProperties.getGroupBranch().getIdAttribute(),
                ldapProperties.getGroupBranch().getDisplayNameAttribute(),
                ldapProperties.getGroupBranch().getSearchAttribute(),
                ldapProperties.getGroupBranch().getGroupAttribute(),
                ldapProperties.getGroupBranch().getGroupMemberKeyPattern(),
                ldapProperties.getGroupBranch().getGroupMemberKeyPatternIndex(),
                ldapProperties.getGroupBranch().getUserMemberKeyPattern(),
                ldapProperties.getGroupBranch().getUserMemberKeyPatternIndex(),
                ldapProperties.getGroupBranch().getGroupDisplayNamePattern(),
                ldapProperties.getGroupBranch().isDNContainsDisplayName(),
                ldapProperties.getGroupBranch().isResolveUserMembers(),
                ldapProperties.getGroupBranch().isResolveUserMembersByUserAttributes(),
                ldapProperties.getGroupBranch().getDontResolveMembersWithGroupPattern(),
                ldapProperties.getGroupBranch().getOtherDisplayedAttributes(),
                ldapProperties.getGroupBranch().getBaseDN());
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
    public IExternalGroupDao ldapExternalGroupDao(IExternalUserDao externalUserDao, LdapTemplate ldapTemplate) {
        log.debug("Configuring IExternalGroupDao with LDAP DAO");
        Assert.notNull(ldapProperties.getGroupBranch(), "Use of profile " + Constants.SPRING_PROFILE_LDAP_GROUP + " require 'app.ldap.groupBranch.*' properties configured !");
        List<IExternalGroupDisplayNameFormatter> formatters = Lists.newLinkedList();
        // should be run firstly !
        formatters.add(new LdapGroupRegexpDisplayNameFormatter(externalGroupHelper()));
        for (GroupRegexProperties grp: ldapProperties.getGroupBranch().getNameFormatters()) {
            formatters.add(new LdapGroupRegexpDisplayNameFormatterESCO(externalGroupHelper(), grp.getGroupMatcher(),
                    grp.getGroupNameRegex(), grp.getGroupNameIndex(), grp.getGroupRecomposerSeparator(), grp.getGroupSuffixeToAppend()));
        }
        //should be run at final
        formatters.add(new LdapGroupRegexpDisplayNameFormatterESCOReplace());

        List<IGroupMemberDesigner> designers = Lists.newArrayList();
        for (GroupDesignerProperties grp: ldapProperties.getGroupBranch().getDesigners()) {
            designers.add(new LdapGroupAttachMemberDesignerImpl(externalGroupHelper(), grp.getGroupRootPattern(),
                    grp.getGroupAttachEndMatch(), grp.getGroupToAttachEndPattern()));
        }
        return new LdapGroupDaoImpl(ldapTemplate, externalGroupHelper(), formatters, externalUserDao, designers);
    }

    @Bean
    //@ConditionalOnMissingClass("org.esupportail.publisher.repository.externals.ldap.LdapGroupDaoImpl")
    @Profile("!" + Constants.SPRING_PROFILE_LDAP_GROUP)
    public IExternalGroupDao emptyExternalGroupDao() {
        log.debug("Configuring emtpy IExternalGroupDao");
        return new EmptyGroupDaoImpl();
    }

    @Bean
    //@ConditionalOnMissingClass("org.esupportail.publisher.repository.externals.ldap.LdapGroupDaoImpl")
    @Profile("!" + Constants.SPRING_PROFILE_LDAP_GROUP)
    public IGroupService emptyGroupService() {
        return new GroupServiceEmpty();
    }

    @Bean
    @Profile(Constants.SPRING_PROFILE_LDAP_GROUP)
    public IGroupService ldapGroupService(IPermissionService permissionService, TreeJSDTOFactory treeJSDTOFactory, UserDTOFactory userDTOFactory,
                                          SubscriberService subscriberService, FilterRepository filterRepository, ContextService contextService, IExternalGroupDao externalGroupDao) {
        return new GroupService(permissionService, treeJSDTOFactory, userDTOFactory, externalGroupDao, subscriberService, filterRepository, contextService);
    }
}