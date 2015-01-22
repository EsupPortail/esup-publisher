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
package org.esupportail.publisher.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald 26 juin 2014
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonTypeName("PERMONCTXWSUBJS")
@Entity
@DiscriminatorValue("OnContextWithSubjectListFilter")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PermissionOnClassificationWithSubjectList extends
		PermissionOnContext {

	/** */
	private static final long serialVersionUID = 6155776530760516244L;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "T_PERM_AUTHORIZED_SUBJECTS")
	@AttributeOverrides({ @AttributeOverride(name = "PermissionOnClassificationWithSubjectList_id", column = @Column(name = "perm_id")) })
	private Set<SubjectKey> authorizedSubjects = new HashSet<SubjectKey>();

	/**
	 * @param context
	 * @param role
	 * @param abstractEvaluator
	 * @param authorizedSubjects
	 */
	public PermissionOnClassificationWithSubjectList(final ContextKey context,
			final PermissionType role, final AbstractEvaluator abstractEvaluator,
			final Set<SubjectKey> authorizedSubjects) {
		super(context, role, abstractEvaluator);
		this.authorizedSubjects = authorizedSubjects;
	}

}
