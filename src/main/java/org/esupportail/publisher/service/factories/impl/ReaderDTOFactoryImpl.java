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
import org.esupportail.publisher.domain.Reader;
import org.esupportail.publisher.repository.ReaderRepository;
import org.esupportail.publisher.service.factories.ReaderDTOFactory;
import org.esupportail.publisher.web.rest.dto.ReaderDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
*
* @author GIP RECIA - Julien Gribonvald
* 13 oct. 2014
*/
@Service
@Transactional(readOnly = true)
@Slf4j
public class ReaderDTOFactoryImpl extends AbstractDTOFactoryImpl<ReaderDTO, Reader, Long> implements ReaderDTOFactory {

    @Inject
    @Getter
    private transient ReaderRepository dao;

    public ReaderDTOFactoryImpl() {
        super(ReaderDTO.class, Reader.class);
    }

    @Override
    public ReaderDTO from(final Reader model) {
        log.debug("Model to DTO of {}", model);
        if (model != null) {
            return new ReaderDTO(model.getId(), model.getName(), model.getDisplayName(), model.getDescription(),
                    model.getAuthorizedTypes(), model.getClassificationDecorations());
        }
        return null;
    }

    /*@Override
    public Reader from(final ReaderDTO dtObject) throws ObjectNotFoundException {
        Reader model = super.from(dtObject);
        model.setName(dtObject.getName());
        model.setDisplayName(dtObject.getDisplayName());
        model.setDescription(dtObject.getDescription());
        model.setAuthorizedTypes(dtObject.getAuthorizedTypes());
        model.setClassificationDecorations(dtObject.getClassificationDecorations());
        return model;
    }*/

//    @Override
//    public List<ReaderDTO> asDTOList(Collection<Reader> models) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public Set<ReaderDTO> asDTOSet(Collection<Reader> models) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public Set<Reader> asSet(Collection<ReaderDTO> tObjects)
//            throws ObjectNotFoundException {
//        // TODO Auto-generated method stub
//        return null;
//    }

}
