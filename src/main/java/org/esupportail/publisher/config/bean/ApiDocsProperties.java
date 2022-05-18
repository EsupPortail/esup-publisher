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
package org.esupportail.publisher.config.bean;

import java.util.Arrays;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class ApiDocsProperties {
    private String title = "Application API";
    private String description = "API documentation";
    private String version = "0.0.1";
    private String termsOfServiceUrl;
    private String contactName;
    private String contactUrl;
    private String contactEmail;
    private String license;
    private String licenseUrl;
    private String defaultIncludePattern = "/api/**";
    private String managementIncludePattern = "/management/**";
    private Server[] servers;

    @Data
    public static class Server {
        private String url;
        private String description;

        @Override
        public String toString() {
            return "{\n\"Server\":{"
                    + "\n \"url\":\"" + url + "\""
                    + ",\n \"description\":\"" + description + "\""
                    + "\n}\n}";
        }
    }

    @Override
    public String toString() {
        return "{\n\"ApiDocsProperties\":{"
                + "\n \"title\":\"" + title + "\""
                + ",\n \"description\":\"" + description + "\""
                + ",\n \"version\":\"" + version + "\""
                + ",\n \"termsOfServiceUrl\":\"" + termsOfServiceUrl + "\""
                + ",\n \"contactName\":\"" + contactName + "\""
                + ",\n \"contactUrl\":\"" + contactUrl + "\""
                + ",\n \"contactEmail\":\"" + contactEmail + "\""
                + ",\n \"license\":\"" + license + "\""
                + ",\n \"licenseUrl\":\"" + licenseUrl + "\""
                + ",\n \"defaultIncludePattern\":\"" + defaultIncludePattern + "\""
                + ",\n \"managementIncludePattern\":\"" + managementIncludePattern + "\""
                + ",\n \"servers\":" + (servers != null ? Arrays.stream(servers)
                .map(String::valueOf)
                .collect(Collectors.joining("\",\"", "[\"", "\"]")) : null )
                + "\n}\n}";
    }
}