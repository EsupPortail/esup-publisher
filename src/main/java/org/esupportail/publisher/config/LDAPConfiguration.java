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

import java.util.Arrays;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.esupportail.publisher.domain.externals.ExternalUserHelper;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import com.google.common.collect.Sets;

/**
 *
 * @author GIP RECIA - Julien Gribonvald 10 juil. 2014
 */
@Configuration
@Slf4j
public class LDAPConfiguration {

    private static final String PROP_ANONYMRO = "ldap.anonymousReadOnly";
    private static final String PROP_BASE = "ldap.base";
    private static final String PROP_URLS = "ldap.urls";
    private static final String PROP_USERDN = "ldap.userDn";
    private static final String PROP_PASSWORD = "ldap.password";
    private static final String PROP_POOLED = "ldap.pooled";
    private static final boolean DEFAULT_POOL = false;
    private static final String PROP_TIMEOUT = "ldap.timeLimit";
    private static final Integer DEFAULT_TIMEOUT = 10000;
    private static final String PROP_NBOBJ_LIMIT = "ldap.countLimit";
    private static final Integer DEFAULT_NBOBJ_LIMIT = 1500;
    private static final String PROP_USERDN_SUBPATH = "ldap.DNsubpath.people";
    private static final String DEFAULT_USERDN_SUBPATH = "ou=people";
    private static final String PROP_USER_ID = "ldap.userId";
    private static final String DEFAULT_USER_ID = "uid";
    private static final String PROP_USER_DISPLAYNAME = "ldap.userDisplayNameAttribute";
    private static final String DEFAULT_USER_DISPLAYNAME = "displayName";
    private static final String PROP_USER_MAIL = "ldap.userEmailAttribute";
    private static final String DEFAULT_USER_MAIL = "mail";
    private static final String PROP_USER_GROUP = "ldap.userGroupAttribute";
    private static final String DEFAULT_USER_GROUP = "isMemberOf";

    private static final String PROP_USER_SEARCHON = "ldap.userSearchAttribute";
    private static final String DEFAULT_USER_SEARCHON = "cn";
    private static final String PROP_USER_DISPLAYEDATTR = "ldap.otherUserDisplayedAttributes";
    private static final String PROP_USER_OTHERATTR = "ldap.otherUserOtherAttributes";

    private Environment environment;

    public LDAPConfiguration(Environment env) {
        this.environment = env;
    }

    @Bean
    public LdapContextSource contextSource() {
        log.debug("Configuring LdapContextSource");
        final LdapContextSource ldapCtx = new LdapContextSource();
        if (environment.getProperty(PROP_URLS) == null && environment.getProperty(PROP_BASE) == null) {
            log.error("Your LDAP connection configuration is incorrect! The application"
                    + "cannot start. Please check your Spring profile, current profiles are: {}",
                Arrays.toString(environment.getActiveProfiles()));

            throw new ApplicationContextException("LDAP connection is not configured correctly");
        }
        ldapCtx.setAnonymousReadOnly(environment.getProperty(PROP_ANONYMRO, Boolean.class, false));
        ldapCtx.setBase(environment.getProperty(PROP_BASE));
        ldapCtx.setUrl(environment.getProperty(PROP_URLS));
        ldapCtx.setUserDn(environment.getProperty(PROP_USERDN));
        ldapCtx.setPassword(environment.getProperty(PROP_PASSWORD));
        ldapCtx.setPooled(environment.getProperty(PROP_POOLED, Boolean.class, DEFAULT_POOL));
        return ldapCtx;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        final LdapTemplate ldapTpl = new LdapTemplate();
        ldapTpl.setContextSource(contextSource());
        ldapTpl.setDefaultCountLimit(environment.getProperty(PROP_NBOBJ_LIMIT, Integer.class, DEFAULT_NBOBJ_LIMIT));
        ldapTpl.setDefaultTimeLimit(environment.getProperty(PROP_TIMEOUT, Integer.class, DEFAULT_TIMEOUT));
        return ldapTpl;
    }

    @Bean
    public ExternalUserHelper externalUserHelper() {
        final String userIdAttribute = environment.getProperty(PROP_USER_ID, DEFAULT_USER_ID);
        final String userDisplayNameAttribute = environment.getProperty(PROP_USER_DISPLAYNAME,
            DEFAULT_USER_DISPLAYNAME);
        final String userEmailAttribute = environment.getProperty(PROP_USER_MAIL, DEFAULT_USER_MAIL);
        final String userSearchAttribute = environment.getProperty(PROP_USER_SEARCHON, DEFAULT_USER_SEARCHON);
        final String userGroupAttribute = environment.getProperty(PROP_USER_GROUP, DEFAULT_USER_GROUP);
        final String UserAttributes = environment.getRequiredProperty(PROP_USER_OTHERATTR);
        final String userDisplayedAttributes = environment.getRequiredProperty(PROP_USER_DISPLAYEDATTR);
        Set<String> otherUserDisplayedAttributes = null;
        if (userDisplayedAttributes != null && !userDisplayedAttributes.isEmpty()) {
            String[] attrs = userDisplayedAttributes.trim().replaceAll("\\s", "").split(",");
            otherUserDisplayedAttributes = Sets.newHashSet(attrs);
        }
        Set<String> otherUserAttributes = null;
        if (UserAttributes != null && !UserAttributes.isEmpty()) {
            String[] attrs = UserAttributes.trim().replaceAll("\\s", "").split(",");
            otherUserAttributes = Sets.newHashSet(attrs);
        }
        final String userDNSubPath = environment.getProperty(PROP_USERDN_SUBPATH, DEFAULT_USERDN_SUBPATH);

        final ExternalUserHelper ldapUh = new ExternalUserHelper(userIdAttribute, userDisplayNameAttribute,
            userEmailAttribute, userSearchAttribute, userGroupAttribute, otherUserAttributes,
            otherUserDisplayedAttributes, userDNSubPath);
        log.debug("LdapAttributes for Users configured : {}", ldapUh);
        return ldapUh;
    }
}
