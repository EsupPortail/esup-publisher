package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.web.rest.dto.ItemDTO;


public interface AItemDTOFactory <DTObject extends ItemDTO, M extends AbstractItem>
extends GenericDTOFactory<DTObject, M, Long, Long>{

    boolean isDTOFactoryImpl(final ItemDTO dtoObject);
    boolean isDTOFactoryImpl(final AbstractItem model);

    String getFactoryName();

}
