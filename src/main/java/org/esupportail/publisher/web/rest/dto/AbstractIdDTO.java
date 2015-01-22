package org.esupportail.publisher.web.rest.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 24 mai 2014
 */
@ToString
@EqualsAndHashCode
public abstract class AbstractIdDTO<ID extends Serializable> implements IAbstractDTO<ID>, Serializable {

    @NonNull
    @NotNull
    private ID modelId;


    public AbstractIdDTO(final ID modelId) {
        super();
        this.modelId = modelId;
    }

    public AbstractIdDTO() {
        super();
    }


    public ID getModelId() {
        return modelId;
    }

    public void setModelId(ID modelId) {
        this.modelId = modelId;
    }

}
