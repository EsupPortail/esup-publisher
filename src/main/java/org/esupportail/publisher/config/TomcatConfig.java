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

import java.util.Collections;

import org.apache.catalina.Context;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by jgribonvald on 25/01/16.
 */
@Configuration
public class TomcatConfig {

    @Bean
    public ServletWebServerFactory servletContainer() {
    	TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.setTomcatContextCustomizers(Collections.singletonList(new CustomCustomizer()));
        return factory;
    }

    public static class CustomCustomizer implements TomcatContextCustomizer {
        public void customize(Context context) {
            context.setMapperContextRootRedirectEnabled(true);
            context.setSessionCookiePathUsesTrailingSlash(true);
        }
    }
}