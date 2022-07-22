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

import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.repository.OrganizationRepository;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.security.SecurityConstants;
import org.esupportail.publisher.service.OrganizationService;
import org.esupportail.publisher.service.bean.UserContextTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST controller for managing Organization.
 */
@RestController
@RequestMapping("/api")
public class OrganizationResource {

	private final Logger log = LoggerFactory.getLogger(OrganizationResource.class);

	@Inject
	private OrganizationRepository organizationRepository;
	@Inject
	private OrganizationService organizationService;

    @Inject
    private IPermissionService permissionService;

	@Inject
	public UserContextTree userSessionTree;

	/**
	 * POST /organizations -> Create a new organization.
	 */
	@RequestMapping(value = "/organizations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
	public ResponseEntity<Void> create(@RequestBody Organization organization) throws URISyntaxException {
		log.debug("REST request to save Organization : {}", organization);
		if (organization.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new organization cannot already have an ID").build();
		}
		organization.setDisplayOrder(organizationRepository.getNextDisplayOrder());
		organizationRepository.save(organization);
		//userSessionTree.addCtx(organization.getContextKey(), null, null, PermissionType.ADMIN);
		return ResponseEntity.created(new URI("/api/organizations/" + organization.getId())).build();
	}

	/**
	 * PUT  /organizations -> Updates an existing organization.
	 */
	@RequestMapping(value = "/organizations", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && hasPermission(#organization, '" + SecurityConstants.PERM_MANAGER + "')")
	public ResponseEntity<Void> update(@RequestBody Organization organization) throws URISyntaxException {
		log.debug("REST request to update Organization : {}", organization);
        PermissionType permType = permissionService.getRoleOfUserInContext(SecurityContextHolder.getContext().getAuthentication(), organization.getContextKey());
        if (permType == null) {
            log.warn("@PreAuthorize didn't work correctly, check Security !");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (organization.getId() == null && PermissionType.ADMIN.getMask() <= permType.getMask()) {
            return create(organization);
        } else if (organization.getId() == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // a manager role can only update DisplayName and if Notifications are allowed
		if (PermissionType.MANAGER.getMask() <= permType.getMask()) {
			Optional<Organization> optionalOrganization =  organizationRepository.findById(organization.getId());
			Organization model = optionalOrganization.orElse(null);
			if (model == null) {
				log.warn("Organization with id '{}' not found", organization.getId());
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
            model.setDisplayName(organization.getDisplayName());
            model.setAllowNotifications(organization.isAllowNotifications());
            organizationRepository.save(organization);
            return ResponseEntity.ok().build();
        }
		organizationService.doMove(organization.getId(), organization.getDisplayOrder());
		organizationRepository.save(organization);
		return ResponseEntity.ok().build();
	}

	// @RequestMapping(value = "/organizations/{id}", method =
	// RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	// public void moveOrder(@PathVariable Long id, @RequestBody MoveDTO pos) {
	// organizationService.doMove(id, pos.getPosition());
	// }

	@RequestMapping(value = "/organizations/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
	public void moveOrder(@PathVariable Long id, @RequestParam int pos) {
		organizationService.doMove(id, pos);
	}

	/**
	 * GET /organizations -> get all the organizations.
	 */
	@PreAuthorize(SecurityConstants.IS_ROLE_USER)
	@PostFilter("hasPermission(filterObject, '" + SecurityConstants.PERM_LOOKOVER + "')")
	@RequestMapping(value = "/organizations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Organization> getAll() {
		log.debug("REST request to get all Organizations");
		return organizationRepository.findAll(sortByDisplayOrderAsc());
	}

	/**
	     * GET  /organizations -> get all the organizations.
	     */
	//    @RequestMapping(value = "/organizations",
	//            method = RequestMethod.GET,
	//            produces = MediaType.APPLICATION_JSON_VALUE)
	//    public ResponseEntity<List<Organization>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
	//                                  @RequestParam(value = "per_page", required = false) Integer limit)
	//        throws URISyntaxException {
	//        Page<Organization> page = organizationRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/organizations", offset, limit);
	//        return new ResponseEntity<List<Organization>>(page.getContent(), headers, HttpStatus.OK);
	//    }

	/**
	 * GET /organizations/:id -> get the "id" organization.
	 */
	@PreAuthorize("hasPermission(#id, 'ORGANIZATION', '" + SecurityConstants.PERM_LOOKOVER + "')")
	@RequestMapping(value = "/organizations/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Organization> get(@PathVariable Long id) {
		log.debug("REST request to get Organization : {}", id);
		Optional<Organization> optionalOrganization =  organizationRepository.findById(id);
		Organization organization = optionalOrganization.orElse(null);
		if (organization == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(organization, HttpStatus.OK);
	}

	/**
	 * DELETE /organizations/:id -> delete the "id" organization.
	 */
	@PreAuthorize(SecurityConstants.IS_ROLE_ADMIN)
	@RequestMapping(value = "/organizations/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void delete(@PathVariable Long id) {
		log.debug("REST request to delete Organization : {}", id);
		organizationRepository.deleteById(id);
		userSessionTree.removeCtx(new ContextKey(id, ContextType.ORGANIZATION));
	}

	/**
	 * Returns a Sort object which sorts Organization in ascending order by
	 * using the displayOrder.
	 *
	 * @return
	 */
	private Sort sortByDisplayOrderAsc() {
		return Sort.by(Sort.Direction.ASC, "displayOrder");
	}
}