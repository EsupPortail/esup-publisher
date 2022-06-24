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
import java.util.ArrayList;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.util.ClassificationList;
import org.esupportail.publisher.repository.ClassificationRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.predicates.ClassificationPredicates;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.security.SecurityConstants;
import org.esupportail.publisher.service.HighlightedClassificationService;
import org.esupportail.publisher.service.bean.HighlightedClassification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Predicate;

/**
 * REST controller for managing AbstractClassification.
 */
@RestController
@RequestMapping("/api")
public class ClassificationResource {

    private final Logger log = LoggerFactory.getLogger(ClassificationResource.class);

    @Inject
    private ClassificationRepository<AbstractClassification> classificationRepository;

    @Inject
    private PublisherRepository publisherRepository;

    @Inject
    private IPermissionService permissionService;

    @Inject
    private HighlightedClassificationService highlightedClassificationService;

    /**
     * POST  /classifications -> Create a new classification.
     */
    @RequestMapping(value = "/classifications",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && @permissionService.canCreateInCtx(authentication, #classification.publisher.contextKey)")
    public ResponseEntity<Void> create(@RequestBody AbstractClassification classification) throws URISyntaxException {
        log.debug("REST request to save AbstractClassification : {}", classification);
        if (classification.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new classification cannot already have an ID").build();
        }
        classificationRepository.save(classification);
        return ResponseEntity.created(new URI("/api/classifications/" + classification.getId())).build();
    }

    /**
     * PUT  /classifications -> Updates an existing classification.
     */
    @RequestMapping(value = "/classifications",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && (@permissionService.canCreateInCtx(authentication, #classification.publisher.contextKey) || @permissionService.canEditCtx(authentication, #classification.contextKey))")
    public ResponseEntity<Void> update(@RequestBody AbstractClassification classification) throws URISyntaxException {
        log.debug("REST request to update AbstractClassification : {}", classification);
        if (classification.getId() == null) {
            return create(classification);
        }
        classificationRepository.save(classification);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /classifications -> get all the classifications.
     */
    @RequestMapping(value = "/classifications",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER)
    @PostFilter("hasPermission(filterObject, '" + SecurityConstants.PERM_LOOKOVER + "')")
    public ClassificationList getAll(@RequestParam(value = "publisherId" , required = false) Long publisherId,
                                               @RequestParam(value = "isPublishing", required = false) Boolean isPublishing) {
        log.debug("REST request to get all AbstractClassifications");
        if (publisherId != null) {
    	    Optional<Publisher> optionalPublisher =  publisherRepository.findById(publisherId);
            Publisher publisher = optionalPublisher == null || !optionalPublisher.isPresent()? null : optionalPublisher.get();
            if (publisher == null)
                return new ClassificationList(new ArrayList<AbstractClassification>());
            Predicate where;
            Predicate filter;
            if (isPublishing != null && isPublishing) {
                if (publisher.getContext().getRedactor().getNbLevelsOfClassification() == 1) {
                    where = ClassificationPredicates.CategoryClassificationOfPublisher(publisherId);
                    filter = permissionService.filterAuthorizedAllOfContextType(SecurityContextHolder.getContext().getAuthentication(),
                        ContextType.CATEGORY, PermissionType.CONTRIBUTOR, where);
                } else {
                    where = ClassificationPredicates.AbstractFeedClassificationOfPublisher(publisherId);
                    filter = permissionService.filterAuthorizedAllOfContextType(SecurityContextHolder.getContext().getAuthentication(),
                        ContextType.FEED, PermissionType.CONTRIBUTOR, where);
                }
            } else {
                where = ClassificationPredicates.classificationsOfPublisher(publisherId);
                filter = where;
            }
            log.debug("Filter applied to obtain items : {}", filter.toString());

            return new ClassificationList(Lists.newArrayList(classificationRepository.findAll(filter,
                ClassificationPredicates.classifOrderByDisplayOrderType(publisher.getDefaultDisplayOrder()))));
        }
        return new ClassificationList(classificationRepository.findAll());
    }



    /**
     * GET  /classifications/:id -> get the "id" classification.
     */
    @RequestMapping(value = "/classifications/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER)
    @PostAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + "hasPermission(returnObject.body, '" + SecurityConstants.PERM_LOOKOVER + "')")
    public ResponseEntity<AbstractClassification> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get AbstractClassification : {}", id);
        Optional<AbstractClassification> optionalAbstractClassification =  classificationRepository.findById(id);
        AbstractClassification classification = optionalAbstractClassification == null || !optionalAbstractClassification.isPresent()? null : optionalAbstractClassification.get();
        if (classification == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(classification, HttpStatus.OK);
    }

    /**
     * DELETE  /classifications/:id -> delete the "id" classification.
     */
    @RequestMapping(value = "/classifications/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && (@permissionService.canDeleteCtx(authentication, #id, '" +  SecurityConstants.CTX_CATEGORY + "') || " +
        " @permissionService.canDeleteCtx(authentication, #id, '" +  SecurityConstants.CTX_FEED + "'))")
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete AbstractClassification : {}", id);
        classificationRepository.deleteById(id);
    }

    /**
     * DELETE  /classifications/highlighted -> get the "highlighted" classification.
     */
    @RequestMapping(value = "/classifications/highlighted",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER)
    public ResponseEntity<HighlightedClassification> getHighlight() {
        log.debug("REST request to get Highlight Classification.");
        return new ResponseEntity<>(highlightedClassificationService.getClassification(), HttpStatus.OK);
    }
}
