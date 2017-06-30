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
package org.esupportail.publisher.service.bean;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.service.evaluators.IEvaluation;

import com.google.common.collect.Maps;

@Getter
@Setter
@Slf4j
public class AuthoritiesDefinition implements IAuthoritiesDefinition {

	/**
	 * Admins are super admin of the app.
	 */
	private IEvaluation admins;

	/**
	 * Users are users that can manage, edit or contributes.
	 */
	private IEvaluation users;

	public AuthoritiesDefinition() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.esupportail.publisher.service.beans.IRolesDefinition#getAppRoles()
	 */
	@Override
	public Map<String, IEvaluation> getAppRoles() {
		Map<String, IEvaluation> accessRoles = Maps.newLinkedHashMap();
		accessRoles.put(AuthoritiesConstants.ADMIN, admins);
		accessRoles.put(AuthoritiesConstants.USER, users);
		if (log.isDebugEnabled()) {
			log.debug("App Roles defined : {}", accessRoles.toString());
		}
		return accessRoles;
	}

}
