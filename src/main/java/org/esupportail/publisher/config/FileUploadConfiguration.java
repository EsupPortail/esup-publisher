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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.esupportail.publisher.config.bean.UploadProperties;
import org.esupportail.publisher.service.bean.FileUploadHelper;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.esupportail.publisher.web.ViewController;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

/**
 * Created by jgribonvald on 25/01/16.
 */
@Configuration
@Slf4j
public class FileUploadConfiguration {

    private final UploadProperties uploadProperties;

    private static final long defaultImageMaxSize = 131072;
    private static final long defaultFileMaxSize = 10485760;
    private static final String defaultFileMimeTypes = "MimeTypes.txt";

    public FileUploadConfiguration(ESUPPublisherProperties esupPublisherProperties) {
        this.uploadProperties = esupPublisherProperties.getUpload();
    }

    @Bean(name = "publicFileUploadHelper")
    public FileUploadHelper publicFileUploadHelper() throws IOException, URISyntaxException {
        FileUploadHelper fuh = new FileUploadHelper();
        final String path = getDefinedUploadPath( "upload.path", uploadProperties.getPath());
        Assert.notNull(path, "You should define a protected file path");

        fuh.setUploadDirectoryPath(path);
        fuh.setResourceLocation("file:///" + path);
        fuh.setUrlResourceMapping("files/");
        fuh.setUseDefaultPath(false);
        fuh.setFileMaxSize(uploadProperties.getImageMaxSize() != null ? uploadProperties.getImageMaxSize() : defaultImageMaxSize);
        fuh.setUnremovablePaths(uploadProperties.getUnremovablePathPattern() != null ? uploadProperties.getUnremovablePathPattern() : Sets.newHashSet());
        final String mimeTypesFilePath = uploadProperties.getFilePathOfAuthorizedMimeTypes() != null ? uploadProperties.getFilePathOfAuthorizedMimeTypes() : defaultFileMimeTypes;
        try {
            List<String> list = Files.readAllLines(Paths.get(new ClassPathResource(mimeTypesFilePath).getURI()), StandardCharsets.UTF_8);
            fuh.setAuthorizedMimeType(Sets.newHashSet(list));
        } catch (IOException e) {
            log.error("No file describing authorized MimeTypes is readable !", e);
            throw e;
        }
        log.debug("FileUploadConfiguration : {}", fuh);
        return fuh;
    }

    @Bean(name = "protectedFileUploadHelper")
    public FileUploadHelper protectedFileUploadHelper() throws IOException, URISyntaxException {
        FileUploadHelper fuh = new FileUploadHelper();
        final String path = getDefinedUploadPath("upload.protectedPath", uploadProperties.getProtectedPath());
        Assert.notNull(path, "You should define a protected file path");

        fuh.setUploadDirectoryPath(path);
        fuh.setResourceLocation("file:///" + path);
        fuh.setUrlResourceMapping(ServiceUrlHelper.FILE_VIEW.replaceFirst("/", ""));
        fuh.setUseDefaultPath(false);
        fuh.setFileMaxSize(uploadProperties.getMaxFileSize() != null ? uploadProperties.getMaxFileSize() : defaultFileMaxSize);
        fuh.setUnremovablePaths(uploadProperties.getUnremovablePathPattern() != null ? uploadProperties.getUnremovablePathPattern() : Sets.newHashSet());
        final String mimeTypesFilePath = uploadProperties.getFilePathOfAuthorizedMimeTypes() != null ? uploadProperties.getFilePathOfAuthorizedMimeTypes() : defaultFileMimeTypes;
        try {
            List<String> list = Files.readAllLines(Paths.get(new ClassPathResource(mimeTypesFilePath).getURI()), StandardCharsets.UTF_8);
            fuh.setAuthorizedMimeType(Sets.newHashSet(list));
        } catch (IOException e) {
            log.error("No file describing authorized MimeTypes is readable !", e);
            throw e;
        }
        log.debug("FileUploadConfiguration : " + fuh.toString());
        return fuh;
    }

    private String getDefinedUploadPath(final String propertyKey, final String path) {
        final File file = new File(path);
        Assert.state(file.exists(), "You defined the property '" + propertyKey + "' = '" + path + "' but the path doesn't exist or there isn't read permissions");
        Assert.state(file.isDirectory(), "You defined the property '" + propertyKey + "' = '" + path + "' but the path isn't a directory");
        Assert.state(file.canWrite(), "You defined the property '" + propertyKey + "' = '" + path + "' but the path isn't has write access");
        return path;
    }
}
