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

import org.esupportail.publisher.domain.ExternalFeed;
import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.esupportail.publisher.repository.ExternalFeedRepository;
import org.esupportail.publisher.security.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ExternalFeed.
 */
@RestController
@RequestMapping("/api")
public class ExternalFeedResource {

    private final Logger log = LoggerFactory.getLogger(ExternalFeedResource.class);

    @Inject
    private ExternalFeedRepository externalFeedRepository;

    /**
     * POST  /externalFeeds -> Create a new externalFeed.
     */
    @RequestMapping(value = "/externalFeeds",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    public ResponseEntity<Void> create(@RequestBody ExternalFeed externalFeed) throws URISyntaxException {
        log.debug("REST request to save ExternalFeed : {}", externalFeed);
        if (externalFeed.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new externalFeed cannot already have an ID").build();
        }
        externalFeedRepository.save(externalFeed);
        return ResponseEntity.created(new URI("/api/externalFeeds/" + externalFeed.getId())).build();
    }

    /**
     * PUT  /externalFeeds -> Updates an existing externalFeed.
     */
    @RequestMapping(value = "/externalFeeds",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    public ResponseEntity<Void> update(@RequestBody ExternalFeed externalFeed) throws URISyntaxException {
        log.debug("REST request to update ExternalFeed : {}", externalFeed);
        if (externalFeed.getId() == null) {
            return create(externalFeed);
        }
        externalFeedRepository.save(externalFeed);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /externalFeeds -> get all the externalFeeds.
     */
    @RequestMapping(value = "/externalFeeds",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    public List<ExternalFeed> getAll() {
        log.debug("REST request to get all ExternalFeeds");
        return externalFeedRepository.findAll();
    }

    /**
     * GET  /externalFeeds/:id -> get the "id" externalFeed.
     */
    @RequestMapping(value = "/externalFeeds/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    public ResponseEntity<ExternalFeed> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get ExternalFeed : {}", id);
        Optional<ExternalFeed> optionalExternalFeed =  externalFeedRepository.findById(id);
        ExternalFeed externalFeed = optionalExternalFeed == null || !optionalExternalFeed.isPresent()? null : optionalExternalFeed.get();
        if (externalFeed == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(externalFeed, HttpStatus.OK);
    }

    /**
     * DELETE  /externalFeeds/:id -> delete the "id" externalFeed.
     */
    @RequestMapping(value = "/externalFeeds/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete ExternalFeed : {}", id);
        externalFeedRepository.deleteById(id);
    }
}
