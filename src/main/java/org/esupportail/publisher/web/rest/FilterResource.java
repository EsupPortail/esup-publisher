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

import org.esupportail.publisher.domain.Filter;
import org.esupportail.publisher.repository.FilterRepository;
import org.esupportail.publisher.security.SecurityConstants;

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
 * REST controller for managing Filter.
 */
@RestController
@RequestMapping("/api")
public class FilterResource {

    private final Logger log = LoggerFactory.getLogger(FilterResource.class);

    @Inject
    private FilterRepository filterRepository;

    /**
     * POST  /filters -> Create a new filter.
     */
    @RequestMapping(value = "/filters",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    public ResponseEntity<Void> create(@RequestBody Filter filter) throws URISyntaxException {
        log.debug("REST request to save Filter : {}", filter);
        if (filter.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new filter cannot already have an ID").build();
        }
        filterRepository.save(filter);
        return ResponseEntity.created(new URI("/api/filters/" + filter.getId())).build();
    }

    /**
     * PUT  /filters -> Updates an existing filter.
     */
    @RequestMapping(value = "/filters",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    public ResponseEntity<Void> update(@RequestBody Filter filter) throws URISyntaxException {
        log.debug("REST request to update Filter : {}", filter);
        if (filter.getId() == null) {
            return create(filter);
        }
        filterRepository.save(filter);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /filters -> get all the filters.
     */
    @RequestMapping(value = "/filters",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    public List<Filter> getAll() {
        log.debug("REST request to get all Filters");
        return filterRepository.findAll();
    }

    /**
     * GET  /filters/:id -> get the "id" filter.
     */
    @RequestMapping(value = "/filters/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    public ResponseEntity<Filter> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Filter : {}", id);
        Optional<Filter> optionalFilter =  filterRepository.findById(id);
        Filter filter = optionalFilter.orElse(null);
        if (filter == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(filter, HttpStatus.OK);
    }

    /**
     * DELETE  /filters/:id -> delete the "id" filter.
     */
    @RequestMapping(value = "/filters/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Filter : {}", id);
        filterRepository.deleteById(id);
    }
}