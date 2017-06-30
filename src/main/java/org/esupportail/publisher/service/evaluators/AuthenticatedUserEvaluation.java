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
