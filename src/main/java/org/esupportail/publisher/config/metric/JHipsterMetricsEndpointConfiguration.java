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
package org.esupportail.publisher.config.metric;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsEndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter({MetricsEndpointAutoConfiguration.class})
@Slf4j
public class JHipsterMetricsEndpointConfiguration {
    public JHipsterMetricsEndpointConfiguration() {
        log.info("Creating jHipsterMetricsEndpoint");
    }

    @Bean
    @ConditionalOnBean({MeterRegistry.class})
    public JHipsterMetricsEndpoint jHipsterMetricsEndpoint(MeterRegistry meterRegistry) {
        log.info("Loaded jHipsterMetricsEndpoint");
        return new JHipsterMetricsEndpoint(meterRegistry);
    }
}