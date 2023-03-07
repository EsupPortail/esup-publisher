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

import javax.annotation.PostConstruct;


import org.esupportail.publisher.config.bean.ApiDocsProperties;
import org.esupportail.publisher.config.bean.CASProperties;
import org.esupportail.publisher.config.bean.CKEditorProperties;
import org.esupportail.publisher.config.bean.CacheProperties;
import org.esupportail.publisher.config.bean.CustomLdapProperties;
import org.esupportail.publisher.config.bean.CustomMailProperties;
import org.esupportail.publisher.config.bean.CustomMetricsProperties;
import org.esupportail.publisher.config.bean.IpRangeProperties;
import org.esupportail.publisher.config.bean.RoleMappingProperties;
import org.esupportail.publisher.config.bean.SecurityProperties;
import org.esupportail.publisher.config.bean.ServiceProperties;
import org.esupportail.publisher.config.bean.UploadProperties;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@ConfigurationProperties(
        prefix = "app",
        ignoreUnknownFields = false
)
@Data
@Validated
@Slf4j
public class ESUPPublisherProperties {

    private ApiDocsProperties apiDocs = new ApiDocsProperties();
    private RoleMappingProperties admins = new RoleMappingProperties();
    private RoleMappingProperties users = new RoleMappingProperties();
    private CustomMailProperties mail = new CustomMailProperties();
    private CorsConfiguration cors = new CorsConfiguration();
    private IpRangeProperties authorizedServices = new IpRangeProperties();
    private CustomMetricsProperties metrics = new CustomMetricsProperties();
    private ServiceProperties service = new ServiceProperties();
    private SecurityProperties security = new SecurityProperties();
    private UploadProperties upload = new UploadProperties();
    private CASProperties cas = new CASProperties();
    private CacheProperties cache = new CacheProperties();
    private CustomLdapProperties ldap = new CustomLdapProperties();
    private CKEditorProperties ckeditor = new CKEditorProperties();

    @PostConstruct
    private void init() throws JsonProcessingException {
        log.info("Loaded properties: \n{}", this);
    }

    @Override
    public String toString() {
        return "{\n\"ESUPPublisherProperties\":{"
                + "\n\t \"apiDocs\":" + apiDocs
                + ",\n\t \"admins\":" + admins
                + ",\n\t \"users\":" + users
                + ",\n\t \"mail\":" + mail
                //+ ", \"cors\":" + cors
                + ",\n\t \"authorizedServices\":" + authorizedServices
                + ",\n\t \"metrics\":" + metrics
                + ",\n\t \"service\":" + service
                + ",\n\t \"security\":" + security
                + ",\n\t \"upload\":" + upload
                + ",\n\t \"cas\":" + cas
                + ",\n\t \"cache\":" + cache
                + ",\n\t \"ldap\":" + ldap
                + ",\n\t \"ckeditor\":" + ckeditor
                + "\n\t}\n}";
    }
}
