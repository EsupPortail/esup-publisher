/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
