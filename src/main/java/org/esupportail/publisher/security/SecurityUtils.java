package org.esupportail.publisher.security;

import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

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
}
