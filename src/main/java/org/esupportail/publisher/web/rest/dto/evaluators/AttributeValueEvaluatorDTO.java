package org.esupportail.publisher.web.rest.dto.evaluators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=true)
public abstract class AttributeValueEvaluatorDTO extends EvaluatorDTO implements Serializable {

    /** */
    private static final long serialVersionUID = 392222345237690021L;

    @NotNull
    @Size(min = 2, max=125)
    private String attribute;

    @NotNull
    @Size(min = 1, max=512)
    private String value;

    /**
     * Constructor to use when creating the object from JPA model.
     * @param modelId
     * @param attribute
     * @param value
     */
    public AttributeValueEvaluatorDTO(@NotNull final Long modelId, @NotNull final String attribute,
            @NotNull final String value) {
        super(modelId);
        this.attribute = attribute;
        this.value = value;
    }

    /**
     * Constructor to use when creating a new object.
     * @param attribute
     * @param value
     */
    public AttributeValueEvaluatorDTO(@NotNull final String attribute, @NotNull final String value) {
        super();
        this.attribute = attribute;
        this.value = value;
    }

}
