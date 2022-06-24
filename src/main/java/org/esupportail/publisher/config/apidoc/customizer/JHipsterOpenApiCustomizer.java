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
package org.esupportail.publisher.config.apidoc.customizer;

import org.esupportail.publisher.config.ESUPPublisherProperties;
import org.esupportail.publisher.config.bean.ApiDocsProperties;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.core.Ordered;

public class JHipsterOpenApiCustomizer implements OpenApiCustomiser, Ordered {
    public static final int DEFAULT_ORDER = 0;
    private int order = 0;
    private final ApiDocsProperties properties;

    public JHipsterOpenApiCustomizer(ESUPPublisherProperties properties) {
        this.properties = properties.getApiDocs();
    }

    public void customise(OpenAPI openAPI) {
        Contact contact = (new Contact()).name(this.properties.getContactName()).url(this.properties.getContactUrl()).email(this.properties.getContactEmail());
        openAPI.info((new Info()).contact(contact).title(this.properties.getTitle()).description(this.properties.getDescription()).version(this.properties.getVersion()).termsOfService(this.properties.getTermsOfServiceUrl()).license((new License()).name(this.properties.getLicense()).url(this.properties.getLicenseUrl())));

        for (ApiDocsProperties.Server server : this.properties.getServers()) {
            openAPI.addServersItem((new Server()).url(server.getUrl()).description(server.getDescription()));
        }
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }
}