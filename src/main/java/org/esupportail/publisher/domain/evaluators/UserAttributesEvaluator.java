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
package org.esupportail.publisher.domain.evaluators;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.StringEvaluationMode;
import org.esupportail.publisher.service.evaluators.IEvaluationVisitor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author GIP RECIA - Julien Gribonvald 14 oct. 2013
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class", visible = true)
@JsonTypeName("USERATTRIBUTES")
@Entity
@DiscriminatorValue("UserAttributes")
public class UserAttributesEvaluator extends AbstractAttributeValueEvaluator {

	/** */
	private static final long serialVersionUID = -3906086605056903886L;

	/** The mode to evaluate the attribute. */
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(length = 12)
	private StringEvaluationMode mode;

	/**
	 * Constructor
	 */
	public UserAttributesEvaluator() {
		super();
	}

	/**
	 * @param attribute
	 * @param value
	 */
	public UserAttributesEvaluator(final String attribute, final String value,
			final StringEvaluationMode mode) {
		super(attribute, value);
		this.mode = mode;
	}

	public boolean isApplicable(IEvaluationVisitor visitor) {
		return visitor.isApplicable(this);
	}

}
