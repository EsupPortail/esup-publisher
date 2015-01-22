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
public class UserAttributesEvaluatorDTO extends AttributeValueEvaluatorDTO implements Serializable {

    /** */
    private static final long serialVersionUID = 5841984710712992947L;

    @NotNull
    private StringEvaluationMode mode;

    /**
     * Constructor to use when creating the object from JPA model.
     * @param modelId
     * @param attribute
     * @param value
     * @param mode
     */
    public UserAttributesEvaluatorDTO(@NotNull final Long modelId, @NotNull final String attribute,
            @NotNull final String value, @NotNull final StringEvaluationMode mode) {
        super(modelId, attribute, value);
        this.mode = mode;
    }

    /**
     * Constructor to use when creating a new object.
     * @param attribute
     * @param value
     * @param mode
     */
    public UserAttributesEvaluatorDTO(@NotNull final String attribute, @NotNull final String value,
            @NotNull final StringEvaluationMode mode) {
        super(attribute, value);
        this.mode = mode;
    }



}
