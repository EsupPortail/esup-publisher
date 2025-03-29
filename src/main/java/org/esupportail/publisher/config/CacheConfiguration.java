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
import java.util.concurrent.TimeUnit;

import lombok.Data;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(ESUPPublisherProperties esupPublisherProperties) throws IllegalStateException {
        long defaultNbEntries = esupPublisherProperties.getCache().getMaxEntries();
        long defaultTTL = esupPublisherProperties.getCache().getTimeToLiveSeconds();
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
            createCache(cm, "actualiteByPublisher");
            //createCache(cm, org.esupportail.publisher.domain.AbstractClassification.class.getName() + ".books");
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
        cm.createCache(cacheName, jcacheConfiguration);
    }
}
