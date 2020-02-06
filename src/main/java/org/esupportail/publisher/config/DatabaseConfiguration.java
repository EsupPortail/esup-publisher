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

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@EnableJpaRepositories("org.esupportail.publisher.repository")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration {

    private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

//	private RelaxedPropertyResolver propertyResolver;
    @Autowired
	private Environment env;

	@Autowired(required = false)
	private MetricRegistry metricRegistry;

//	@Bean(destroyMethod = "close")
//	@ConditionalOnMissingClass(value = "org.esupportail.publisher.config.HerokuDatabaseConfiguration")
//	@Profile("!" + Constants.SPRING_PROFILE_CLOUD)
//	public DataSource dataSource() {
//		log.debug("Configuring Datasource");
//        if (env.getProperty("spring.datasource.url") == null && env.getProperty("spring.datasource.databaseName") == null) {
//            log.error("Your database connection pool configuration is incorrect! The application" +
//                    "cannot start. Please check your Spring profile, current profiles are: {}",
//					Arrays.toString(env.getActiveProfiles()));
//
//            throw new ApplicationContextException("Database connection pool is not configured correctly");
//		}
//		HikariConfig config = new HikariConfig();
//        config.setDataSourceClassName(env.getProperty("spring.datasource.dataSourceClassName"));
//        if (env.getProperty("spring.datasource.url") == null || "".equals(env.getProperty("spring.datasource.url"))) {
//            config.addDataSourceProperty("databaseName", env.getProperty("spring.datasource.databaseName"));
//            config.addDataSourceProperty("serverName", env.getProperty("spring.datasource.serverName"));
//
//		} else {
//            config.addDataSourceProperty("url", env.getProperty("spring.datasource.url"));
//		}
//        config.addDataSourceProperty("user", env.getProperty("spring.datasource.username"));
//        config.addDataSourceProperty("password", env.getProperty("spring.datasource.password"));
//
//        config.setConnectionTestQuery(env.getProperty("spring.datasource.connectionTestQuery", "SELECT 1"));
//
//        //MySQL optimizations, see https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
//        if ("com.mysql.jdbc.jdbc2.optional.MysqlDataSource".equals(env.getProperty("spring.datasource.dataSourceClassName"))) {
//            config.addDataSourceProperty("cachePrepStmts", env.getProperty("spring.datasource.cachePrepStmts", "true"));
//            config.addDataSourceProperty("prepStmtCacheSize", env.getProperty("spring.datasource.prepStmtCacheSize", "250"));
//            config.addDataSourceProperty("prepStmtCacheSqlLimit", env.getProperty("spring.datasource.prepStmtCacheSqlLimit", "2048"));
//            config.addDataSourceProperty("useServerPrepStmts", env.getProperty("spring.datasource.useServerPrepStmts", "true"));
//		}
//		log.debug("Use config  {}", config.getDataSourceProperties().toString());
//		if (metricRegistry != null) {
//			config.setMetricRegistry(metricRegistry);
//		}
//		return new HikariDataSource(config);
//	}

	@Bean
	public SpringLiquibase liquibase(DataSource dataSource) {
		SpringLiquibase liquibase = new SpringLiquibase();
		liquibase.setDataSource(dataSource);
        liquibase.setIgnoreClasspathPrefix(true);
		liquibase.setChangeLog("classpath:config/liquibase/master.xml");
		liquibase.setContexts("development, production");
		if (env.acceptsProfiles(Profiles.of(Constants.SPRING_PROFILE_FAST))) {
            liquibase.setShouldRun(true);
            if ("org.h2.jdbcx.JdbcDataSource".equals(env.getProperty("spring.datasource.dataSourceClassName"))) {
                log.warn("Using '{}' profile with H2 database in memory is not optimal, you should consider switching to" +
                    " MySQL or Postgresql to avoid rebuilding your database upon each start.", Constants.SPRING_PROFILE_FAST);
            }
		} else {
			log.debug("Configuring Liquibase");
		}
		return liquibase;
	}

}
