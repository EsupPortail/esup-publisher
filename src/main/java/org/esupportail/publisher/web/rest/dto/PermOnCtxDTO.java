package org.esupportail.publisher.web.rest.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.web.rest.dto.evaluators.EvaluatorDTO;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ToString(callSuper=true)
public class PermOnCtxDTO extends PermissionDTO implements Serializable {

    /** */
    private static final long serialVersionUID = -4415576815509825654L;

    /** The role. */
    @NotNull
    @Setter
    @Getter
    private PermissionType role;

    /**
     * Constructor to use when creating the object from JPA model.
     * @param modelId
     * @param creationDate
     * @param lastUpdateDate
     * @param createdBy
     * @param lastUpdateBy
     * @param context
     * @param evaluator
     * @param role
     */
    public PermOnCtxDTO(@NotNull final Long modelId, final DateTime creationDate, final DateTime lastUpdateDate,
            @NotNull final SubjectDTO createdBy, final SubjectDTO lastUpdateBy,
            @NotNull final ContextKeyDTO context, @NotNull final EvaluatorDTO evaluator, @NotNull final PermissionType role) {
        super(modelId, creationDate, lastUpdateDate, createdBy, lastUpdateBy, context, evaluator);
        this.role = role;
    }

    /**
     * Constructor to use when creating a new object.
     * @param createdBy
     * @param context
     */
    public PermOnCtxDTO(@NotNull final SubjectDTO createdBy, @NotNull final ContextKeyDTO context) {
        super(createdBy, context);
    }
}
