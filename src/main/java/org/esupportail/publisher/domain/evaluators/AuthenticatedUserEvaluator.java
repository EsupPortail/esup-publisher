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
/**
 *
 */
package org.esupportail.publisher.domain.evaluators;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.service.evaluators.IEvaluationVisitor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author GIP RECIA - Julien Gribonvald 17 nov. 2014
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class", visible = true)
@JsonTypeName("AUTHENTICATED")
@Entity
@DiscriminatorValue("Authenticated")
public class AuthenticatedUserEvaluator extends AbstractEvaluator {

	/** */
	private static final long serialVersionUID = 2558275213355338280L;

	/**
     *
     */
	public AuthenticatedUserEvaluator() {
		super();
	}

	public boolean isApplicable(IEvaluationVisitor visitor) {
		return visitor.isApplicable(this);
	}

}
