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

import org.esupportail.publisher.domain.enums.StringEvaluationMode;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author GIP RECIA - Julien Gribonvald 14 oct. 2013
 */
@Service
@Slf4j
@ToString(callSuper = true)
public class UserMultivaluedAttributesEvaluation extends
		UserAttributesEvaluation implements IEvaluation, Serializable {

	/** */
	private static final long serialVersionUID = -6869851713317041930L;

	/**
	 * Empty Constructor.
	 */
	public UserMultivaluedAttributesEvaluation() {
		super();
	}

	/**
	 * Contructor.
	 */
	public UserMultivaluedAttributesEvaluation(@NotNull final String attribute,
			@NotNull final String value,
			@NotNull final StringEvaluationMode mode) {
		super(attribute, value, mode);
	}

	/**
	 * @see org.esupportail.portlet.tools.evaluators.IEvaluator#isApplicable(javax.portlet.PortletRequest)
	 */
	@Override
	public boolean isApplicable(@NotNull final UserDTO userInfos) {
		Assert.notEmpty(userInfos.getAttributes(),
				"User Attributes not loaded from source !");
		final List<String> attribs = userInfos.getAttributes().get(
				this.getAttribute());
		if (attribs == null || attribs.isEmpty())
			return false;

		if (log.isDebugEnabled()) {
			log.debug(this.toString() + " evaluation over values "
					+ attribs.toString());
		}

		// for tests other than 'exists' the attribute must be defined
		if ((attribs == null || attribs.isEmpty())
				&& !StringEvaluationMode.EXISTS.equals(this.getMode()))
			return false;

		if (StringEvaluationMode.EQUALS.equals(this.getMode()))
			return attribs.contains(this.getValue());
		if (StringEvaluationMode.EXISTS.equals(this.getMode()))
			return attribs != null && !attribs.isEmpty();
		if (StringEvaluationMode.STARTS_WITH.equals(this.getMode())) {
			for (String val : attribs) {
				if (val.startsWith(this.getValue()))
					return true;
			}
		}
		if (StringEvaluationMode.ENDS_WITH.equals(this.getMode())) {
			for (String val : attribs) {
				if (val.endsWith(this.getValue()))
					return true;
			}
		}
		if (StringEvaluationMode.CONTAINS.equals(this.getMode())) {
			for (String val : attribs) {
				if (val.indexOf(this.getValue()) != -1)
					return true;
			}
		}
		if (StringEvaluationMode.MATCH.equals(this.getMode())) {
			for (String val : attribs) {
				if (val.matches(this.getValue()))
					return true;
			}
		}

		return false;
	}

}
