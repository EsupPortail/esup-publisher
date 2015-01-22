package org.esupportail.publisher.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.esupportail.publisher.security.SecurityConstants;
import org.esupportail.publisher.service.ContentService;
import org.esupportail.publisher.service.bean.UserSearchs;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.ContentDTOFactory;
import org.esupportail.publisher.web.rest.dto.ContentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URISyntaxException;

/**
 * REST controller for managing Evaluator.
 */
@RestController
@RequestMapping("/api")
public class ContentResource {

    private final Logger log = LoggerFactory.getLogger(ContentResource.class);

    @Inject
    private ContentService contentService;

    @Inject ContentDTOFactory ContentDTOFactory;

    @Inject
    private UserSearchs userSearchResults;

    /**
     * POST  /contents -> Create a new content.
     */
    @RequestMapping(value = "/contents",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER)
    @Timed
    public ResponseEntity<Void> create(@RequestBody ContentDTO content) throws URISyntaxException {
        log.debug("REST request to save ContentDTO : classifications : {} \n item : {} \n targets {}", content.getClassifications(), content.getItem(), content.getTargets());
        return contentService.createContent(content);
    }

    /**
     * PUT  /contents -> Updates an existing content.
     */
    @RequestMapping(value = "/contents",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && @permissionService.canEditCtx(authentication, #content.item.contextKey)")
    @Timed
    public ResponseEntity<Void> update(@RequestBody ContentDTO content) throws URISyntaxException {
        log.debug("REST request to update ContentDTO : classifications : {} \n item : {} \n targets {}", content.getClassifications(), content.getItem(), content.getTargets());
        return contentService.updateContent(content);
    }

    /**
     * GET  /contents/:id -> get the "id" content.
     */
    @RequestMapping(value = "/contents/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && hasPermission(#id,  '" + SecurityConstants.CTX_ITEM + "', '" + SecurityConstants.PERM_CONTRIBUTOR + "')")
    @Timed
    public ResponseEntity<ContentDTO> get(@PathVariable Long id) {
        log.debug("REST request to get ContentDTO : {}", id);
        ContentDTO content = null;
        try {
            content = ContentDTOFactory.from(id);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (content == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(content, HttpStatus.OK);
    }
    /**
     * DELETE  /contents/:id -> delete the "id" content.
     */
    @RequestMapping(value = "/contents/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && @permissionService.canDeleteCtx(authentication, #id, '" +  SecurityConstants.CTX_ITEM + "')")
    //+ " && (hasPermission(#item, '" + SecurityConstants.PERM_EDITOR + "') || #item.createdBy.login.equals(principal.username))")
    //+ " && hasPermission(#id,  '" + SecurityConstants.CTX_ITEM + "', '" + SecurityConstants.PERM_EDITOR + "')")
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Content : {}", id);
        contentService.deleteContent(id);
    }


}
