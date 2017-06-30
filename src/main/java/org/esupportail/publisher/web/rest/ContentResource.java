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

import java.net.URISyntaxException;

import javax.inject.Inject;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Evaluator.
 */
@RestController
@RequestMapping("/api")
public class ContentResource {

    private final Logger log = LoggerFactory.getLogger(ContentResource.class);

    @Inject
    private ContentService contentService;

    @Inject
    private ContentDTOFactory contentDTOFactory;

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
    public ResponseEntity<?> create(@RequestBody ContentDTO content) throws URISyntaxException {
        log.debug("REST request to save ContentDTO : classifications : {} \n item : {} \n targets : {} \n linkedFiles : {}",
            content.getClassifications(), content.getItem(), content.getTargets(), content.getLinkedFiles());
        if (content.getItem().getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new contents cannot already have an ID").build();
        }
        return contentService.saveContent(content);
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
    public ResponseEntity<?> update(@RequestBody ContentDTO content) throws URISyntaxException {
        log.debug("REST request to update ContentDTO : classifications : {} \n item : {} \n targets : {} \n linkedFiles : {}",
            content.getClassifications(), content.getItem(), content.getTargets(), content.getLinkedFiles());
        return contentService.saveContent(content);
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
            content = contentDTOFactory.from(id);
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
