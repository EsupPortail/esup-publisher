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

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.esupportail.publisher.domain.enums.StringEvaluationMode;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author GIP RECIA - Julien Gribonvald 27 oct. 2013
 */
@Service
@Slf4j
@ToString
@Getter
public class UserAttributesEvaluation implements IEvaluation, Serializable {

	/** */
	private static final long serialVersionUID = -1856324507448970264L;

	private String attribute;
	private String value;
	private StringEvaluationMode mode;

	/**
	 * Empty Constructor.
	 */
	public UserAttributesEvaluation() {
		super();
	}

	/**
	 * Constructor.
	 */
	public UserAttributesEvaluation(@NotNull final String attribute,
			@NotNull final String value,
			@NotNull final StringEvaluationMode mode) {
		super();
		this.attribute = attribute;
		this.value = value;
		this.mode = mode;
	}

	@Override
	public boolean isApplicable(@NotNull final UserDTO userInfos) {
		Assert.notEmpty(userInfos.getAttributes(),
				"User Attributes not loaded from source !");
		final List<String> attribs = userInfos.getAttributes().get(
				this.attribute);
		if (attribs == null || attribs.isEmpty()) {
			return false;
		}
		if (attribs.size() > 1) {
			throw new IllegalArgumentException(
					"Multivalued Attribute Evaluator should be used on '"
							+ this.attribute + "' ");
		}
		final String attrib = attribs.get(0);

		if (log.isDebugEnabled()) {
			log.debug(this.toString() + " evaluation over value '" + attrib
					+ "'");
		}

		// for tests other than 'exists' the attribute must be defined
		if (attrib == null && !StringEvaluationMode.EXISTS.equals(mode))
			return false;

		if (StringEvaluationMode.EQUALS.equals(mode))
			return attrib.equals(value);
		if (StringEvaluationMode.EXISTS.equals(mode))
			return attrib != null;
		if (StringEvaluationMode.STARTS_WITH.equals(mode))
			return attrib.startsWith(value);
		if (StringEvaluationMode.ENDS_WITH.equals(mode))
			return attrib.endsWith(value);
		if (StringEvaluationMode.CONTAINS.equals(mode))
			return (attrib.indexOf(value) != -1);
		if (StringEvaluationMode.MATCH.equals(mode))
			return attrib.matches(value);
		// will never get here
		return false;
	}

}
