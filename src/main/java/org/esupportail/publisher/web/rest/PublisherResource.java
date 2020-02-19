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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.esupportail.publisher.domain.AbstractPermission;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.predicates.PublisherPredicates;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.security.SecurityConstants;
import org.esupportail.publisher.service.PublisherService;
import org.esupportail.publisher.web.rest.dto.ActionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

/**
 * REST controller for managing Publisher.
 */
@RestController
@RequestMapping("/api")
public class PublisherResource {

    private final Logger log = LoggerFactory.getLogger(PublisherResource.class);

    @Inject
    private PublisherRepository publisherRepository;

    @Inject
    private IPermissionService permissionService;

    @Inject
    private PublisherService publisherService;

    /**
     * POST  /publishers -> Create a new publisher.
     */
    @RequestMapping(value = "/publishers",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Publisher publisher) throws URISyntaxException {
        log.debug("REST request to save Publisher : {}", publisher);
        if (publisher.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new publisher cannot already have an ID").build();
        }
        publisher.setDisplayOrder(publisherRepository.getNextDisplayOrderInOrganization(publisher.getContext().getOrganization().getId()));
        publisherRepository.save(publisher);
        //userSessionTree.addCtx(publisher.getContextKey(), null, null, PermissionType.ADMIN);
        return ResponseEntity.created(new URI("/api/publishers/" + publisher.getId())).build();
    }

    /**
     * PUT  /publishers -> Updates an existing publisher.
     */
    @RequestMapping(value = "/publishers",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && hasPermission(#publisher, '" + SecurityConstants.PERM_MANAGER + "')")
    @Timed
    public ResponseEntity<Void> update(@RequestBody Publisher publisher) throws URISyntaxException {
        log.debug("REST request to update Publisher : {}", publisher);
        PermissionType permType = permissionService.getRoleOfUserInContext(SecurityContextHolder.getContext().getAuthentication(), publisher.getContextKey());
        if (permType == null) {
            log.warn("@PreAuthorize didn't work correctly, check Security !");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (publisher.getId() == null && PermissionType.ADMIN.getMask() <= permType.getMask()) {
            return create(publisher);
        } else if (publisher.getId() == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // a manager role can only update DisplayName and if Notifications are allowed
        if (PermissionType.MANAGER.getMask() <= permType.getMask()) {
        	Optional<Publisher> optionalPublisher =  publisherRepository.findById(publisher.getId());
        	Publisher model = optionalPublisher == null || !optionalPublisher.isPresent()? null : optionalPublisher.get();
            model.setPermissionType(publisher.getPermissionType());
            model.setDefaultDisplayOrder(publisher.getDefaultDisplayOrder());
            model.setUsed(publisher.isUsed());
            publisherRepository.save(publisher);
            return ResponseEntity.ok().build();
        }

        publisherService.doMove(publisher, publisher.getDisplayOrder());
        publisherRepository.save(publisher);
        return ResponseEntity.ok().build();
    }

    /**
     * PUT  /publishers -> Do update action on an existing publisher.
     */
    @RequestMapping(value = "/publishers", params = "action",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && hasPermission(#action.objectId, '" + SecurityConstants.CTX_PUBLISHER + "', '" + SecurityConstants.PERM_MANAGER + "')")
    @Timed
    public ResponseEntity<Void> doChange(@RequestBody ActionDTO action) {
    	Optional<Publisher> optionalPublisher =  publisherRepository.findById(action.getObjectId());
    	Publisher publisher = optionalPublisher == null || !optionalPublisher.isPresent()? null : optionalPublisher.get();
        log.debug("REST request with Action {} of Publisher : {}", action, publisher);
        if (publisher != null) {
            boolean doUpdate = false;
            switch (action.getAttribute()) {
                case "used": publisher.setUsed(Boolean.parseBoolean(action.getValue())); doUpdate=true; break;
                case "move": publisherService.doMove(publisher, Integer.parseInt(action.getValue()));; doUpdate=false; break;
            }
            if (doUpdate) {
                publisherRepository.save(publisher);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * GET  /publishers -> get all the publishers.
     */
    @RequestMapping(value = "/publishers",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER)
    @PostFilter("hasPermission(filterObject, '" + SecurityConstants.PERM_LOOKOVER + "')")
    @Timed
    public List<Publisher> getAll(@RequestParam(value = "organizationId" , required = false) Long organizationId,
                                  @RequestParam(value = "used" , required = false) Boolean used) {
        log.debug("REST request to get all Publishers");
        Predicate where = null;
        if (organizationId != null && used == null) {
            where = PublisherPredicates.AllOfOrganization(organizationId);
        } else if (organizationId != null) {
            where = new BooleanBuilder(PublisherPredicates.AllOfOrganization(organizationId)).and(PublisherPredicates.AllOfUsedState(true));
        } else if (used != null) {
            where = PublisherPredicates.AllOfUsedState(true);
        } else {
        	where = new BooleanBuilder();
        }
       return Lists.newArrayList(publisherRepository.findAll(where, PublisherPredicates.orderByOrganizations(), PublisherPredicates.orderByDisplayOrder()));
    }

    /**
     * GET  /publishers/:id -> get the "id" publisher.
     */
    @RequestMapping(value = "/publishers/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && hasPermission(#id, '" + SecurityConstants.CTX_PUBLISHER + "', '" + SecurityConstants.PERM_LOOKOVER + "')")
    @Timed
    public ResponseEntity<Publisher> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Publisher : {}", id);
        Optional<Publisher> optionalPublisher =  publisherRepository.findById(id);
    	Publisher publisher = optionalPublisher == null || !optionalPublisher.isPresent()? null : optionalPublisher.get();
        if (publisher == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(publisher, HttpStatus.OK);
    }

    /**
     * DELETE  /publishers/:id -> delete the "id" publisher.
     */
    @RequestMapping(value = "/publishers/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    //@PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
    //    + " && hasPermission(#id, '" + SecurityConstants.CTX_PUBLISHER + "', '" + SecurityConstants.PERM_MANAGER + "')")
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Publisher : {}", id);
        publisherRepository.deleteById(id);
    }
}
