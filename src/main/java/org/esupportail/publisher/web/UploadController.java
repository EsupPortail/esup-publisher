package org.esupportail.publisher.web;

import com.codahale.metrics.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.security.SecurityConstants;
import org.esupportail.publisher.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by jgribonvald on 22/01/16.
 */
@RestController
@RequestMapping("/app")
@Slf4j
public class UploadController {

    @Inject
    private FileService fileService;


    @RequestMapping(value = "/upload",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && hasPermission(#entityId,  '" + SecurityConstants.CTX_ORGANIZATION + "', '" + SecurityConstants.PERM_LOOKOVER + "')")
    @Timed
    public ResponseEntity<Void> upload(@RequestParam(value = "isPublic", required = false, defaultValue = "false") boolean isPublic,
                                       @RequestParam(value = "entityId") Long entityId,
                                       @RequestParam(value = "name", required = false) String name,
                                       @RequestParam("file") MultipartFile file) throws URISyntaxException {
        log.debug("Entering in upload : {}, {}, {}, file : {}, {}, {}", isPublic, entityId, name, file.getOriginalFilename(), file.getSize(), file.getContentType());
        if (!file.isEmpty()) {
            String result = null;

            if (isPublic) {
                result = fileService.uploadInternalResource(entityId,name,file);
            } else {
                result = fileService.uploadPrivateResource(entityId, name, file);
            }
            if (result != null && !result.isEmpty()) {
                return ResponseEntity.created(new URI(result)).build();
            }
            return ResponseEntity.badRequest().header("Failure", "errors.upload.config").build();
        }
        return ResponseEntity.badRequest().header("Failure", "errors.upload.empty").build();
    }
}
