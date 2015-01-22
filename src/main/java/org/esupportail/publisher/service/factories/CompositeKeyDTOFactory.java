package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.web.rest.dto.ICompositeKey;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Data Transfer Object factory for converting to and from models.
 * @author GIP RECIA - Julien Gribonvald
 * @param <DTOKEY> DTO type for the corresponding model
 * @param <MODELKEY> Model type
 * @param <KEY> Type of id of the composite key
 * @param <TYPE> Enum Type of the composite key
 */
public interface CompositeKeyDTOFactory<DTOKEY extends ICompositeKey<KEY, TYPE>, MODELKEY  extends ICompositeKey<KEY, TYPE>, KEY extends Serializable, TYPE extends Serializable> {


    /**
     * Load model from storage based on the specified identifier.
     *
     * @param id
     *            Load model for this identifier
     *
     * @return Model loaded from persistent storage
     * //@throws org.esupportail.publisher.service.exceptions.ObjectNotFoundException
     */
    MODELKEY convertToModelKey(@NotNull final DTOKEY id);

    Set<MODELKEY> convertToModelKey(@NotNull final Set<DTOKEY> id);

    List<MODELKEY> convertToModelKey(@NotNull final List<DTOKEY> id);

    /**
     * Load model from storage based on the specified identifier.
     *
     * @param set
     *            Load model for this identifier
     *
     * @return Model loaded from persistent storage
     * //@throws org.esupportail.publisher.service.exceptions.ObjectNotFoundException
     */
    DTOKEY convertToDTOKey(@NotNull final MODELKEY set);

    Set<DTOKEY> convertToDTOKey(@NotNull final Set<MODELKEY> id);

    List<DTOKEY> convertToDTOKey(@NotNull final List<MODELKEY> id);


}
