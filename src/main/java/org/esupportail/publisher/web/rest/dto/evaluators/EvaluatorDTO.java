package org.esupportail.publisher.web.rest.dto.evaluators;

import lombok.ToString;
import org.esupportail.publisher.web.rest.dto.AbstractIdDTO;
import org.esupportail.publisher.web.rest.dto.IAbstractDTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ToString(callSuper=true)
public abstract class EvaluatorDTO extends AbstractIdDTO<Long> implements IAbstractDTO<Long>, Serializable {

    /** */
    private static final long serialVersionUID = -683180383538160744L;

    /**
     *Constructor to use when creating a new object.
     */
    public EvaluatorDTO() {
        super();
    }

    /**
     * Constructor to use when creating the object from JPA model.
     * @param modelId
     */
    public EvaluatorDTO(@NotNull final Long modelId) {
        super(modelId);
    }


}
