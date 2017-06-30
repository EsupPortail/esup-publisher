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
import org.esupportail.publisher.domain.AbstractAuditingEntity;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.SubjectDTOFactory;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.AuditableDTO;

import javax.inject.Inject;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 25 juil. 2014
 * @param <DTObject> DTO Object
 * @param <M> Model Object (JPA Entity)
 */
public abstract class AuditableDTOFactoryImpl<DTObject extends AuditableDTO, M extends AbstractAuditingEntity>
    extends AbstractDTOFactoryImpl<DTObject, M, Long> {

    @Inject
    @Getter
    private transient UserRepository userDao;

    @Inject
    @Getter
    private transient SubjectDTOFactory subjectFactory;

    /**
     * Constructor that initializes specific class instances for use by the
     * common base class methods.
     * @param dtObjectClass Data Transfer Object class for the associated model class
     * @param mClass The model class
     */
    public AuditableDTOFactoryImpl(final Class<DTObject> dtObjectClass,
            final Class<M> mClass) {
        super(dtObjectClass, mClass);
    }

    @Override
    public M from(final DTObject dtObject) throws ObjectNotFoundException {
        M model = super.from(dtObject);
        return model;
    }
}
