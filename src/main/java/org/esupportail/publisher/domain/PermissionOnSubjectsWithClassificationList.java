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

import javax.persistence.CollectionTable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.HashSet;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald 27 juin 2014
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonTypeName("PERMONSUBJWCLASSIFS")
@Entity
@DiscriminatorValue("OnContextPerSubjectsWithSubContextListFilter")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PermissionOnSubjectsWithClassificationList extends
		PermissionOnSubjects {

	/** */
	private static final long serialVersionUID = -1311490394950589993L;

	@ElementCollection
	@CollectionTable(name = "T_PERM_AUTHORIZED_CONTEXTS"
	/*
	 * , joinColumns= {
	 *
	 * @JoinColumn(name= "pac_ctx_id", referencedColumnName ="ctx_id"),
	 *
	 * @JoinColumn(name= "pac_ctx_type", referencedColumnName ="ctx_type"),
	 *
	 * @JoinColumn(name= "pac_ctx_role", referencedColumnName ="role")}
	 */)
	private Set<ContextKey> authorizedContexts = new HashSet<ContextKey>();

	/**
	 * Constructor
	 */
	public PermissionOnSubjectsWithClassificationList() {
		super();
	}

	/**
	 * @param context
	 * @param abstractEvaluator
	 * @param rolesOnSubject
	 * @param authorizedContexts
	 */
	public PermissionOnSubjectsWithClassificationList(final ContextKey context,
			final AbstractEvaluator abstractEvaluator,
			final Set<SubjectPermKey> rolesOnSubject,
			final Set<ContextKey> authorizedContexts) {
		super(context, abstractEvaluator, rolesOnSubject);
		this.authorizedContexts = authorizedContexts;
	}

}
