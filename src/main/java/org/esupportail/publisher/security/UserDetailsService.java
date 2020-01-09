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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.repository.externals.IExternalUserDao;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

/**
 * Authenticate a user from the database.
 */
@Service
@Slf4j
public class UserDetailsService implements
		AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

	@Inject
	@Getter
	private transient UserRepository userDao;

	@Inject
	@Getter
	private transient IExternalUserDao extDao;

	@Inject
	private transient IAuthorityService grantedAuthorityService;

	@Inject
	private transient UserDTOFactory userDTOFactory;

	@Override
	@Transactional
	public CustomUserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
		String login = token.getPrincipal().toString();
		log.debug("Authenticating {}", login);

		return loadUserByUsername(login);
	}

	@Transactional
	public CustomUserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		Optional<User> optionalUser = userDao.findById(userName);
		User internal = optionalUser == null || !optionalUser.isPresent() ? null : optionalUser.get();
		final IExternalUser external = extDao.getUserByUid(userName);
		if (external == null) {
			throw new UsernameNotFoundException(String.format(
					"User [%s] without permission !", userName));
		}
		UserDTO user = userDTOFactory.from(internal, external);
		Collection<? extends GrantedAuthority> authorities = grantedAuthorityService
				.getUserAuthorities(user);

		if (internal == null) {
			// TODO save the user if he has permissions before, so check perms !
			boolean isAtLeastUser = authorities
					.contains(new SimpleGrantedAuthority(
							AuthoritiesConstants.USER))
					|| authorities.contains(new SimpleGrantedAuthority(
							AuthoritiesConstants.ADMIN));
			if (authorities != null && !authorities.isEmpty() && isAtLeastUser) {
				internal = userDao.saveAndFlush(userDTOFactory.from(user));
				Optional<User> optionalU = userDao.findById(userName);
				internal = optionalU == null || !optionalU.isPresent() ? null : optionalU.get();
				if (internal == null) {
					log.error(
							"User with username {} could not be read back after being created.",
							userName);
					throw new UsernameNotFoundException(
							String.format(
									"User with username [%s] could not be read back after being created.",
									userName));
				}

			} else if (authorities == null || authorities.isEmpty()) {
				log.error("User {} without permission !", userName);
				throw new UsernameNotFoundException(String.format(
						"User [%s] without permission !", userName));
			} else if (!isAtLeastUser) {
				if (log.isDebugEnabled()) {
					log.debug("Loading a user without permissions defined as only Authenticated.");
				}
			}
		}
		return new CustomUserDetails(userDTOFactory.from(internal, external), internal,	authorities);
	}

	// @Inject
	// private UserRepository userRepository;

	// @Override
	// @Transactional
	// public UserDetails loadUserByUsername(final String login) {
	// log.debug("Authenticating {}", login);
	// String lowercaseLogin = login.toLowerCase();
	//
	// User userFromDatabase = userRepository.findOne(lowercaseLogin);
	// if (userFromDatabase == null) {
	// throw new UsernameNotFoundException("User " + lowercaseLogin
	// + " was not found in the database");
	// } else if (!userFromDatabase.isEnabled()) {
	// throw new UserNotActivatedException("User " + lowercaseLogin
	// + " is not authorized");
	// }
	//
	// Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
	// for (Authority authority : userFromDatabase.getAuthorities()) {
	// GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
	// authority.getName());
	// grantedAuthorities.add(grantedAuthority);
	// }
	//
	// return new org.springframework.security.core.userdetails.User(
	// lowercaseLogin, userFromDatabase.getPassword(),
	// grantedAuthorities);
	// }
}
