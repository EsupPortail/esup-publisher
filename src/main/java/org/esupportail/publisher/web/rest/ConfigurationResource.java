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

import org.esupportail.publisher.service.bean.FileUploadHelper;
import org.esupportail.publisher.web.rest.dto.ValueResource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Evaluator.
 */
@RestController
@RequestMapping("/api")
public class ConfigurationResource {

    //private final Logger log = LoggerFactory.getLogger(ConfigurationResource.class);

    @Inject
    @Qualifier("publicFileUploadHelper")
    private FileUploadHelper publicFileUploadHelper;

    @Inject
    @Qualifier("protectedFileUploadHelper")
    private FileUploadHelper protectedFileUploadHelper;

    @RequestMapping(value = "/conf/uploadimagesize",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValueResource> getConfImageSize() {
        return new ResponseEntity<ValueResource>(new ValueResource(publicFileUploadHelper.getFileMaxSize()), HttpStatus.OK);
    }

    @RequestMapping(value = "/conf/uploadfilesize",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValueResource> getConfFileSize() {
        return new ResponseEntity<ValueResource>(new ValueResource(protectedFileUploadHelper.getFileMaxSize()), HttpStatus.OK);
    }
}
