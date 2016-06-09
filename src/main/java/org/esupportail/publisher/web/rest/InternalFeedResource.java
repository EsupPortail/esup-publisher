package org.esupportail.publisher.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.esupportail.publisher.domain.InternalFeed;
import org.esupportail.publisher.repository.InternalFeedRepository;
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
 * REST controller for managing InternalFeed.
 */
@RestController
@RequestMapping("/api")
public class InternalFeedResource {

    private final Logger log = LoggerFactory.getLogger(InternalFeedResource.class);

    @Inject
    private InternalFeedRepository internalFeedRepository;

    /**
     * POST  /internalFeeds -> Create a new internalFeed.
     */
    @RequestMapping(value = "/internalFeeds",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    @Timed
    public ResponseEntity<Void> create(@RequestBody InternalFeed internalFeed) throws URISyntaxException {
        log.debug("REST request to save InternalFeed : {}", internalFeed);
        if (internalFeed.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new internalFeed cannot already have an ID").build();
        }
        internalFeedRepository.save(internalFeed);
        return ResponseEntity.created(new URI("/api/internalFeeds/" + internalFeed.getId())).build();
    }

    /**
     * PUT  /internalFeeds -> Updates an existing internalFeed.
     */
    @RequestMapping(value = "/internalFeeds",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    @Timed
    public ResponseEntity<Void> update(@RequestBody InternalFeed internalFeed) throws URISyntaxException {
        log.debug("REST request to update InternalFeed : {}", internalFeed);
        if (internalFeed.getId() == null) {
            return create(internalFeed);
        }
        internalFeedRepository.save(internalFeed);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /internalFeeds -> get all the internalFeeds.
     */
    @RequestMapping(value = "/internalFeeds",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    @Timed
    public List<InternalFeed> getAll() {
        log.debug("REST request to get all InternalFeeds");
        return internalFeedRepository.findAll();
    }

    /**
     * GET  /internalFeeds/:id -> get the "id" internalFeed.
     */
    @RequestMapping(value = "/internalFeeds/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    @Timed
    public ResponseEntity<InternalFeed> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get InternalFeed : {}", id);
        InternalFeed internalFeed = internalFeedRepository.findOne(id);
        if (internalFeed == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(internalFeed, HttpStatus.OK);
    }

    /**
     * DELETE  /internalFeeds/:id -> delete the "id" internalFeed.
     */
    @RequestMapping(value = "/internalFeeds/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete InternalFeed : {}", id);
        internalFeedRepository.delete(id);
    }
}
