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
