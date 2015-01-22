package org.esupportail.publisher.service.factories.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.esupportail.publisher.domain.evaluators.UserMultivaluedAttributesEvaluator;
import org.esupportail.publisher.repository.UserMultivaluedAttributesEvaluatorRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.UserMultivaluedAttrEvaluatorDTOFactory;
import org.esupportail.publisher.web.rest.dto.evaluators.EvaluatorDTO;
import org.esupportail.publisher.web.rest.dto.evaluators.UserMultAttEvaluatorDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 23 oct. 2014
 */
@Service
@Transactional(readOnly=true)
@Slf4j
public class UserMultivaluedAttributesEvaluatorDTOFactoryImpl extends EvaluatorDTOFactoryImpl<UserMultAttEvaluatorDTO, UserMultivaluedAttributesEvaluator>
implements UserMultivaluedAttrEvaluatorDTOFactory {

    @Inject
    @Getter
    private transient UserMultivaluedAttributesEvaluatorRepository dao;

    public UserMultivaluedAttributesEvaluatorDTOFactoryImpl() {
        super(UserMultAttEvaluatorDTO.class, UserMultivaluedAttributesEvaluator.class);
    }

    @Override
    public UserMultAttEvaluatorDTO from(UserMultivaluedAttributesEvaluator model) {
        log.debug("Model to DTO of {}", model);
        if (model != null) {
            return new UserMultAttEvaluatorDTO(model.getId(), model.getAttribute(), model.getValue(), model.getMode());
        }
        return null;
    }

    @Override
    public UserMultivaluedAttributesEvaluator from(UserMultAttEvaluatorDTO dtObject) throws ObjectNotFoundException {
        UserMultivaluedAttributesEvaluator model = super.from(dtObject);
        model.setAttribute(dtObject.getAttribute());
        model.setValue(dtObject.getValue());
        model.setMode(dtObject.getMode());
        return model;
    }

    public boolean isDTOFactoryImpl(EvaluatorDTO dtoObject) {
        if (dtoObject instanceof UserMultAttEvaluatorDTO) {
            log.debug("ClassificationDTOFactoryImpl selected : {}", this.getClass().getCanonicalName());
            return true;
       }
       return false;
    }

    public boolean isDTOFactoryImpl(AbstractEvaluator model) {
        if (model instanceof UserMultivaluedAttributesEvaluator) {
            log.debug("ClassificationDTOFactoryImpl selected : {}", this.getClass().getCanonicalName());
            return true;
        }
        return false;
    }

}
