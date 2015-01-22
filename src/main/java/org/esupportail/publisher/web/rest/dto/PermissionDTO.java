/**
 *
 */
package org.esupportail.publisher.web.rest.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.web.rest.dto.evaluators.EvaluatorDTO;
import org.joda.time.DateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 21 juil. 2014
 */
@Getter
@ToString(callSuper=true)
public abstract class PermissionDTO extends AuditableDTO implements IAbstractDTO<Long> {
    /** */
    private static final long serialVersionUID = -1951934656598078964L;

    @NotNull
    private ContextKeyDTO context;
    /** The evaluator to address permissions */
    @NotNull
    @Setter
    @Valid
    private EvaluatorDTO evaluator;

    public PermissionDTO(@NotNull final Long modelId, final DateTime creationDate, final DateTime lastUpdateDate,
                                      @NotNull final SubjectDTO createdBy, final SubjectDTO lastUpdateBy,
                                      @NotNull final ContextKeyDTO context, @NotNull final EvaluatorDTO evaluator) {
        super(modelId, creationDate, lastUpdateDate, createdBy, lastUpdateBy);
        this.context = context;
        this.evaluator = evaluator;
    }

    public PermissionDTO(@NotNull final SubjectDTO createdBy, @NotNull final ContextKeyDTO context) {
        super(createdBy);
        this.context = context;
    }

    public String toStringLite() {
        return "PermissionDTO{" +
            "context=" + context +
            ", evaluator=" + evaluator +
            '}';
    }
}
