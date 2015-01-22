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
import org.esupportail.publisher.domain.enums.OperatorType;
import org.esupportail.publisher.service.evaluators.IEvaluationVisitor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald 14 oct. 2013
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class", visible = true)
@JsonTypeName("OPERATOR")
@Entity
@DiscriminatorValue("Operator")
public class OperatorEvaluator extends AbstractEvaluator {

	/** */
	private static final long serialVersionUID = -1946009726490030327L;

	/** Operator type on evaluators list. */
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(length = 3)
	private OperatorType type;

	/** List of evaluators to evaluate, must contains more than one evaluator. */
	@NotEmpty
	@Size(min = 1)
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	// @JsonIgnore
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@JoinColumn(name = "parent_id")
	private Set<AbstractEvaluator> evaluators = new HashSet<AbstractEvaluator>();

	/**
	 * Constructor
	 */
	public OperatorEvaluator() {
		super();
	}

	/**
	 * @param type
	 * @param abstractEvaluators
	 */
	public OperatorEvaluator(@NotNull final OperatorType type,
			@NotEmpty final Set<AbstractEvaluator> abstractEvaluators) {
		super();
		this.type = type;
		this.evaluators = abstractEvaluators;
	}

	@Override
	public boolean isApplicable(IEvaluationVisitor visitor) {
		return visitor.isApplicable(this);
	}

}
