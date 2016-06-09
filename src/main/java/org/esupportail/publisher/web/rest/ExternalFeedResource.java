package org.esupportail.publisher.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.esupportail.publisher.domain.ExternalFeed;
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
    @Timed
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
    @Timed
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
    @Timed
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
    @Timed
    public ResponseEntity<ExternalFeed> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get ExternalFeed : {}", id);
        ExternalFeed externalFeed = externalFeedRepository.findOne(id);
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
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete ExternalFeed : {}", id);
        externalFeedRepository.delete(id);
    }
}
