package org.esupportail.publisher.config.metrics;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.externals.ExternalUserHelper;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.externals.ldap.LdapUserContextMapper;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * SpringBoot Actuator HealthIndicator check for the Database.
 */
@Slf4j
public class LdapHealthIndicator extends AbstractHealthIndicator {

	private LdapTemplate ldapTemplate;

	private ExternalUserHelper userHelper;

	private static String DEFAULT_QUERY = "(&(uid=admin))";

	private static final Integer DEFAULT_TIMEOUT = 10000;
	private static final Integer DEFAULT_NBOBJ_LIMIT = 10;

	private String query = null;

	public LdapHealthIndicator(LdapContextSource contextSource,
			ExternalUserHelper helper) {
		Assert.notNull(contextSource, "contextSource must not be null");
		Assert.notNull(helper, "ExternalUserHelper must not be null");
		this.ldapTemplate = this.ldapTemplate(contextSource);
		this.userHelper = helper;
	}

	public LdapTemplate ldapTemplate(LdapContextSource contextSource) {
		final LdapTemplate ldapTpl = new LdapTemplate();
		ldapTpl.setContextSource(contextSource);
		ldapTpl.setDefaultCountLimit(DEFAULT_NBOBJ_LIMIT);
		ldapTpl.setDefaultTimeLimit(DEFAULT_TIMEOUT);
		return ldapTpl;
	}

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		log.debug("Initializing Ldap health indicator");
		builder.up().withDetail("datasource", "LDAP");
		final Filter filter = new HardcodedFilter(detectQuery());
		LdapQuery ldapQuery = LdapQueryBuilder.query()
				.base(userHelper.getUserDNSubPath()).filter(filter);
		ContextMapper<IExternalUser> mapper = new LdapUserContextMapper(
				this.userHelper);
		// if (StringUtils.hasText(query.toString())) {
		try {
			IExternalUser user = ldapTemplate.searchForObject(ldapQuery, mapper);
			builder.withDetail("Admin", user);
		} catch (Exception ex) {
			log.debug("Cannot connect to ldap server. Error: {}",
					ex.getMessage());
			builder.down(ex);
		}
		// }
	}

	protected String detectQuery() {
		String query = this.query;
		if (!StringUtils.hasText(query)) {
			query = DEFAULT_QUERY;
		}
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
