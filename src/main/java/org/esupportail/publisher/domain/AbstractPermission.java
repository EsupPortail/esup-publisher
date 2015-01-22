package org.esupportail.publisher.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PermissionOnContext.class, name = "PERMONCTX"),
    @JsonSubTypes.Type(value = PermissionOnClassificationWithSubjectList.class, name = "PERMONCTXWSUBJS"),
    @JsonSubTypes.Type(value = PermissionOnSubjects.class, name = "PERMONSUBJ"),
    @JsonSubTypes.Type(value = PermissionOnSubjectsWithClassificationList.class, name = "PERMONSUBJWCLASSIFS")
})
@Entity
@Table(name = "T_PERMISSION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "permissiontype", discriminatorType = DiscriminatorType.STRING, length = 45)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public abstract class AbstractPermission extends AbstractAuditingEntity
		implements Serializable {

	/** */
	private static final long serialVersionUID = -8988983500350553644L;

	/** The context */
	@NotNull
	@Embedded
	private ContextKey context;

	/** The evaluator to address permissions */
	@NotNull
	@OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	// @OneToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH,
	// CascadeType.REMOVE }, fetch = FetchType.EAGER)
	@JoinColumn(name = "evaluator_id")
	@Valid
	private AbstractEvaluator evaluator;

	public AbstractPermission() {
		super();
	}

	public AbstractPermission(final ContextKey context, final AbstractEvaluator abstractEvaluator) {
		super();
		this.context = context;
		this.evaluator = abstractEvaluator;
	}

}
