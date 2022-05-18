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
package org.esupportail.publisher.config.metrics;

import javax.sql.DataSource;

import org.esupportail.publisher.config.LDAPConfiguration;
import org.esupportail.publisher.config.LiquibaseConfiguration;
import org.esupportail.publisher.domain.externals.ExternalUserHelper;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@AutoConfigureAfter({LiquibaseConfiguration.class, LDAPConfiguration.class, ExternalUserHelper.class, JavaMailSenderImpl.class})
public class JHipsterHealthIndicatorConfiguration {

	private JavaMailSenderImpl javaMailSender;

	private DataSource dataSource;

	private LdapContextSource contextSource;

	private ExternalUserHelper externalUserHelper;

	public JHipsterHealthIndicatorConfiguration(JavaMailSenderImpl javaMailSender, DataSource dataSource, LdapContextSource contextSource, ExternalUserHelper externalUserHelper) {
		this.javaMailSender = javaMailSender;
		this.dataSource = dataSource;
		this.contextSource = contextSource;
		this.externalUserHelper = externalUserHelper;
	}

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