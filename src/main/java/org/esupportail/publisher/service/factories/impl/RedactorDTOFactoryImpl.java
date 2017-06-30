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

import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.repository.RedactorRepository;
import org.esupportail.publisher.service.factories.RedactorDTOFactory;
import org.esupportail.publisher.web.rest.dto.RedactorDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
*
* @author GIP RECIA - Julien Gribonvald
* 13 oct. 2014
*/
@Service
@Transactional(readOnly = true)
@Slf4j
public class RedactorDTOFactoryImpl extends AbstractDTOFactoryImpl<RedactorDTO, Redactor, Long> implements RedactorDTOFactory {

    @Inject
    @Getter
    private transient RedactorRepository dao;

    public RedactorDTOFactoryImpl() {
        super(RedactorDTO.class, Redactor.class);
    }

    @Override
    public RedactorDTO from(final Redactor model) {
        log.debug("Model to DTO of {}", model);
        if (model != null) {
            return new RedactorDTO(model.getId(), model.getName(), model.getDisplayName(), model.getDescription(),
                    model.getNbLevelsOfClassification(), model.isOptionalPublishTime(), model.getNbDaysMaxDuration());
        }
        return null;
    }

    /*@Override
    public Redactor from(final RedactorDTO dtObject) throws ObjectNotFoundException {
        Redactor model = super.from(dtObject);
        model.setName(dtObject.getName());
        model.setDisplayName(dtObject.getDisplayName());
        model.setDescription(dtObject.getDescription());
        model.setNbLevelsOfClassification(dtObject.getNbLevelsOfClassification());
        return model;
    }*/

//    @Override
//    public List<RedactorDTO> asDTOList(Collection<Redactor> models) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public Set<RedactorDTO> asDTOSet(Collection<Redactor> models) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public Set<Redactor> asSet(Collection<RedactorDTO> tObjects)
//            throws ObjectNotFoundException {
//        // TODO Auto-generated method stub
//        return null;
//    }

}
