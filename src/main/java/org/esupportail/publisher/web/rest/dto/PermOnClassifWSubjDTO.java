package org.esupportail.publisher.web.rest.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.web.rest.dto.evaluators.EvaluatorDTO;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(callSuper=true)
public class PermOnClassifWSubjDTO extends PermOnCtxDTO implements Serializable {

    /** */
    private static final long serialVersionUID = -2085364841894444451L;

    private Set<SubjectKeyDTO> authorizedSubjects = new HashSet<SubjectKeyDTO>();

    /**
     * @param modelId
     * @param creationDate
     * @param lastUpdateDate
     * @param createdBy
     * @param lastUpdateBy
     * @param context
     * @param evaluator
     * @param role
     * @param authorizedSubjects
     */
    public PermOnClassifWSubjDTO(@NotNull final Long modelId, final DateTime creationDate,
            DateTime lastUpdateDate, @NotNull final SubjectDTO createdBy, final SubjectDTO lastUpdateBy,
            @NotNull final ContextKeyDTO context, @NotNull final EvaluatorDTO evaluator, @NotNull final PermissionType role,
            final Set<SubjectKeyDTO> authorizedSubjects) {
        super(modelId, creationDate, lastUpdateDate, createdBy, lastUpdateBy,
                context, evaluator, role);
        this.authorizedSubjects = authorizedSubjects;
    }

    /**
     * @param createdBy
     * @param context
     * @param authorizedSubjects
     */
    public PermOnClassifWSubjDTO(@NotNull final SubjectDTO createdBy, @NotNull final ContextKeyDTO context,
            final Set<SubjectKeyDTO> authorizedSubjects) {
        super(createdBy, context);
        this.authorizedSubjects = authorizedSubjects;
    }


}
