package org.esupportail.publisher.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.repository.RedactorRepository;
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

/**
 * REST controller for managing Redactor.
 */
@RestController
@RequestMapping("/api")
public class RedactorResource {

    private final Logger log = LoggerFactory.getLogger(RedactorResource.class);

    @Inject
    private RedactorRepository redactorRepository;

    /**
     * POST  /redactors -> Create a new redactor.
     */
    @RequestMapping(value = "/redactors",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Redactor redactor) throws URISyntaxException {
        log.debug("REST request to save Redactor : {}", redactor);
        if (redactor.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new redactor cannot already have an ID").build();
        }
        redactorRepository.save(redactor);
        return ResponseEntity.created(new URI("/api/redactors/" + redactor.getId())).build();
    }

    /**
     * PUT  /redactors -> Updates an existing redactor.
     */
    @RequestMapping(value = "/redactors",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Redactor redactor) throws URISyntaxException {
        log.debug("REST request to update Redactor : {}", redactor);
        if (redactor.getId() == null) {
            return create(redactor);
        }
        redactorRepository.save(redactor);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /redactors -> get all the redactors.
     */
    @RequestMapping(value = "/redactors",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER)
    @Timed
    public List<Redactor> getAll() {
        log.debug("REST request to get all Redactors");
        return redactorRepository.findAll();
    }

    /**
     * GET  /redactors/:id -> get the "id" redactor.
     */
    @RequestMapping(value = "/redactors/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER)
    @Timed
    public ResponseEntity<Redactor> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Redactor : {}", id);
        Redactor redactor = redactorRepository.findOne(id);
        if (redactor == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(redactor, HttpStatus.OK);
    }

    /**
     * DELETE  /redactors/:id -> delete the "id" redactor.
     */
    @RequestMapping(value = "/redactors/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Redactor : {}", id);
        redactorRepository.delete(id);
    }
}
