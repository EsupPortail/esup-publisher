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
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.codahale.metrics.MetricRegistry;

@Configuration
@EnableCaching
@AutoConfigureAfter(value = {DatabaseConfiguration.class})
@Profile("!" + Constants.SPRING_PROFILE_FAST)
public class CacheConfiguration {

    private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private Environment env;

    @Inject
    private MetricRegistry metricRegistry;

    private org.ehcache.CacheManager cacheManager;
    
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    @PreDestroy
    public void destroy() {
        log.info("Remove Cache Manager metrics");
        SortedSet<String> names = metricRegistry.getNames();
        for (String name : names) {
            metricRegistry.remove(name);
        }
        log.info("Closing Cache Manager");
//        cacheManager.shutdown();
    }
    
    public CacheConfiguration(Environment env) {
    	this.env = env;
    	
        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.newResourcePoolsBuilder())
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(env.getProperty("cache.timeToLiveSeconds", Long.class, 3600L))))
                .withSizeOfMaxObjectSize(16, MemoryUnit.MB)
                .build());
    }
    
    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
    	return cm -> {
    		Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
    		for (EntityType<?> entity : entities) {
    			String name = entity.getName();
              if (name == null || entity.getJavaType() != null) {
                  name = entity.getJavaType().getName();
              }
    			createCache(cm, name);
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

//    @Bean
//    public CacheManager cacheManager() {
//        log.debug("Starting Ehcache");
//        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
//        		.withCache("preConfigured", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(100)).build())
//        		.build(true);
////        cacheManager.getConfiguration().setMaxBytesLocalHeap(env.getProperty("cache.ehcache.maxBytesLocalHeap", String.class, "16M"));
//        log.debug("Registering Ehcache Metrics gauges");
//        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
//        for (EntityType<?> entity : entities) {
//
//            String name = entity.getName();
//            if (name == null || entity.getJavaType() != null) {
//                name = entity.getJavaType().getName();
//            }
//            Assert.notNull(name, "entity cannot exist without a identifier");
//
//            Cache cache = cacheManager.ge getCache(name);
//            if (cache != null) {
////                cache.getRuntimeConfiguration(). getCacheConfiguration().setTimeToLiveSeconds(env.getProperty("cache.timeToLiveSeconds", Long.class, 3600L));
//                net.sf.ehcache.Ehcache decoratedCache = InstrumentedEhcache.instrument(metricRegistry, cache);
//                cacheManager.replaceCacheWithDecoratedCache(cache, decoratedCache);
//            }
//        }
//        EhCacheCacheManager ehCacheManager = new EhCacheCacheManager();
//        ehCacheManager.setCacheManager(cacheManager);
//        return ehCacheManager;
//    }
}
