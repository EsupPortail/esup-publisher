package org.esupportail.publisher.service.evaluators;

import java.io.Serializable;

import lombok.extern.slf4j.Slf4j;

import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class AuthenticatedUserEvaluation implements IEvaluation, Serializable {

	/** */
	private static final long serialVersionUID = 3585197571521497948L;

	public AuthenticatedUserEvaluation() {
		super();
	}

	@Override
	public boolean isApplicable(UserDTO userInfos) {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		final boolean authenticated = (auth != null) && auth.isAuthenticated()
				&& auth.getPrincipal().equals(userInfos.getLogin());
		if (log.isDebugEnabled()) {
			log.debug("AuthenticatedUserEvaluation on {} with auth state {}",
					userInfos.getLogin(), auth.toString());
		}
		return authenticated;
	}

}
