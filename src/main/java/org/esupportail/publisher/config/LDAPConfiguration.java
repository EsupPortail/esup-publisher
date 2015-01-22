package org.esupportail.publisher.config;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.externals.ExternalUserHelper;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.util.Arrays;
import java.util.Set;

/**
 *
 * @author GIP RECIA - Julien Gribonvald 10 juil. 2014
 */
@Configuration
@Slf4j
public class LDAPConfiguration implements EnvironmentAware {

	private static final String ENV_LDAP = "ldap.";
	private static final String PROP_ANONYMRO = "anonymousReadOnly";
	private static final String PROP_BASE = "base";
	private static final String PROP_URLS = "urls";
	private static final String PROP_USERDN = "userDn";
	private static final String PROP_PASSWORD = "password";
	private static final String PROP_POOLED = "pooled";
	private static final boolean DEFAULT_POOL = false;
	private static final String PROP_TIMEOUT = "timeLimit";
	private static final Integer DEFAULT_TIMEOUT = 10000;
	private static final String PROP_NBOBJ_LIMIT = "countLimit";
	private static final Integer DEFAULT_NBOBJ_LIMIT = 1500;
	private static final String PROP_USERDN_SUBPATH = "DNsubpath.people";
	private static final String DEFAULT_USERDN_SUBPATH = "ou=people";
	private static final String PROP_USER_ID = "userId";
	private static final String DEFAULT_USER_ID = "uid";
	private static final String PROP_USER_DISPLAYNAME = "userDisplayNameAttribute";
	private static final String DEFAULT_USER_DISPLAYNAME = "displayName";
	private static final String PROP_USER_MAIL = "userEmailAttribute";
	private static final String DEFAULT_USER_MAIL = "mail";
	private static final String PROP_USER_GROUP = "userGroupAttribute";
	private static final String DEFAULT_USER_GROUP = "isMemberOf";
	private static final String PROP_USER_SEARCHON = "userSearchAttribute";
	private static final String DEFAULT_USER_SEARCHON = "cn";
	private static final String PROP_USER_DISPLAYEDATTR = "otherUserDisplayedAttributes";
    private static final String PROP_USER_OTHERATTR = "otherUserOtherAttributes";

	private RelaxedPropertyResolver propertyResolver;

	private Environment environment;

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
		this.propertyResolver = new RelaxedPropertyResolver(environment,
				ENV_LDAP);
	}

	@Bean
	public LdapContextSource contextSource() {
		log.debug("Configuring LdapContextSource");
		final LdapContextSource ldapCtx = new LdapContextSource();
		if (propertyResolver.getProperty(PROP_URLS) == null
				&& propertyResolver.getProperty(PROP_BASE) == null) {
			log.error(
					"Your LDAP connection configuration is incorrect! The application"
							+ "cannot start. Please check your Spring profile, current profiles are: {}",
					Arrays.toString(environment.getActiveProfiles()));

			throw new ApplicationContextException(
					"LDAP connection is not configured correctly");
		}
		ldapCtx.setAnonymousReadOnly(propertyResolver.getProperty(PROP_ANONYMRO, Boolean.class, false));
		ldapCtx.setBase(propertyResolver.getProperty(PROP_BASE));
		ldapCtx.setUrl(propertyResolver.getProperty(PROP_URLS));
		ldapCtx.setUserDn(propertyResolver.getProperty(PROP_USERDN));
		ldapCtx.setPassword(propertyResolver.getProperty(PROP_PASSWORD));
		ldapCtx.setPooled(propertyResolver.getProperty(PROP_POOLED, Boolean.class, DEFAULT_POOL));
		return ldapCtx;
	}

	@Bean
	public LdapTemplate ldapTemplate() {
		final LdapTemplate ldapTpl = new LdapTemplate();
		ldapTpl.setContextSource(contextSource());
		ldapTpl.setDefaultCountLimit(propertyResolver.getProperty(PROP_NBOBJ_LIMIT, Integer.class, DEFAULT_NBOBJ_LIMIT));
		ldapTpl.setDefaultTimeLimit(propertyResolver.getProperty(PROP_TIMEOUT, Integer.class, DEFAULT_TIMEOUT));
		return ldapTpl;
	}

	@Bean
	public ExternalUserHelper externalUserHelper() {
		final String userIdAttribute = propertyResolver.getProperty(PROP_USER_ID, DEFAULT_USER_ID);
		final String userDisplayNameAttribute = propertyResolver.getProperty(PROP_USER_DISPLAYNAME, DEFAULT_USER_DISPLAYNAME);
		final String userEmailAttribute = propertyResolver.getProperty(PROP_USER_MAIL, DEFAULT_USER_MAIL);
		final String userSearchAttribute = propertyResolver.getProperty(PROP_USER_SEARCHON, DEFAULT_USER_SEARCHON);
		final String userGroupAttribute = propertyResolver.getProperty(PROP_USER_GROUP, DEFAULT_USER_GROUP);
        final String UserAttributes = propertyResolver.getRequiredProperty(PROP_USER_OTHERATTR);
		final String userDisplayedAttributes = propertyResolver.getRequiredProperty(PROP_USER_DISPLAYEDATTR);
		Set<String> otherUserDisplayedAttributes = null;
		if (userDisplayedAttributes != null	&& !userDisplayedAttributes.isEmpty()) {
            String[] attrs = userDisplayedAttributes.trim().replaceAll("\\s", "").split(",");
			otherUserDisplayedAttributes = Sets.newHashSet(attrs);
		}
        Set<String> otherUserAttributes = null;
        if (UserAttributes != null	&& !UserAttributes.isEmpty()) {
            String[] attrs = UserAttributes.trim().replaceAll("\\s", "").split(",");
            otherUserAttributes = Sets.newHashSet(attrs);
        }
		final String userDNSubPath = propertyResolver.getProperty(PROP_USERDN_SUBPATH, DEFAULT_USERDN_SUBPATH);

		final ExternalUserHelper ldapUh = new ExternalUserHelper(
				userIdAttribute, userDisplayNameAttribute, userEmailAttribute,
				userSearchAttribute, userGroupAttribute, otherUserAttributes,
				otherUserDisplayedAttributes, userDNSubPath);
		log.debug("LdapAttributes for Users configured : {}", ldapUh);
		return ldapUh;
	}
}
