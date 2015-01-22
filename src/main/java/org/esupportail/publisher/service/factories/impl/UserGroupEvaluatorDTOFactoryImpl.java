package org.esupportail.publisher.service.factories.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.esupportail.publisher.domain.evaluators.UserGroupEvaluator;
import org.esupportail.publisher.repository.UserGroupEvaluatorRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.UserGroupEvaluatorDTOFactory;
import org.esupportail.publisher.web.rest.dto.evaluators.EvaluatorDTO;
import org.esupportail.publisher.web.rest.dto.evaluators.UserGroupEvaluatorDTO;
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
public class UserGroupEvaluatorDTOFactoryImpl extends EvaluatorDTOFactoryImpl<UserGroupEvaluatorDTO, UserGroupEvaluator>
implements UserGroupEvaluatorDTOFactory {

    @Inject
    @Getter
    private transient UserGroupEvaluatorRepository dao;

    public UserGroupEvaluatorDTOFactoryImpl() {
        super(UserGroupEvaluatorDTO.class, UserGroupEvaluator.class);
    }

    @Override
    public UserGroupEvaluatorDTO from(UserGroupEvaluator model) {
        log.debug("Model to DTO of {}", model);
        if (model != null) {
            return new UserGroupEvaluatorDTO(model.getId(), model.getGroup());
        }
        return null;
    }

    @Override
    public UserGroupEvaluator from(UserGroupEvaluatorDTO dtObject) throws ObjectNotFoundException {
        UserGroupEvaluator model = super.from(dtObject);
        model.setGroup(dtObject.getGroup());
        return model;
    }

    public boolean isDTOFactoryImpl(EvaluatorDTO dtoObject) {
        if (dtoObject instanceof UserGroupEvaluatorDTO) {
            log.debug("ClassificationDTOFactoryImpl selected : {}", this.getClass().getCanonicalName());
            return true;
       }
       return false;
    }

    public boolean isDTOFactoryImpl(AbstractEvaluator model) {
        if (model instanceof UserGroupEvaluator) {
            log.debug("ClassificationDTOFactoryImpl selected : {}", this.getClass().getCanonicalName());
            return true;
        }
        return false;
    }

}
