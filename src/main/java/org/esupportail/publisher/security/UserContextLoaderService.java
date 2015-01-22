package org.esupportail.publisher.security;

import java.util.Collection;

import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public interface UserContextLoaderService {

	void loadUserTree(Authentication authentication);

	void loadUserTree(final UserDTO user,
			final Collection<? extends GrantedAuthority> authorities);

}
