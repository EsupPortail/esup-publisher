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

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {
	}

	/**
	 * Get the login of the current user.
	 */
	public static String getCurrentLogin() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		UserDetails springSecurityUser = null;
		String userName = null;

		if (authentication != null) {
			if (authentication.getPrincipal() instanceof UserDetails) {
                springSecurityUser = (UserDetails) authentication.getPrincipal();
				userName = springSecurityUser.getUsername();
			} else if (authentication.getPrincipal() instanceof String) {
				userName = (String) authentication.getPrincipal();
			}
		}

		return userName;
	}

    /**
     * Get the UserDTO object of the current user.
     */
    public static UserDTO getCurrentUserDTO() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        CustomUserDetails springSecurityUser = null;
        UserDTO user = null;

        if (authentication != null) {
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                springSecurityUser = (CustomUserDetails) authentication.getPrincipal();
                user = springSecurityUser.getUser();
            }
        }

        return user;
    }
    /**
     * Get the User object of the current user.
     */
    public static User getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        CustomUserDetails springSecurityUser = null;
        User user = null;

        if (authentication != null) {
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                springSecurityUser = (CustomUserDetails) authentication.getPrincipal();
                user = springSecurityUser.getInternalUser();
            }
        }

        return user;
    }

    /**
     * Get the User object of the current user.
     */
    public static CustomUserDetails getCurrentUserDetails() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        CustomUserDetails springSecurityUser = null;

        if (authentication != null) {
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                springSecurityUser = (CustomUserDetails) authentication.getPrincipal();
            }
        }

        return springSecurityUser;
    }


	/**
	 * Check if a user is authenticated.
	 *
	 * @return true if the user is authenticated, false otherwise
	 */
	public static boolean isAuthenticated() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Collection<? extends GrantedAuthority> authorities = securityContext.getAuthentication().getAuthorities();
		return authorities != null
				&& !authorities.equals(AuthorityUtils.NO_AUTHORITIES)
				&& !authorities.contains(new SimpleGrantedAuthority(
						AuthoritiesConstants.ANONYMOUS));

	}

	/**
     * If the current user has a specific security role.
     */
    public static boolean isUserInRole(String role) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if(authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                return springSecurityUser.getAuthorities().contains(new SimpleGrantedAuthority(role));
            }
        }
        return false;
    }

    public static String makeDynamicCASServiceUrl(ServiceUrlHelper urlHelper, HttpServletRequest request) {
        String urlBase = urlHelper.getRootAppUrl(request);
        for (String url : urlHelper.getAuthorizedDomainNames()) {
            if (urlBase.startsWith(url)) return urlBase;
        }
        throw new AccessDeniedException("The server is unable to authenticate from requested url " + urlBase);
    }
}
