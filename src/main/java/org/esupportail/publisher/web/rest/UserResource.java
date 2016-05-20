package org.esupportail.publisher.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.externals.ExternalUserHelper;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.security.SecurityConstants;
import org.esupportail.publisher.service.UserService;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.SearchSubjectFormDTO;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.esupportail.publisher.web.rest.dto.ValueResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

	private final Logger log = LoggerFactory.getLogger(UserResource.class);

	@Inject
	private UserRepository userRepository;

	@Inject
	private UserDTOFactory userFactory;

    @Inject
    private ExternalUserHelper externalUserHelper;

    @Inject
    private IPermissionService permissionService;

    @Inject
    private UserService userService;

	/**
	 * GET /users/:login -> get the "login" user.
	 */
	@RequestMapping(value = "/users/{login}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@RolesAllowed(AuthoritiesConstants.USER)
	public User getUser(@PathVariable String login, HttpServletResponse response) {
		log.debug("REST request to get User : {}", login);
		User user = userRepository.findOne(login);
		if (user == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		return user;
	}

	/**
	 * GET /users/extended/:login -> get the "login" user.
	 */
	@RequestMapping(value = "/users/extended/{login}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@RolesAllowed(AuthoritiesConstants.USER)
	public UserDTO getAllInfoUser(@PathVariable String login, HttpServletResponse response) {
		log.debug("REST request to get User : {}", login);
		UserDTO user = userFactory.from(login);
		if (user == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		return user;
	}

    /**
     * GET /users/attributes -> get the displayed user attributes.
     */
    @RequestMapping(value = "/users/attributes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public List<String> getShownUserAttributes(HttpServletResponse response) {
        log.debug("REST request to get List of user Attributes to show !");
        return Lists.newArrayList(externalUserHelper.getOtherUserDisplayedAttributes());
    }

    /**
     * GET /users/fnattributes -> get the functional user attributes.
     */
    @RequestMapping(value = "/users/fnattributes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public List<String> getFunctionalUserAttributes(HttpServletResponse response) {
        log.debug("REST request to get List of functional user Attributes !");
        return Lists.newArrayList(externalUserHelper.getAttributes());
    }

    /**
     * GET /users/perm/edit -> get the permission of editing a context.
     */
    @RequestMapping(value = "/users/perm/edit", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<ValueResource> userCanEditCtx(@RequestParam("keyId") Long ctxId, @RequestParam("keyType") ContextType ctxType) {
        log.debug("REST request to get if user has edit permission on context !");
        boolean canDo = permissionService.canEditCtx(SecurityContextHolder.getContext().getAuthentication(), new ContextKey(ctxId, ctxType));
        return new ResponseEntity<>(new ValueResource(canDo), HttpStatus.OK);
    }

    /**
     * GET /users/perm/editPerms -> get the permission of editing permisions of a context.
     */
    @RequestMapping(value = "/users/perm/editPerms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<ValueResource> userCanEditCtxPerms(@RequestParam("keyId") Long ctxId, @RequestParam("keyType") ContextType ctxType) {
        log.debug("REST request to get if user has edit rights on permission on context !");
        boolean canDo = permissionService.canEditCtxPerms(SecurityContextHolder.getContext().getAuthentication(), new ContextKey(ctxId, ctxType));
        return new ResponseEntity<>(new ValueResource(canDo), HttpStatus.OK);
    }

    /**
     * GET /users/perm/editTargets -> get the permission of editing permisions of a context.
     */
    @RequestMapping(value = "/users/perm/editTargets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<ValueResource> userCanEditCtxTargets(@RequestParam("keyId") Long ctxId, @RequestParam("keyType") ContextType ctxType) {
        log.debug("REST request to get if user has edit rights on permission on context !");
        boolean canDo = permissionService.canEditCtxTargets(SecurityContextHolder.getContext().getAuthentication(), new ContextKey(ctxId, ctxType));
        return new ResponseEntity<>(new ValueResource(canDo), HttpStatus.OK);
    }

    /**
     * GET /users/perm/delete -> get the permission of deleting a context.
     */
    @RequestMapping(value = "/users/perm/delete", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<ValueResource> userCanDeleteCtx(@RequestParam("keyId") Long ctxId, @RequestParam("keyType") ContextType ctxType) {
        log.debug("REST request to get List of user Attributes to show !");
        boolean canDo = permissionService.canDeleteCtx(SecurityContextHolder.getContext().getAuthentication(), new ContextKey(ctxId, ctxType));
        return new ResponseEntity<>(new ValueResource(canDo), HttpStatus.OK);
    }
    /**
     * GET /users/perm/createin -> get the permission of creating in a context.
     */
    @RequestMapping(value = "/users/perm/createin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<ValueResource> userCanCreateInCtx(@RequestParam("keyId") Long ctxId, @RequestParam("keyType") ContextType ctxType) {
        log.debug("REST request to get List of user Attributes to show !");
        boolean canDo = permissionService.canCreateInCtx(SecurityContextHolder.getContext().getAuthentication(), new ContextKey(ctxId, ctxType));
        return new ResponseEntity<>(new ValueResource(canDo), HttpStatus.OK);
    }

//    /**
//     * GET /users/search/:param -> get the "login" user.
//     */
//    @RequestMapping(value = "/users/search/{ctx_id}/{ctx_type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Timed
//    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
//        + " && hasPermission(#ctxId,  #ctxType, '" + SecurityConstants.PERM_LOOKOVER + "')")
//    public ResponseEntity<List<UserDTO>> searchUsers(@PathVariable("ctx_id") Long ctxId, @PathVariable("ctx_type") ContextType ctxType,
//                                                     @RequestParam(value = "subctxs", required = false) List<ContextKey> ctxs, @RequestParam("criteria") String search) {
//        log.debug("REST request to search Users with value {} in contextKey[keyType ={},keyId={}] and subContexts {}", search, ctxType, ctxId, ctxs);
//        List<UserDTO> users = userService.getUserFromSearchInCtx(new ContextKey(ctxId, ctxType), ctxs, search);
//        if (users == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(users,HttpStatus.OK);
//    }

    /**
     * POST /users/search/:param -> get the "login" user.
     */
    @RequestMapping(value = "/users/search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && hasPermission(#form.context.keyId, #form.context.keyType, '" + SecurityConstants.PERM_LOOKOVER + "')")
    public ResponseEntity<List<UserDTO>> searchUsersBis(@RequestBody SearchSubjectFormDTO form) {
        log.debug("REST request to search Users with params {}", form);
        List<UserDTO> users = userService.getUserFromSearchInCtx(form.getContext(), form.getSubContexts(), form.getSearch());
        if (users == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users,HttpStatus.OK);
    }

}
