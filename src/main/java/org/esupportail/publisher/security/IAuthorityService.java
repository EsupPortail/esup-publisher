package org.esupportail.publisher.security;

import java.util.Collection;

import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.security.core.GrantedAuthority;

public interface IAuthorityService {

	Collection<? extends GrantedAuthority> getUserAuthorities(
			final String userName);

	Collection<? extends GrantedAuthority> getUserAuthorities(final UserDTO user);

}
