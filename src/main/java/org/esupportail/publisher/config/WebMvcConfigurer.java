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
    }

    /*@Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

        configurer.favorPathExtension(false);
        configurer.ignoreAcceptHeader(true);
        configurer.defaultContentType(MediaType.APPLICATION_JSON);

    }*/
}
