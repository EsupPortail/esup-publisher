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
import org.esupportail.publisher.domain.util.CstPropertiesLength;
import org.esupportail.publisher.service.evaluators.IEvaluationVisitor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author GIP RECIA - Julien Gribonvald 14 oct. 2013
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class", visible = true)
@JsonTypeName("USERGROUP")
@Entity
@DiscriminatorValue("UserGroup")
public class UserGroupEvaluator extends AbstractEvaluator {

	/** */
	private static final long serialVersionUID = 3824929164575472064L;

	/** The portlet group value. */
	@NotNull
	@Size(min = 5, max = CstPropertiesLength.SUBJECTID)
	@Column(length = CstPropertiesLength.SUBJECTID, name = "group_name")
	private String group;

	/**
	 * Contructor of the object UserRoleEvaluator.java.
	 */
	public UserGroupEvaluator() {
		super();
	}

	/**
	 * Contructor of the object UserRoleEvaluator.java.
	 *
	 * @param role
	 *            The group name to evaluate.
	 */
	public UserGroupEvaluator(@NotNull final String role) {
		super();
		this.group = role;
	}

	public boolean isApplicable(IEvaluationVisitor visitor) {
		return visitor.isApplicable(this);
	}
}
