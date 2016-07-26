package org.esupportail.publisher.config;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.portal.ws.client.PortalService;
import org.esupportail.portal.ws.client.support.uportal.BasicUportalServiceImpl;
import org.esupportail.publisher.domain.externals.EmptyExternalGroupDisplayNameFormatter;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
import org.esupportail.publisher.domain.externals.IExternalGroupDisplayNameFormatter;
import org.esupportail.publisher.repository.FilterRepository;
import org.esupportail.publisher.repository.externals.*;
import org.esupportail.publisher.repository.externals.ldap.LdapGroupDisplayNameFormatterESCO;
import org.esupportail.publisher.repository.externals.ldap.LdapGroupDaoImpl;
import org.esupportail.publisher.repository.externals.ldap.LdapGroupMemberDesignerImpl;
import org.esupportail.publisher.repository.externals.ws.WSEsupGroupDaoImpl;
import org.esupportail.publisher.repository.externals.ws.WSExternalGroupDisplayNameFormatter;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.*;
import org.esupportail.publisher.service.factories.TreeJSDTOFactory;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.service.SubscriberService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * @author GIP RECIA - Julien Gribonvald 10 juil. 2014
 */
@Configuration
@Slf4j
public class GroupConfiguration {

    private static final String ENV_LDAP = "ldap.";
    private static final String PROP_GROUPDN_SUBPATH = "DNsubpath.group";
    private static final String DEFAULT_GROUPDN_SUBPATH = "ou=groups";
    private static final String PROP_GROUP_ID = "groupIdAttribute";
    private static final String DEFAULT_GROUP_ID = "cn";
    private static final String PROP_GROUP_DISPLAYNAME = "groupDisplayNameAttribute";
    private static final String DEFAULT_GROUP_DISPLAYNAME = "cn";
    private static final String PROP_GROUP_MEMBERS = "groupMembersAttribute";
    private static final String DEFAULT_GROUP_MEMBERS = "member";
    private static final String PROP_GROUP_SEARCHON = "groupSearchAttribute";
    private static final String DEFAULT_GROUP_SEARCHON = "cn";
    private static final String PROP_GROUP_DISPLAYEDATTR = "otherGroupDisplayedAttributes";
    private static final String PROP_GROUP_GROUPMEMBER_REGEX = "groupKeyMemberRegex";
    private static final String PROP_GROUP_GROUPMEMBER_INDEX = "groupKeyMemberIndex";
    private static final String PROP_GROUP_USERMEMBER_REGEX = "userKeyMemberRegex";
    private static final String PROP_GROUP_USERMEMBER_INDEX = "userKeyMemberIndex";
    private static final String PROP_GROUP_DISPLAYNAME_REGEX = "groupDisplayNameRegex";
    private static final String PROP_GROUP_DISPLAYNAME_IN_DN = "groupDNContainsDisplayName";
    private static final String PROP_GROUP_RESOLVE_USERMEMBER = "groupResolveUserMember";
    private static final String PROP_GROUP_RESOLVE_USERMEMBER_BYUSER = "groupResolveUserMemberByUserAttributes";

    private static final String ENV_WS = "ws.esup.";
    private static final String PROP_URL = "url";

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

        @Bean
        @Profile(Constants.SPRING_PROFILE_LDAP_GROUP)
        public ExternalGroupHelper externalGroupHelper() {
            log.debug("Configure bean ExternalGroupHelper with LDAP attributes");

            RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, ENV_LDAP);
            final String groupIdAttribute = propertyResolver.getProperty(PROP_GROUP_ID, DEFAULT_GROUP_ID);
            final String groupDisplayNameAttribute = propertyResolver.getProperty(PROP_GROUP_DISPLAYNAME, DEFAULT_GROUP_DISPLAYNAME);
            final String groupSearchAttribute = propertyResolver.getProperty(PROP_GROUP_SEARCHON, DEFAULT_GROUP_SEARCHON);
            final String groupMembersAttribute = propertyResolver.getProperty(PROP_GROUP_MEMBERS, DEFAULT_GROUP_MEMBERS);
            final String groupDisplayedAttributes = propertyResolver.getProperty(PROP_GROUP_DISPLAYEDATTR);
            Set<String> otherGroupDisplayedAttributes = Sets.newHashSet();
            if (groupDisplayedAttributes != null && !groupDisplayedAttributes.isEmpty()) {
                String[] attrs = groupDisplayedAttributes.trim().replaceAll("\\s", "").split(",");
                otherGroupDisplayedAttributes = Sets.newHashSet(attrs);
            }
            final String groupDNSubPath = propertyResolver.getProperty(PROP_GROUPDN_SUBPATH, DEFAULT_GROUPDN_SUBPATH);

            final String pGroupKeyMemberRegex = propertyResolver.getRequiredProperty(PROP_GROUP_GROUPMEMBER_REGEX);
            final Pattern groupKeyMemberRegex = Pattern.compile(pGroupKeyMemberRegex);
            final int groupKeyMemberIndex = propertyResolver.getProperty(PROP_GROUP_GROUPMEMBER_INDEX, Integer.class, 0);

            final String pUserKeyMemberRegex = propertyResolver.getRequiredProperty(PROP_GROUP_USERMEMBER_REGEX);
            final Pattern userKeyMemberRegex = Pattern.compile(pUserKeyMemberRegex);
            final int userKeyMemberIndex = propertyResolver.getProperty(PROP_GROUP_USERMEMBER_INDEX, Integer.class, 0);

            final String pGroupDisplayNameRegex = propertyResolver.getProperty(PROP_GROUP_DISPLAYNAME_REGEX);
            final Pattern groupDisplayNameRegex =
                pGroupDisplayNameRegex != null && !pGroupDisplayNameRegex.isEmpty() ?
                    Pattern.compile(pGroupDisplayNameRegex) : null;

            final boolean groupDNContainsDisplayName = propertyResolver.getProperty(PROP_GROUP_DISPLAYNAME_IN_DN, Boolean.class, false);
            final boolean groupResolveUserMember = propertyResolver.getProperty(PROP_GROUP_RESOLVE_USERMEMBER, Boolean.class, false);
            final boolean groupResolveUserMemberByUserAttributes = propertyResolver.getProperty(PROP_GROUP_RESOLVE_USERMEMBER_BYUSER, Boolean.class, true);

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
        @Profile(Constants.SPRING_PROFILE_WS_GROUP)
        public PortalService portalService() {
            log.debug("Configuring PortalService");

            RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, ENV_WS);
            final BasicUportalServiceImpl portalService = new BasicUportalServiceImpl();
            String url = propertyResolver.getProperty(PROP_URL);

            if (url == null) {
                log.error(
                    "Your WS Portal Service connection configuration is incorrect! The application"
                        + "cannot start. Please check your properties current, profiles are: {}",
                    Arrays.toString(environment.getActiveProfiles()));

                throw new ApplicationContextException(
                    "WS Portal Service is not configured correctly");
            }
            portalService.setUrl(url);
            try {
                portalService.getRootGroup();
            } catch (RuntimeException re) {
                log.error(
                    "Your WS Portal Service connection configuration is incorrect! The application"
                        + "cannot start. Please check your properties, current url property is: {}",
                    url);

                throw new ApplicationContextException(
                    "WS Portal Service is not configured correctly");
            }
            return portalService;
        }

        @Bean
        @Profile(Constants.SPRING_PROFILE_LDAP_GROUP)
        public IExternalGroupDisplayNameFormatter ldapExternalGroupDisplayNameFormatter() {
            RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, ENV_LDAP);
            String applicationGroupMatcher = propertyResolver.getProperty("escoGroupApplicationMatcher");
            String applicationGroupNameRegex = propertyResolver.getProperty("escoGroupApplicationNameRegex");
            String applicationGroupNameIndex = propertyResolver.getProperty("escoGroupApplicationNameIndex");

            return new LdapGroupDisplayNameFormatterESCO(externalGroupHelper(), applicationGroupMatcher, applicationGroupNameRegex, applicationGroupNameIndex);
        }

        @Bean
        @ConditionalOnMissingClass({LdapGroupDaoImpl.class,WSEsupGroupDaoImpl.class})
        public IExternalGroupDisplayNameFormatter externalGroupDisplayNameFormatter() {
            return new EmptyExternalGroupDisplayNameFormatter();
        }

        @Bean
        @Profile(Constants.SPRING_PROFILE_WS_GROUP)
        public IExternalGroupDisplayNameFormatter wsExternalGroupDisplayNameFormatter() {
            return new WSExternalGroupDisplayNameFormatter();
        }

        @Inject
        private IExternalGroupDisplayNameFormatter externalGroupDisplayNameFormatter;

        @Inject
        private IGroupMemberDesigner groupMemberDesigner;


        @Bean
        @Profile(Constants.SPRING_PROFILE_LDAP_GROUP)
        public IExternalGroupDao ldapExternalGroupDao() {
            log.debug("Configuring IExternalGroupDao with LDAP DAO");
            return new LdapGroupDaoImpl(ldapTemplate, externalGroupHelper(), externalGroupDisplayNameFormatter, externalUserDao, groupMemberDesigner);
        }

        @Bean
        @Profile(Constants.SPRING_PROFILE_WS_GROUP)
        public IExternalGroupDao wsExternalGroupDao() {
            log.debug("Configuring IExternalGroupDao with WS ESUP DAO");
            return new WSEsupGroupDaoImpl(portalService(), externalGroupDisplayNameFormatter);
        }

        @Bean
        @ConditionalOnMissingClass({LdapGroupDaoImpl.class, WSEsupGroupDaoImpl.class})
        public IExternalGroupDao emptyExternalGroupDao() {
            log.debug("Configuring emtpy IExternalGroupDao");
            return new EmptyGroupDaoImpl();
        }

        @Bean
        @Profile(Constants.SPRING_PROFILE_LDAP_GROUP)
        public IGroupMemberDesigner ldapGroupMemberDesigner() {
            return new LdapGroupMemberDesignerImpl(externalGroupHelper());
        }

        @Bean
        @ConditionalOnMissingClass({LdapGroupDaoImpl.class,WSEsupGroupDaoImpl.class})
        public IGroupMemberDesigner emptyGroupMemberDesigner() {
            return new EmptyGroupMemberDesignerImpl();
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
    @Profile(Constants.SPRING_PROFILE_WS_GROUP)
    public IGroupService wsGroupService() {
        return new GroupServiceWS(permissionService,treeJSDTOFactory, userDTOFactory, externalGroupDao, subscriberService, filterRepository, contextService);
    }

    @Bean
    @ConditionalOnMissingClass({LdapGroupDaoImpl.class,WSEsupGroupDaoImpl.class})
    public IGroupService emptyGroupService() {
        return new GroupServiceEmpty();
    }

    @Bean
    @Profile(Constants.SPRING_PROFILE_LDAP_GROUP)
    public IGroupService ldapGroupService() {
        return new GroupService(permissionService, treeJSDTOFactory, userDTOFactory, externalGroupDao, subscriberService, filterRepository, contextService);
    }
}
