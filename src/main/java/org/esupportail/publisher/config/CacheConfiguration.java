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

import java.time.Duration;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jcache.JCacheGaugeSet;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;

import org.hibernate.cache.jcache.ConfigSettings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.util.Assert;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    private Environment env;

    @Autowired(required = false)
    private MetricRegistry metricRegistry;

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(Environment env) throws IllegalStateException {
        Assert.notNull(env, "Environnement is null !");
        this.env = env;
        long defaultNbEntries = env.getRequiredProperty("cache.ehcache.maxEntries", long.class);
        long defaultTTL = env.getRequiredProperty("cache.ehcache.timeToLiveSeconds", long.class);
        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(defaultNbEntries))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(defaultTTL)))
                .build());
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, org.esupportail.publisher.domain.User.class.getName());
            createCache(cm, org.esupportail.publisher.domain.PersistentAuditEvent.class.getName());
            createCache(cm, org.esupportail.publisher.domain.AbstractClassification.class.getName());
            createCache(cm, org.esupportail.publisher.domain.AbstractItem.class.getName());
            createCache(cm, org.esupportail.publisher.domain.AbstractPermission.class.getName());
            createCache(cm, org.esupportail.publisher.domain.evaluators.OperatorEvaluator.class.getName());
            createCache(cm, org.esupportail.publisher.domain.evaluators.OperatorEvaluator.class.getName() + ".evaluators");
            createCache(cm, org.esupportail.publisher.domain.externals.ExternalGroup.class.getName());
            createCache(cm, org.esupportail.publisher.domain.externals.ExternalUser.class.getName());
            createCache(cm, org.esupportail.publisher.domain.Filter.class.getName());
            createCache(cm, org.esupportail.publisher.domain.ItemClassificationOrder.class.getName());
            createCache(cm, org.esupportail.publisher.domain.LinkedFileItem.class.getName());
            createCache(cm, org.esupportail.publisher.domain.Organization.class.getName());
            createCache(cm, org.esupportail.publisher.domain.Organization.class.getName() + ".availablePublisherContexts");
            createCache(cm, org.esupportail.publisher.domain.Publisher.class.getName());
            createCache(cm, org.esupportail.publisher.domain.Reader.class.getName());
            createCache(cm, org.esupportail.publisher.domain.Reader.class.getName() + ".organizationReaderRedactors");
            createCache(cm, org.esupportail.publisher.domain.Redactor.class.getName());
            createCache(cm, org.esupportail.publisher.domain.Redactor.class.getName() + ".organizationReaderRedactors");
            createCache(cm, org.esupportail.publisher.domain.Subscriber.class.getName());
            createCache(cm, "feed");
            //createCache(cm, org.esupportail.publisher.domain.AbstractClassification.class.getName() + ".books");
            if (!env.acceptsProfiles(Profiles.of(Constants.SPRING_PROFILE_FAST))) {
                this.metricRegistry.register("jcache.statistics", new JCacheGaugeSet());
            }
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cm.destroyCache(cacheName);
        }
        cm.createCache(cacheName, jcacheConfiguration);
    }
}
