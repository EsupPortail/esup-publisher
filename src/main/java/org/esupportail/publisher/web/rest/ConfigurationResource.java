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
