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
package org.esupportail.publisher.web.rest;

import javax.inject.Inject;

import org.esupportail.publisher.config.ESUPPublisherProperties;
import org.esupportail.publisher.config.bean.InjectedWebComponentsProperties;
import org.esupportail.publisher.service.bean.CKEditorHelper;
import org.esupportail.publisher.service.bean.FileUploadHelper;
import org.esupportail.publisher.web.rest.dto.ValueResource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing Evaluator.
 */
@RestController
@RequestMapping("/api")
public class ConfigurationResource {

    //private final Logger log = LoggerFactory.getLogger(ConfigurationResource.class);

    private final InjectedWebComponentsProperties injectedWebComponentsProperties;

    public ConfigurationResource(ESUPPublisherProperties esupPublisherProperties) {
        this.injectedWebComponentsProperties = esupPublisherProperties.getInjectedWebComponents();
    }

    @Inject
    @Qualifier("publicFileUploadHelper")
    private FileUploadHelper publicFileUploadHelper;

    @Inject
    @Qualifier("protectedFileUploadHelper")
    private FileUploadHelper protectedFileUploadHelper;

    @Inject
    @Qualifier("ckeditorHelper")
    private CKEditorHelper ckeditorHelper;

    @GetMapping(value = "/conf/uploadimagesize",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValueResource> getConfImageSize() {
        return new ResponseEntity<>(new ValueResource(publicFileUploadHelper.getFileMaxSize()), HttpStatus.OK);
    }

    @GetMapping(value = "/conf/uploadfilesize",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValueResource> getConfFileSize() {
        return new ResponseEntity<>(new ValueResource(protectedFileUploadHelper.getFileMaxSize()), HttpStatus.OK);
    }

    @GetMapping(value = "/conf/authorizedmimetypes",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValueResource> getConfAuthorizedMimeTypes() {
        return new ResponseEntity<>(new ValueResource(protectedFileUploadHelper.getAuthorizedMimeType()), HttpStatus.OK);
    }

    @GetMapping(value = "/conf/ckeditor",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValueResource> getConfCKEditor() {
        return new ResponseEntity<>(new ValueResource(ckeditorHelper), HttpStatus.OK);
    }

    @GetMapping(value = "/conf/injectedWebComponents",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<InjectedWebComponentsProperties.WebComponents>> getInjectedWebComponents() {
        return new ResponseEntity<>(injectedWebComponentsProperties.getWebComponents(), HttpStatus.OK);
    }
}
