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

import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.service.factories.AItemDTOFactory;
import org.esupportail.publisher.web.rest.dto.ItemDTO;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 22 oct. 2014
 * @param <DTObject>
 * @param <M>
 */
public abstract class ItemDTOFactoryImpl<DTObject extends ItemDTO, M extends AbstractItem> extends
        AuditableDTOFactoryImpl<DTObject, M>
    implements AItemDTOFactory<DTObject, M> {

    public ItemDTOFactoryImpl(final Class<DTObject> dtObjectClass, final Class<M> mClass) {
        super(dtObjectClass, mClass);
    }

}
