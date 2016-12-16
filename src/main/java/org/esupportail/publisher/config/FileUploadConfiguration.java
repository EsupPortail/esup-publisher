package org.esupportail.publisher.config;

import java.io.File;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.service.bean.FileUploadHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

/**
 * Created by jgribonvald on 25/01/16.
 */
@Configuration
@Slf4j
public class FileUploadConfiguration {

    @Autowired
    private Environment env;

    private static final String ENV_UPLOAD_PATH = "app.upload.path";
    private static final String ENV_UPLOAD_IMAGESIZE = "app.upload.imageMaxSize";

    private static final long DefaultImageMaxSize = 131072;

    @Bean
    public FileUploadHelper internalFileUploadHelper() {
        FileUploadHelper fuh = new FileUploadHelper();
        if (getDefinedUploadPath() != null) {
            fuh.setUploadDirectoryPath(getDefinedUploadPath());
            fuh.setResourceLocation("file:///" + getDefinedUploadPath());
            fuh.setUrlResourceMapping("files/");
            fuh.setUseDefaultPath(false);
            fuh.setImageMaxSize(env.getProperty(ENV_UPLOAD_IMAGESIZE, long.class, DefaultImageMaxSize));
            log.debug("FileUploadConfiguration : " + fuh.toString());
            return fuh;
        }
        fuh.setUploadDirectoryPath(new ClassPathResource("classpath:static/").getPath());
        fuh.setResourceLocation("classpath:static/");
        fuh.setUrlResourceMapping("files/");
        fuh.setUseDefaultPath(true);
        fuh.setImageMaxSize(env.getProperty(ENV_UPLOAD_IMAGESIZE, long.class, DefaultImageMaxSize));
        log.debug("FileUploadConfiguration : " + fuh.toString());
        return fuh;
    }

    private String getDefinedUploadPath() {
        if (env.containsProperty(ENV_UPLOAD_PATH)) {
            final String path = env.getProperty(ENV_UPLOAD_PATH);
            final File file = new File(path);
            Assert.state(file.exists(), "You defined the property \'app.upload.path\' = '" + path + "' but the path doesn't exist or there isn't read permissions");
            Assert.state(file.isDirectory(), "You defined the property \'app.upload.path\' = '" + path + "' but the path isn't a directory");
            Assert.state(file.canWrite(), "You defined the property \'app.upload.path\' = '" + path + "' but the path isn't has write access");
            return path;
        }
        return null;
    }
}
