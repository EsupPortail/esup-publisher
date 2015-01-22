package org.esupportail.publisher.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import lombok.Getter;

import org.esupportail.publisher.service.bean.IAuthoritiesDefinition;
import org.esupportail.publisher.service.evaluators.IEvaluation;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

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
