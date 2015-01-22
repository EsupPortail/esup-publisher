package org.esupportail.publisher.service.factories.impl;

import lombok.Getter;
import org.apache.commons.lang.IllegalClassException;
import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.esupportail.publisher.repository.EvaluatorRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.AEvaluatorDTOFactory;
import org.esupportail.publisher.service.factories.EvaluatorDTOSelectorFactory;
import org.esupportail.publisher.web.rest.dto.evaluators.EvaluatorDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 23 oct. 2014
 */
@Service
@Transactional(readOnly=true)
public class EvaluatorDTOSelectorFactoryImpl extends AbstractDTOFactoryImpl<EvaluatorDTO, AbstractEvaluator, Long> implements EvaluatorDTOSelectorFactory {

    @Inject
    @Getter
    private transient EvaluatorRepository<AbstractEvaluator> dao;

    @Inject
    private transient List<AEvaluatorDTOFactory<? extends EvaluatorDTO, ? extends AbstractEvaluator>> evaluatorFactories;

    public EvaluatorDTOSelectorFactoryImpl() {
        super(EvaluatorDTO.class, AbstractEvaluator.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public EvaluatorDTO from(AbstractEvaluator model) {
        return (EvaluatorDTO) ((AEvaluatorDTOFactory) getFactory(model)).from(model);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public AbstractEvaluator from(EvaluatorDTO dtObject) throws ObjectNotFoundException {
        return (AbstractEvaluator) ((AEvaluatorDTOFactory) getFactory(dtObject)).from(dtObject);
    }


    private AEvaluatorDTOFactory<? extends EvaluatorDTO, ? extends AbstractEvaluator> getFactory(final EvaluatorDTO dtoObject) {
        for (AEvaluatorDTOFactory<? extends EvaluatorDTO, ? extends AbstractEvaluator> factory : evaluatorFactories) {
            if (factory.isDTOFactoryImpl(dtoObject)) return factory;
        }
        throw new IllegalClassException("No DTOFactoryImpl found for " + dtoObject.getClass().getCanonicalName());
    }

    private AEvaluatorDTOFactory<? extends EvaluatorDTO, ? extends AbstractEvaluator> getFactory(final AbstractEvaluator model) {
        for (AEvaluatorDTOFactory<? extends EvaluatorDTO, ? extends AbstractEvaluator> factory : evaluatorFactories) {
            if (factory.isDTOFactoryImpl(model)) return factory;
        }
        throw new IllegalClassException("No DTOFactoryImpl found for " + model.getClass().getCanonicalName());
    }


}
