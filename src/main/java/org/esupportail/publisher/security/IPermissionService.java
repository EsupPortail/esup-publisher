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
package org.esupportail.publisher.security;

import javax.validation.constraints.NotNull;

import com.mysema.commons.lang.Pair;
import com.mysema.query.types.Predicate;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.web.rest.dto.PermissionDTO;
import org.springframework.security.core.Authentication;

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

    boolean canModerateSomething(Authentication authentication);

    boolean hasAuthorizedChilds(Authentication authentication, @NotNull final ContextKey contextKey);

    boolean canHighlightInCtx(Authentication authentication, @NotNull final ContextKey contextKey);

}
