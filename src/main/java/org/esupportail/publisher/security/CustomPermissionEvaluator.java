package org.esupportail.publisher.security;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.IContext;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import javax.inject.Inject;
import java.io.Serializable;

@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {

	@Inject
	public IPermissionService permissionService;

	public CustomPermissionEvaluator() {
		super();
	}

	@Override
	public boolean hasPermission(Authentication authentication,
			Object targetDomainObject, Object permission) {

        if (!checkAuthentication(authentication)) {
            log.error("Authentication has something wrong, check that the user is authenticated and security is wheel implemented !");
            return false;
        }
        if (targetDomainObject == null || !(targetDomainObject instanceof IContext)){
            log.error("The object {} passed to check permission doesn't implements IContext interface.", targetDomainObject);
            throw new IllegalArgumentException("The object passed to check permission doesn't implements IContext interface.");
        }
        log.debug(
            "Call custom hasPermission with object {} and permission {} !",
            ((IContext) targetDomainObject).getContextKey(), permission);
		IContext context = (IContext) targetDomainObject;

		PermissionType RoleInCtx = permissionService.getRoleOfUserInContext(
				authentication, context.getContextKey());

		if (RoleInCtx == null)
			return false;

		PermissionType perm = PermissionType.valueOf((String) permission);

        return perm != null && RoleInCtx.getMask() >= perm.getMask();
	}

	@Override
	public boolean hasPermission(Authentication authentication,
			Serializable targetId, String targetType, Object permission) {
        if (!checkAuthentication(authentication)) {
            log.error("Authentication has something wrong, check that the user is authenticated and security is wheel implemented !");
            return false;
        }
		log.debug(
				"Call custom hasPermission with id {}, type {} and permission {} !",
				targetId, targetType, permission);
		ContextType ctxType = ContextType.valueOf(targetType);

		PermissionType RoleInCtx = permissionService.getRoleOfUserInContext(
				authentication, new ContextKey((Long) targetId, ctxType));

		if (RoleInCtx == null)
			return false;

		PermissionType perm = PermissionType.valueOf((String) permission);

        return perm != null && RoleInCtx.getMask() >= perm.getMask();
	}

    private boolean checkAuthentication(Authentication authentication){
        return authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof CustomUserDetails;
    }
}
