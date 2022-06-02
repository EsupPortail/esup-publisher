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
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.esupportail.publisher.service.bean.IAuthoritiesDefinition;
import org.esupportail.publisher.service.evaluators.IEvaluation;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.UserDTO;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl implements IAuthorityService {

	@Inject
	@Getter
	private transient UserDTOFactory userDTOFactory;

	@Inject
	private RoleHierarchy roleHierarchy;

	@Inject
	public IAuthoritiesDefinition rolesDefs;

	public AuthorityServiceImpl() {
		super();
	}

	@Override
	public Collection<? extends GrantedAuthority> getUserAuthorities(final String userName) {
		if (userName == null) {
			return AuthorityUtils.createAuthorityList(AuthoritiesConstants.ANONYMOUS);
		}
		return getUserAuthorities(userDTOFactory.from(userName));
	}

	@Override
	public Collection<? extends GrantedAuthority> getUserAuthorities(UserDTO user) {
		if (user == null || !user.isFoundOnExternalSource()) {
			return AuthorityUtils.NO_AUTHORITIES;
		}
		List<GrantedAuthority> authorities = Lists.newArrayList();
		if (!rolesDefs.getAppRoles().isEmpty()) {
			for (Map.Entry<String, IEvaluation> map : rolesDefs.getAppRoles().entrySet()) {
				if (map.getValue().isApplicable(user)) {
					authorities.add(new SimpleGrantedAuthority(map.getKey()));
					break;
				}
			}
		}
		if (authorities.isEmpty()) {
			authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.AUTHENTICATED));
		}
		return roleHierarchy.getReachableGrantedAuthorities(authorities);
	}

}