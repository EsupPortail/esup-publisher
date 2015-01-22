/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * Copyright (C) 2014 RECIA http://www.recia.fr
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 				http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.service.evaluators;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author GIP RECIA - Julien Gribonvald 27 oct. 2013
 */
@Service
@Slf4j
@ToString
public class UserGroupFromUserInfosEvaluation implements IEvaluation,
		Serializable {

	/** */
	private static final long serialVersionUID = -8568457660391132807L;

	private String group;

	private String userGroupAttribute;

	/**
	 * Empty constructor.
	 */
	public UserGroupFromUserInfosEvaluation() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param group
	 *            The group evaluator.
	 */
	public UserGroupFromUserInfosEvaluation(@NotNull final String group,
			@NotNull final String userGroupAttribute) {
		super();
		this.group = group;
		this.userGroupAttribute = userGroupAttribute;
	}

	@Override
	public boolean isApplicable(@NotNull final UserDTO userInfos) {
		Assert.notEmpty(userInfos.getAttributes(),
				"User Attributes not loaded from source.");
		final List<String> attribs = userInfos.getAttributes().get(
				userGroupAttribute);
		if (attribs == null || attribs.isEmpty())
			return false;

		if (log.isDebugEnabled()) {
			log.debug(this.toString()
					+ " evaluation over UserMultivaluedAttributes on "
					+ userGroupAttribute + " over values " + attribs.toString());
		}

		// test on startWith as some groups in IsMemberOf has only a part the
		// real group name.
		for (String val : attribs) {
			if (val.startsWith(group))
				return true;
		}

		return false;
	}

}