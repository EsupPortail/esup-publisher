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

import java.sql.SQLException;

import javax.sql.DataSource;

import org.esupportail.publisher.config.h2.H2ConfigurationHelper;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories("org.esupportail.publisher.repository")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration {

    private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

	private final Environment env;

	public DatabaseConfiguration(Environment env) {
		this.env = env;
	}

	/**
	 * Open the TCP port for the H2 database, so it is available remotely.
	 *
	 * @return the H2 database TCP server.
	 * @throws SQLException if the server failed to start.
	 */
	@Bean(initMethod = "start", destroyMethod = "stop")
	@Profile(Constants.SPRING_PROFILE_DEVELOPMENT + " && " + Constants.SPRING_PROFILE_DBH2)
	public Object h2TCPServer() throws SQLException {
		String port = getValidPortForH2();
		log.debug("H2 database is available on port {}", port);
		return H2ConfigurationHelper.createServer(port);
	}

	private String getValidPortForH2() {
		int port = env.getProperty("server.port", Integer.class, 1000);
		if (port < 10000) {
			port = 10000 + port;
		} else {
			if (port < 63536) {
				port = port + 2000;
			} else {
				port = port - 2000;
			}
		}
		return String.valueOf(port);
	}

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
            JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .usingDbTime() // Works on Postgres, MySQL, MariaDb, MS SQL, Oracle, DB2, HSQL and H2
                .build()
        );
    }

}