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

import org.esupportail.publisher.service.bean.FileUploadHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by jgribonvald on 25/01/16.
 */
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {


    @Autowired
    @Qualifier("publicFileUploadHelper")
    private FileUploadHelper publicFileUploadHelper;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/" + publicFileUploadHelper.getUrlResourceMapping() + "**").addResourceLocations(publicFileUploadHelper.getResourceLocation());
        registry.addResourceHandler("/assets/styles/images/**").addResourceLocations("/bower_components/jquery-ui/themes/smoothness/");
    }

    /*@Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

        configurer.favorPathExtension(false);
        configurer.ignoreAcceptHeader(true);
        configurer.defaultContentType(MediaType.APPLICATION_JSON);

    }*/
}
