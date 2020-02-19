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
import org.esupportail.publisher.domain.AbstractPermission;
import org.esupportail.publisher.repository.PermissionRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.APermissionDTOFactory;
import org.esupportail.publisher.service.factories.PermissionDTOSelectorFactory;
import org.esupportail.publisher.web.rest.dto.PermissionDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 23 oct. 2014
 */
@Service
@Transactional(readOnly=true)
public class PermissionDTOSelectorFactoryImpl extends AbstractDTOFactoryImpl<PermissionDTO, AbstractPermission, Long> implements PermissionDTOSelectorFactory {

    @Inject
    @Getter
    private transient PermissionRepository<AbstractPermission> dao;

    @Inject
    private transient List<APermissionDTOFactory<? extends PermissionDTO, ? extends AbstractPermission>> permissionFactories;

    public PermissionDTOSelectorFactoryImpl() {
        super(PermissionDTO.class, AbstractPermission.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public PermissionDTO from(@NotNull AbstractPermission model) {
        return (PermissionDTO) ((APermissionDTOFactory) getFactory(model)).from(model);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public AbstractPermission from(@NotNull PermissionDTO dtObject) throws ObjectNotFoundException {
        return (AbstractPermission) ((APermissionDTOFactory) getFactory(dtObject)).from(dtObject);
    }


    private APermissionDTOFactory<? extends PermissionDTO, ? extends AbstractPermission> getFactory(final PermissionDTO dtoObject) {
        for (APermissionDTOFactory<? extends PermissionDTO, ? extends AbstractPermission> factory : permissionFactories) {
            if (factory.isDTOFactoryImpl(dtoObject)) return factory;
        }
        throw new IllegalArgumentException("No DTOFactoryImpl found for " + dtoObject.getClass().getCanonicalName());
    }

    private APermissionDTOFactory<? extends PermissionDTO, ? extends AbstractPermission> getFactory(final AbstractPermission model) {
        for (APermissionDTOFactory<? extends PermissionDTO, ? extends AbstractPermission> factory : permissionFactories) {
            if (factory.isDTOFactoryImpl(model)) return factory;
        }
        throw new IllegalArgumentException("No DTOFactoryImpl found for " + model.getClass().getCanonicalName());
    }


}
