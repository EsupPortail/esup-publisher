package org.esupportail.publisher.security;

import com.mysema.commons.lang.Pair;
import com.mysema.query.types.Predicate;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.web.rest.dto.PermissionDTO;
import org.springframework.security.core.Authentication;

import javax.validation.constraints.NotNull;

public interface IPermissionService {

	PermissionType getRoleOfUserInContext(Authentication authentication, @NotNull final ContextKey contextKey);

	Pair<PermissionType, PermissionDTO> getPermsOfUserInContext(Authentication authentication, @NotNull final ContextKey contextKey);

	// Role getRoleOfUserInContext(UserDTO from,
	// Collection<? extends GrantedAuthority> authorities,
	// ContextKey contextKey);

    Predicate filterAuthorizedAllOfContextType(Authentication authentication, @NotNull final ContextType contextType,
                                               @NotNull final PermissionType permissionType, @NotNull final Predicate predicate);

    Predicate filterAuthorizedChildsOfContext(Authentication authentication, @NotNull final ContextKey contextKey,
                                              @NotNull final PermissionType permissionType, @NotNull final Predicate predicate);

    boolean canCreateInCtx(Authentication authentication, @NotNull final ContextKey contextKey);

    boolean canEditCtx(Authentication authentication, @NotNull final ContextKey contextKey);

    boolean canDeleteCtx(Authentication authentication, @NotNull final ContextKey contextKey);

    boolean canCreateInCtx(Authentication authentication, @NotNull final long contextId, @NotNull final ContextType contextType);

    boolean canEditCtx(Authentication authentication, @NotNull final long contextId, @NotNull final ContextType contextType);

    boolean canDeleteCtx(Authentication authentication, @NotNull final long contextId, @NotNull final ContextType contextType);

    boolean canEditCtxPerms (Authentication authentication, @NotNull final ContextKey contextKey);

    boolean canEditCtxTargets (Authentication authentication, @NotNull final ContextKey contextKey);


    boolean hasAuthorizedChilds(Authentication authentication, @NotNull final ContextKey contextKey);

}
