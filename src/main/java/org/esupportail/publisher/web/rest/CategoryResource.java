package org.esupportail.publisher.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.esupportail.publisher.domain.Category;
import org.esupportail.publisher.repository.CategoryRepository;
import org.esupportail.publisher.security.SecurityConstants;
import org.esupportail.publisher.service.bean.UserContextTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * REST controller for managing Category.
 */
@RestController
@RequestMapping("/api")
public class CategoryResource {

    private final Logger log = LoggerFactory.getLogger(CategoryResource.class);

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    public UserContextTree userSessionTree;

    /**
     * POST  /categorys -> Create a new category.
     */
    @RequestMapping(value = "/categorys",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && @permissionService.canCreateInCtx(authentication, #category.publisher.contextKey)")
    @Timed
    public ResponseEntity<Void> create(@RequestBody Category category) throws URISyntaxException {
        log.debug("REST request to save Category : {}", category);
        if (category.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new category cannot already have an ID").build();
        }
        categoryRepository.save(category);
        //userSessionTree.addCreatedCtx(category.getContextKey(), true, category.getPublisher().getContextKey());
        userSessionTree.cleanup();
        return ResponseEntity.created(new URI("/api/categorys/" + category.getId())).build();
    }

    /**
     * PUT  /categorys -> Updates an existing category.
     */
    @RequestMapping(value = "/categorys",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && (@permissionService.canCreateInCtx(authentication, #category.publisher.contextKey) || @permissionService.canEditCtx(authentication, #category.contextKey))")
    @Timed
    public ResponseEntity<Void> update(@RequestBody Category category) throws URISyntaxException {
        log.debug("REST request to update Category : {}", category);
        if (category.getId() == null) {
            return create(category);
        }
        categoryRepository.save(category);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /categorys -> get all the categorys.
     */
    @RequestMapping(value = "/categorys",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER)
    @PostFilter("hasPermission(filterObject, '" + SecurityConstants.PERM_LOOKOVER + "')")
    @Timed
    public List<Category> getAll() {
        log.debug("REST request to get all Categorys");
        return categoryRepository.findAll();
    }

    /**
     * GET  /categorys/:id -> get the "id" category.
     */
    @RequestMapping(value = "/categorys/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && hasPermission(#id,  '" + SecurityConstants.CTX_CATEGORY + "', '" + SecurityConstants.PERM_LOOKOVER + "')")
    @Timed
    public ResponseEntity<Category> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Category : {}", id);
        Category category = categoryRepository.findOne(id);
        if (category == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    /**
     * DELETE  /categorys/:id -> delete the "id" category.
     */
    @RequestMapping(value = "/categorys/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && @permissionService.canDeleteCtx(authentication, #id, '" +  SecurityConstants.CTX_CATEGORY + "')")
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Category : {}", id);
        categoryRepository.delete(id);
    }
}
