package org.esupportail.publisher.config.metrics;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.esupportail.publisher.domain.externals.ExternalUserHelper;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class JHipsterHealthIndicatorConfiguration {

	@Inject
	private JavaMailSenderImpl javaMailSender;

	@Inject
	private DataSource dataSource;

	@Inject
	private LdapContextSource contextSource;

	@Inject
	private ExternalUserHelper externalUserHelper;

	@Bean
	public HealthIndicator dbHealthIndicator() {
		return new DatabaseHealthIndicator(dataSource);
	}

	@Bean
	public HealthIndicator mailHealthIndicator() {
		return new JavaMailHealthIndicator(javaMailSender);
	}

	@Bean
	public HealthIndicator ldapHealthIndicator() {
		return new LdapHealthIndicator(contextSource, externalUserHelper);
	}
}
