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
import org.esupportail.publisher.domain.evaluators.OperatorEvaluator;
import org.esupportail.publisher.repository.OperatorEvaluatorRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.EvaluatorDTOSelectorFactory;
import org.esupportail.publisher.service.factories.OperatorEvaluatorDTOFactory;
import org.esupportail.publisher.web.rest.dto.evaluators.EvaluatorDTO;
import org.esupportail.publisher.web.rest.dto.evaluators.OperatorEvaluatorDTO;
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
public class OperatorEvaluatorDTOFactoryImpl extends EvaluatorDTOFactoryImpl<OperatorEvaluatorDTO, OperatorEvaluator>
implements OperatorEvaluatorDTOFactory {

    @Inject
    @Getter
    private transient OperatorEvaluatorRepository dao;

    @Inject
    private transient EvaluatorDTOSelectorFactory evaluatorFactory;

    public OperatorEvaluatorDTOFactoryImpl() {
        super(OperatorEvaluatorDTO.class, OperatorEvaluator.class);
    }

    @Override
    public OperatorEvaluatorDTO from(OperatorEvaluator model) {
        log.debug("Model to DTO of {}", model);
        if (model != null) {
            return new OperatorEvaluatorDTO(model.getId(), model.getType(),
                    evaluatorFactory.asDTOSet(model.getEvaluators()));
        }
        return null;
    }

    @Override
    public OperatorEvaluator from(OperatorEvaluatorDTO dtObject) throws ObjectNotFoundException {
        OperatorEvaluator model = super.from(dtObject);
        model.setType(dtObject.getType());
        model.setEvaluators(evaluatorFactory.asSet(dtObject.getEvaluators()));
        return model;
    }

    public boolean isDTOFactoryImpl(EvaluatorDTO dtoObject) {
        if (dtoObject instanceof OperatorEvaluatorDTO) {
            log.debug("ClassificationDTOFactoryImpl selected : {}", this.getClass().getCanonicalName());
            return true;
       }
       return false;
    }

    public boolean isDTOFactoryImpl(AbstractEvaluator model) {
        if (model instanceof OperatorEvaluator) {
            log.debug("ClassificationDTOFactoryImpl selected : {}", this.getClass().getCanonicalName());
            return true;
        }
        return false;
    }

}
