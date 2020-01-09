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

import com.codahale.metrics.annotation.Timed;

import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.Reader;
import org.esupportail.publisher.repository.ReaderRepository;
import org.esupportail.publisher.security.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Reader.
 */
@RestController
@RequestMapping("/api")
public class ReaderResource {

    private final Logger log = LoggerFactory.getLogger(ReaderResource.class);

    @Inject
    private ReaderRepository readerRepository;

    /**
     * POST  /readers -> Create a new reader.
     */
    @RequestMapping(value = "/readers",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Reader reader) throws URISyntaxException {
        log.debug("REST request to save Reader : {}", reader);
        if (reader.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new reader cannot already have an ID").build();
        }
        readerRepository.save(reader);
        return ResponseEntity.created(new URI("/api/readers/" + reader.getId())).build();
    }

    /**
     * PUT  /readers -> Updates an existing reader.
     */
    @RequestMapping(value = "/readers",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Reader reader) throws URISyntaxException {
        log.debug("REST request to update Reader : {}", reader);
        if (reader.getId() == null) {
            return create(reader);
        }
        readerRepository.save(reader);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /readers -> get all the readers.
     */
    @RequestMapping(value = "/readers",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER)
    @Timed
    public List<Reader> getAll() {
        log.debug("REST request to get all Readers");
        return readerRepository.findAll();
    }

    /**
     * GET  /readers/:id -> get the "id" reader.
     */
    @RequestMapping(value = "/readers/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER)
    @Timed
    public ResponseEntity<Reader> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Reader : {}", id);
        Optional<Reader> optionalReader =  readerRepository.findById(id);
        Reader reader = optionalReader == null || !optionalReader.isPresent()? null : optionalReader.get();
        if (reader == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(reader, HttpStatus.OK);
    }

    /**
     * DELETE  /readers/:id -> delete the "id" reader.
     */
    @RequestMapping(value = "/readers/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Reader : {}", id);
        readerRepository.deleteById(id);
    }
}
