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
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.AItemDTOFactory;
import org.esupportail.publisher.service.factories.ItemDTOSelectorFactory;
import org.esupportail.publisher.web.rest.dto.ItemDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 21 oct. 2014
 */
@Service
@Transactional(readOnly=true)
@Slf4j
public class ItemDTOSelectorFactoryImpl extends AbstractDTOFactoryImpl<ItemDTO, AbstractItem, Long> implements ItemDTOSelectorFactory {

    @Inject
    @Getter
    private transient ItemRepository<AbstractItem> dao;

    @Inject
    private List<AItemDTOFactory<? extends ItemDTO, ? extends AbstractItem>> AbstractItemFactories;

    public ItemDTOSelectorFactoryImpl() {
        super(ItemDTO.class, AbstractItem.class);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public AbstractItem from(ItemDTO dtObject) throws ObjectNotFoundException {
        return (AbstractItem) ((AItemDTOFactory) getFactory(dtObject)).from(dtObject);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ItemDTO from(AbstractItem model) {
        return (ItemDTO) ((AItemDTOFactory) getFactory(model)).from(model);
    }


    private AItemDTOFactory<? extends ItemDTO, ? extends AbstractItem> getFactory(final ItemDTO dtoObject) {
        for (AItemDTOFactory<? extends ItemDTO, ? extends AbstractItem> factory : AbstractItemFactories) {
            if (factory.isDTOFactoryImpl(dtoObject)) {
                log.debug("Factory selected {} for object type {}", factory.getFactoryName(), dtoObject.getClass().getCanonicalName());
                return factory;
            }
        }
        throw new IllegalArgumentException("No DTOFactoryImpl found for " + dtoObject.getClass().getCanonicalName());
    }

    private AItemDTOFactory<? extends ItemDTO, ? extends AbstractItem> getFactory(final AbstractItem model) {
        for (AItemDTOFactory<? extends ItemDTO, ? extends AbstractItem> factory : AbstractItemFactories) {
            if (factory.isDTOFactoryImpl(model)) {
                log.debug("Factory selected {} for object type {}", factory.getFactoryName(), model.getClass().getCanonicalName());
                return factory;
            }
        }
        throw new IllegalArgumentException("No DTOFactoryImpl found for " + model.getClass().getCanonicalName());
    }

}
