package org.esupportail.publisher.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.PermissionOnContext;
import org.esupportail.publisher.domain.QPermissionOnContext;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.repository.PermissionOnContextRepository;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.security.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * REST controller for managing PermissionOnContext.
 */
@RestController
@RequestMapping("/api")
public class PermissionOnContextResource {

	private final Logger log = LoggerFactory
			.getLogger(PermissionOnContextResource.class);

	@Inject
	private PermissionOnContextRepository permissionOnContextRepository;

    private QPermissionOnContext pObj = QPermissionOnContext.permissionOnContext;

    @Inject
    private IPermissionService permissionService;

	// @Inject
	// private EvaluatorRepository<AbstractEvaluator> evaluatorRepository;

	/**
	 * POST /permissionOnContexts -> Create a new permissionOnContext.
	 */
    @RequestMapping(value = "/permissionOnContexts",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && @permissionService.canEditCtxPerms(authentication, #permission.context)")
	@Timed
    public ResponseEntity<Void> create(@RequestBody PermissionOnContext permission) throws URISyntaxException {
        log.debug("REST request to save PermissionOnContext : {}", permission);
        if (permission.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new permissionOnContext cannot already have an ID").build();
        }
        permissionOnContextRepository.save(permission);
        return ResponseEntity.created(new URI("/api/permissionOnContexts/" + permission.getId())).build();
    }

    /**
     * PUT  /permissionOnContexts -> Updates an existing permissionOnContext.
     */
    @RequestMapping(value = "/permissionOnContexts",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && @permissionService.canEditCtxPerms(authentication, #permission.context)")
    @Timed
    public ResponseEntity<Void> update(@RequestBody PermissionOnContext permission) throws URISyntaxException {
        log.debug("REST request to update PermissionOnContext : {}", permission);
        if (permission.getId() == null) {
            return create(permission);
        }
        permissionOnContextRepository.save(permission);
        return ResponseEntity.ok().build();
	}

	/**
	 * GET /permissionOnContexts -> get all the permissionOnContexts.
	 */
	@RequestMapping(value = "/permissionOnContexts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER )
    @PostFilter("hasPermission(filterObject.context.keyId, filterObject.context.keyType, '" + SecurityConstants.PERM_LOOKOVER + "')")
	@Timed
	public List<PermissionOnContext> getAll() {
		log.debug("REST request to get all PermissionOnContexts");
		return permissionOnContextRepository.findAll();
	}

    /**
     * GET /permissionOnContexts/:ctxType/:ctxId -> get the "contextKey" permissionOnContext.
     */
    @RequestMapping(value = "/permissionOnContexts/{ctx_type}/{ctx_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && hasPermission(#id,  #type, '" + SecurityConstants.PERM_MANAGER + "')")
    @Timed
    public List<PermissionOnContext> getAllOf(@PathVariable("ctx_type") ContextType type, @PathVariable("ctx_id") Long id) {
        log.debug("REST request to get PermissionOnContext : CtxKey [{}, {}]", type, id);
        return Lists.newArrayList(permissionOnContextRepository.findAll(pObj.context.eq(new ContextKey(id, type))));
    }


    /**
     * GET  /permissionOnContexts -> get all the permissionOnContexts.
     */
 //   @RequestMapping(value = "/permissionOnContexts",
 //           method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    @Timed
//    public ResponseEntity<List<PermissionOnContext>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
//                                  @RequestParam(value = "per_page", required = false) Integer limit)
 //       throws URISyntaxException {
//        Page<PermissionOnContext> page = permissionOnContextRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/permissionOnContexts", offset, limit);
//        return new ResponseEntity<List<PermissionOnContext>>(page.getContent(), headers, HttpStatus.OK);
//    }

	/**
	 * GET /permissionOnContexts/:id -> get the "id" permissionOnContext.
	 */
	@RequestMapping(value = "/permissionOnContexts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER)
    @PostAuthorize("hasPermission(returnObject.body.context.keyId, returnObject.body.context.keyType, '" + SecurityConstants.PERM_LOOKOVER + "')")
	@Timed
	public ResponseEntity<PermissionOnContext> get(@PathVariable Long id,
			HttpServletResponse response) {
		log.debug("REST request to get PermissionOnContext : {}", id);
		PermissionOnContext permissionOnContext = permissionOnContextRepository.findOne(id);
		if (permissionOnContext == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(permissionOnContext, HttpStatus.OK);
	}

	/**
	 * DELETE /permissionOnContexts/:id -> delete the "id"
	 * permissionOnContext.
	 */
	@RequestMapping(value = "/permissionOnContexts/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER )
	@Timed
	public ResponseEntity delete(@PathVariable Long id) {
		log.debug("REST request to delete PermissionOnContext : {}", id);
        PermissionOnContext permissionOnContext = permissionOnContextRepository.findOne(id);
        if (permissionOnContext == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (permissionService.canEditCtxPerms(SecurityContextHolder.getContext().getAuthentication(), permissionOnContext.getContext())) {
            permissionOnContextRepository.delete(permissionOnContext);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
}
