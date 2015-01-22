package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.AbstractPermission;
import org.esupportail.publisher.web.rest.dto.PermissionDTO;

public interface APermissionDTOFactory<DTObject extends PermissionDTO, M extends AbstractPermission>
extends DTOFactory<DTObject, M, Long> {

    boolean isDTOFactoryImpl(final PermissionDTO dtoObject);
    boolean isDTOFactoryImpl(final AbstractPermission model);

}
