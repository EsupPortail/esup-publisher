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

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;

import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.PermissionOnClassificationWithSubjectList;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.repository.PermOnClassifWithSubjectsRepository;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing PermissionOnContext.
 */
@RestController
@RequestMapping("/api")
public class PermissionOnClassificationWithSubjectListResource {

	private final Logger log = LoggerFactory
			.getLogger(PermissionOnClassificationWithSubjectListResource.class);

	@Inject
	private PermOnClassifWithSubjectsRepository permissionRepository;

    @Inject
    private IPermissionService permissionService;

	// @Inject
	// private EvaluatorRepository<AbstractEvaluator> evaluatorRepository;

	/**
	 * POST /permissionOnClassificationWithSubjectLists -> Create a new PermissionOnClassificationWithSubjectList.
	 */
    @RequestMapping(value = "/permissionOnClassificationWithSubjectLists",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && @permissionService.canEditCtxPerms(authentication, #permission.context)")
	@Timed
    public ResponseEntity<Void> create(@RequestBody PermissionOnClassificationWithSubjectList permission) throws URISyntaxException {
        log.debug("REST request to save permissionOnClassificationWithSubjectLists : {}", permission);
        if (permission.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new PermissionOnClassificationWithSubjectList cannot already have an ID").build();
        }
        permissionRepository.save(permission);
        return ResponseEntity.created(new URI("/api/permissionOnClassificationWithSubjectLists/" + permission.getId())).build();
    }

    /**
     * PUT  /permissionOnClassificationWithSubjectLists -> Updates an existing PermissionOnClassificationWithSubjectList.
     */
    @RequestMapping(value = "/permissionOnClassificationWithSubjectLists",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && @permissionService.canEditCtxPerms(authentication, #permission.context)")
    @Timed
    public ResponseEntity<Void> update(@RequestBody PermissionOnClassificationWithSubjectList permission) throws URISyntaxException {
        log.debug("REST request to update permissionOnClassificationWithSubjectLists : {}", permission);
        if (permission.getId() == null) {
            return create(permission);
        }
        permissionRepository.save(permission);
        return ResponseEntity.ok().build();
	}

	/**
	 * GET /permissionOnClassificationWithSubjectLists -> get all the PermissionOnClassificationWithSubjectList.
	 */
	@RequestMapping(value = "/permissionOnClassificationWithSubjectLists", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER )
    @PostFilter("hasPermission(filterObject.context.keyId, filterObject.context.keyType, '" + SecurityConstants.PERM_LOOKOVER + "')")
	@Timed
	public List<PermissionOnClassificationWithSubjectList> getAll() {
		log.debug("REST request to get all permissionOnClassificationWithSubjectLists");
		return Lists.newArrayList(permissionRepository.findAll(PermissionPredicates.ofType(PermissionClass.CONTEXT_WITH_SUBJECTS, false)));
	}

    /**
     * GET /permissionOnClassificationWithSubjectLists/:ctxType/:ctxId -> get the "contextKey" PermissionOnClassificationWithSubjectList.
     */
    @RequestMapping(value = "/permissionOnClassificationWithSubjectLists/{ctx_type}/{ctx_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && hasPermission(#id,  #type, '" + SecurityConstants.PERM_MANAGER + "')")
    @Timed
    public List<PermissionOnClassificationWithSubjectList> getAllOf(@PathVariable("ctx_type") ContextType type, @PathVariable("ctx_id") Long id) {
        log.debug("REST request to get permissionOnClassificationWithSubjectLists : CtxKey [{}, {}]", type, id);
        return Lists.newArrayList(permissionRepository.findAll(PermissionPredicates.OnCtx(type, id, PermissionClass.CONTEXT_WITH_SUBJECTS, false)));
    }

	/**
	 * GET /permissionOnClassificationWithSubjectLists/:id -> get the "id" PermissionOnClassificationWithSubjectList.
	 */
	@RequestMapping(value = "/permissionOnClassificationWithSubjectLists/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER)
    @PostAuthorize("hasPermission(returnObject.body.context.keyId, returnObject.body.context.keyType, '" + SecurityConstants.PERM_LOOKOVER + "')")
	@Timed
	public ResponseEntity<PermissionOnClassificationWithSubjectList> get(@PathVariable Long id,
			HttpServletResponse response) {
		log.debug("REST request to get permissionOnClassificationWithSubjectLists : {}", id);
		Optional<PermissionOnClassificationWithSubjectList> optionalPermission =  permissionRepository.findById(id);
		PermissionOnClassificationWithSubjectList permission = optionalPermission == null || !optionalPermission.isPresent()? null : optionalPermission.get();
		if (permission == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(permission, HttpStatus.OK);
	}

	/**
	 * DELETE /permissionOnContexts/:id -> delete the "id"
	 * permissionOnContext.
	 */
	@RequestMapping(value = "/permissionOnClassificationWithSubjectLists/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_USER )
	@Timed
	public ResponseEntity delete(@PathVariable Long id) {
		log.debug("REST request to delete permissionOnClassificationWithSubjectLists : {}", id);
		Optional<PermissionOnClassificationWithSubjectList> optionalPermission =  permissionRepository.findById(id);
		PermissionOnClassificationWithSubjectList permission = optionalPermission == null || !optionalPermission.isPresent()? null : optionalPermission.get();
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
