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

import org.esupportail.publisher.config.bean.CKEditorProperties;
import org.esupportail.publisher.service.bean.CKEditorHelper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class UIConfiguration {

    private final CKEditorProperties ckeditorProperties;

    public UIConfiguration(ESUPPublisherProperties esupPublisherProperties) {
        this.ckeditorProperties = esupPublisherProperties.getCkeditor();
    }

    @Bean
    public CKEditorHelper ckeditorHelper() {
        log.debug("Configuring CKEditorHelper");
        final CKEditorHelper ckeditorHelper = new CKEditorHelper();
        ckeditorHelper.setMediaUrlPattern(ckeditorProperties.getMediaEmbed().getMediaUrlPattern());
        log.debug("CKEditorHelper is configured with properties {}", ckeditorHelper);
        return ckeditorHelper;
    }

}
