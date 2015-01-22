package org.esupportail.publisher.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.esupportail.publisher.domain.Filter;
import org.esupportail.publisher.repository.FilterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
    @Timed
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
    @Timed
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
    @Timed
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
    @Timed
    public ResponseEntity<Filter> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Filter : {}", id);
        Filter filter = filterRepository.findOne(id);
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
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Filter : {}", id);
        filterRepository.delete(id);
    }
}
