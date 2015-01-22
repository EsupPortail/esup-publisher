package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.esupportail.publisher.web.rest.dto.evaluators.EvaluatorDTO;

public interface AEvaluatorDTOFactory<DTObject extends EvaluatorDTO, M extends AbstractEvaluator>
extends DTOFactory<DTObject, M, Long> {

    boolean isDTOFactoryImpl(final EvaluatorDTO dtoObject);
    boolean isDTOFactoryImpl(final AbstractEvaluator model);

}
