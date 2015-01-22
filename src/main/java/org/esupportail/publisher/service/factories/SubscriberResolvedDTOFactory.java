package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.web.rest.dto.SubscriberResolvedDTO;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface SubscriberResolvedDTOFactory {

    /**
     * Copy the model to a new DTO.
     *
     * @param model
     *            Model to translate
     * @return Enhanced Model Object
     */
    SubscriberResolvedDTO from(@NotNull final Subscriber model);

    /**
     * Copy the list of models to a new list of transfer objects.
     *
     * @param models
     *            Models to translate
     * @return Enhanced Model Object
     */
    List<SubscriberResolvedDTO> asDTOList(@NotNull final List<Subscriber> models);
}
