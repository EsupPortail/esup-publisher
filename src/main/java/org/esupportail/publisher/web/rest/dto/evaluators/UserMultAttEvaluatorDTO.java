package org.esupportail.publisher.web.rest.dto.evaluators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.StringEvaluationMode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
public class UserMultAttEvaluatorDTO extends UserAttributesEvaluatorDTO implements Serializable {

    /** */
    private static final long serialVersionUID = -4190141457405946002L;

    /**
     * Constructor to use when creating the object from JPA model.
     * @param modelId
     * @param attribute
     * @param value
     * @param mode
     */
    public UserMultAttEvaluatorDTO(@NotNull final Long modelId, @NotNull final String attribute,
            @NotNull final String value, @NotNull final StringEvaluationMode mode) {
        super(modelId, attribute, value, mode);
    }

    /**
     * Constructor to use when creating a new object.
     * @param attribute
     * @param value
     * @param mode
     */
    public UserMultAttEvaluatorDTO(@NotNull final String attribute, @NotNull final String value,
            @NotNull final StringEvaluationMode mode) {
        super(attribute, value, mode);
    }



}
