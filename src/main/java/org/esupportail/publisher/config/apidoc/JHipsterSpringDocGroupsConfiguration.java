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
package org.esupportail.publisher.config.apidoc;

import static org.springdoc.core.Constants.DEFAULT_GROUP_NAME;
import static org.springdoc.core.Constants.SPRINGDOC_SHOW_ACTUATOR;
import static org.springdoc.core.SpringDocUtils.getConfig;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

import org.esupportail.publisher.config.Constants;
import org.esupportail.publisher.config.ESUPPublisherProperties;
import org.esupportail.publisher.config.apidoc.customizer.JHipsterOpenApiCustomizer;

import io.swagger.v3.oas.models.info.Info;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.GroupedOpenApi.Builder;
import org.springdoc.core.customizers.ActuatorOpenApiCustomizer;
import org.springdoc.core.customizers.ActuatorOperationCustomizer;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * OpenApi Groups configuration.
 * <p>
 * Warning! When having a lot of REST endpoints, OpenApi can become a performance issue.
 * In that case, you can use the "no-api-docs" Spring profile, so that this bean is ignored.
 */
@Configuration
@Profile(Constants.SPRING_PROFILE_API_DOCS)
public class JHipsterSpringDocGroupsConfiguration {

    public static final String MANAGEMENT_GROUP_NAME = "management";

    static {
        getConfig().replaceWithClass(ByteBuffer.class, String.class);
    }

    static final String MANAGEMENT_TITLE_SUFFIX = "Management API";
    static final String MANAGEMENT_DESCRIPTION = "Management endpoints documentation";

    private final Logger log = LoggerFactory.getLogger(JHipsterSpringDocGroupsConfiguration.class);

    private final ESUPPublisherProperties properties;

    /**
     * <p>Constructor for OpenApiAutoConfiguration.</p>
     *
     * @param esupPublisherProperties a {@link ESUPPublisherProperties} object.
     */
    public JHipsterSpringDocGroupsConfiguration(ESUPPublisherProperties esupPublisherProperties) {
        properties = esupPublisherProperties;
    }

    /**
     * JHipster OpenApi Customiser
     *
     * @return the Customizer of JHipster
     */
    @Bean
    public JHipsterOpenApiCustomizer jhipsterOpenApiCustomizer() {
        log.debug("Initializing JHipster OpenApi customizer");
        return new JHipsterOpenApiCustomizer(properties);
    }

    /**
     * OpenApi default group configuration.
     *
     * @return the GroupedOpenApi configuration
     */
    @Bean
    @ConditionalOnMissingBean(name = "openAPIDefaultGroupedOpenAPI")
    public GroupedOpenApi openAPIDefaultGroupedOpenAPI(
        List<OpenApiCustomiser> openApiCustomisers,
        List<OperationCustomizer> operationCustomizers,
        @Qualifier("apiFirstGroupedOpenAPI") Optional<GroupedOpenApi> apiFirstGroupedOpenAPI
    ) {
        log.debug("Initializing JHipster OpenApi default group");
        Builder builder = GroupedOpenApi.builder()
            .group(DEFAULT_GROUP_NAME)
            .pathsToMatch(properties.getApiDocs().getDefaultIncludePattern());
        openApiCustomisers.stream()
            .filter(customizer -> !(customizer instanceof ActuatorOpenApiCustomizer))
            .forEach(builder::addOpenApiCustomiser);
        operationCustomizers.stream()
            .filter(customizer -> !(customizer instanceof ActuatorOperationCustomizer))
            .forEach(builder::addOperationCustomizer);
        apiFirstGroupedOpenAPI.map(GroupedOpenApi::getPackagesToScan)
            .ifPresent(packagesToScan -> packagesToScan.forEach(builder::packagesToExclude));
        return builder.build();
    }

    /**
     * OpenApi management group configuration for the management endpoints (actuator) OpenAPI docs.
     *
     * @return the GroupedOpenApi configuration
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties")
    @ConditionalOnMissingBean(name = "openAPIManagementGroupedOpenAPI")
    @ConditionalOnProperty(SPRINGDOC_SHOW_ACTUATOR)
    public GroupedOpenApi openAPIManagementGroupedOpenAPI(
        @Value("${spring.application.name:application}") String appName,
        ActuatorOpenApiCustomizer actuatorOpenApiCustomiser,
        ActuatorOperationCustomizer actuatorCustomizer
    ) {
        log.debug("Initializing JHipster OpenApi management group");
        return GroupedOpenApi.builder()
            .group(MANAGEMENT_GROUP_NAME)
            .addOpenApiCustomiser(openApi -> openApi.info(new Info()
                .title(StringUtils.capitalize(appName) + " " + MANAGEMENT_TITLE_SUFFIX)
                .description(MANAGEMENT_DESCRIPTION)
                .version(properties.getApiDocs().getVersion())
            ))
            .addOpenApiCustomiser(actuatorOpenApiCustomiser)
            .addOperationCustomizer(actuatorCustomizer)
            .pathsToMatch(properties.getApiDocs().getManagementIncludePattern())
            .build();
    }
}