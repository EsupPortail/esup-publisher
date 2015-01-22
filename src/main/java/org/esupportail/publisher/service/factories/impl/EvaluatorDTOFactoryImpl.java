package org.esupportail.publisher.service.factories.impl;

import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.esupportail.publisher.service.factories.AEvaluatorDTOFactory;
import org.esupportail.publisher.web.rest.dto.evaluators.EvaluatorDTO;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 22 oct. 2014
 * @param <DTObject>
 * @param <M>
 */
public abstract class EvaluatorDTOFactoryImpl<DTObject extends EvaluatorDTO, M extends AbstractEvaluator> extends
    AbstractDTOFactoryImpl<DTObject, M, Long>
    implements AEvaluatorDTOFactory<DTObject, M> {

    public EvaluatorDTOFactoryImpl(final Class<DTObject> dtObjectClass, final Class<M> mClass) {
        super(dtObjectClass, mClass);
    }

}
