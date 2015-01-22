package org.esupportail.publisher.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;
import org.esupportail.publisher.domain.AbstractPermission;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.repository.PermissionRepository;
import org.esupportail.publisher.repository.predicates.PermissionPredicates;
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
 * REST controller for managing AbstractPermission.
 */
@RestController
@RequestMapping("/api")
public class PermissionResource {

	private final Logger log = LoggerFactory
			.getLogger(PermissionResource.class);

	@Inject
	private PermissionRepository<AbstractPermission> permissionRepository;

    @Inject
    private IPermissionService permissionService;

	/**
	 * POST /permissions -> Create a new AbstractPermission.
	 */
    @RequestMapping(value = "/permissions", method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && @permissionService.canEditCtxPerms(authentication, #permission.context)")
	@Timed
    public ResponseEntity<Void> create(@RequestBody AbstractPermission permission) throws URISyntaxException {
        log.debug("REST request to save AbstractPermission : {}", permission);
        if (permission.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new permission cannot already have an ID").build();
        }
        permissionRepository.save(permission);
        return ResponseEntity.created(new URI("/api/permissions/" + permission.getId())).build();
    }

    /**
     * PUT  /permissions -> Updates an existing permission.
     */
    @RequestMapping(value = "/permissions", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && @permissionService.canEditCtxPerms(authentication, #permission.context)")
    @Timed
    public ResponseEntity<Void> update(@RequestBody AbstractPermission permission) throws URISyntaxException {
        log.debug("REST request to update AbstractPermission : {}", permission);
        if (permission.getId() == null) {
            return create(permission);
        }
        permissionRepository.save(permission);
        return ResponseEntity.ok().build();
	}

	/**
	 * GET /permissions -> get all the permissions.
	 */
	@RequestMapping(value = "/permissions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER )
    @PostFilter("hasPermission(filterObject.context.keyId, filterObject.context.keyType, '" + SecurityConstants.PERM_LOOKOVER + "')")
	@Timed
	public List<AbstractPermission> getAll() {
		log.debug("REST request to get all AbstractPermission");
		return permissionRepository.findAll();
	}

    /**
     * GET /permissions/:ctxType/:ctxId -> get the "ContextKey" permission.
     */
    @RequestMapping(value = "/permissions/{ctx_type}/{ctx_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && hasPermission(#id,  #type, '" + SecurityConstants.PERM_MANAGER + "')")
    @Timed
    public List<AbstractPermission> getAllOf(@PathVariable("ctx_type") ContextType type, @PathVariable("ctx_id") Long id,
                                              HttpServletResponse response) {
        log.debug("REST request to get AbstractPermission : CtxKey [{}, {}]", type, id);
        return Lists.newArrayList(permissionRepository.findAll(PermissionPredicates.AbstractPermOnCtx(type, id)));
    }


    /**
     * GET  /permissions -> get all the permissions.
     */
 //   @RequestMapping(value = "/permissions",
 //           method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    @Timed
//    public ResponseEntity<List<AbstractPermission>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
//                                  @RequestParam(value = "per_page", required = false) Integer limit)
 //       throws URISyntaxException {
//        Page<AbstractPermission> page = permissionRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/permissions", offset, limit);
//        return new ResponseEntity<List<AbstractPermission>>(page.getContent(), headers, HttpStatus.OK);
//    }

	/**
	 * GET /permissions/:id -> get the "id" permission.
	 */
	@RequestMapping(value = "/permissions/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER)
    @PostAuthorize("hasPermission(returnObject.body.context.keyId, returnObject.body.context.keyType, '" + SecurityConstants.PERM_LOOKOVER + "')")
	@Timed
	public ResponseEntity<AbstractPermission> get(@PathVariable Long id,
			HttpServletResponse response) {
		log.debug("REST request to get AbstractPermission : {}", id);
		AbstractPermission permission = permissionRepository.findOne(id);
		if (permission == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(permission, HttpStatus.OK);
	}

	/**
	 * DELETE /permissions/:id -> delete the "id"
	 * permission.
	 */
	@RequestMapping(value = "/permissions/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER )
	@Timed
    public ResponseEntity delete(@PathVariable Long id) {
        log.debug("REST request to delete AbstractPermission : {}", id);
        AbstractPermission permission = permissionRepository.findOne(id);
        if (permission == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (permissionService.canEditCtxPerms(SecurityContextHolder.getContext().getAuthentication(), permission.getContext())) {
            permissionRepository.delete(permission);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
