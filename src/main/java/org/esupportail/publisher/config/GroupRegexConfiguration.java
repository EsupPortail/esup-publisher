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

import java.util.List;

import javax.annotation.PostConstruct;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.config.bean.GroupRegexProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix="ldap.escoGroupRegexp")
@Validated
@Data
@Slf4j
public class GroupRegexConfiguration {

    private List<GroupRegexProperties> properties;

    @PostConstruct
    public void debug() {
        log.debug("List of GroupRegexProperties setted {}", properties);
    }
}
