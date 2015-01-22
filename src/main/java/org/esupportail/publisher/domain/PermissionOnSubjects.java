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
package org.esupportail.publisher.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.CollectionTable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald 27 juin 2014
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonTypeName("PERMONSUBJ")
@Entity
@DiscriminatorValue("OnContextPerSubjects")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PermissionOnSubjects extends AbstractPermission {

	/** */
	private static final long serialVersionUID = 6068659313472636312L;

	@NotEmpty
	@ElementCollection
	@CollectionTable(name = "T_PERM_ROLES_ON_SUBJECTS")
	private Set<SubjectPermKey> rolesOnSubjects = new HashSet<SubjectPermKey>();

	/**
	 * Constructor
	 */
	public PermissionOnSubjects() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param context
	 * @param abstractEvaluator
	 * @param rolesOnSubjects
	 */
	public PermissionOnSubjects(@NotNull final ContextKey context,
			@NotNull final AbstractEvaluator abstractEvaluator,
			final Set<SubjectPermKey> rolesOnSubjects) {
		super(context, abstractEvaluator);
		this.rolesOnSubjects = rolesOnSubjects;
	}

}
