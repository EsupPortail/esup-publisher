package org.esupportail.publisher.service.factories.impl;

import org.esupportail.publisher.domain.AbstractPermission;
import org.esupportail.publisher.service.factories.APermissionDTOFactory;
import org.esupportail.publisher.web.rest.dto.PermissionDTO;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 22 oct. 2014
 * @param <DTObject>
 * @param <M>
 */
public abstract class PermissionDTOFactoryImpl<DTObject extends PermissionDTO, M extends AbstractPermission> extends
        AuditableDTOFactoryImpl<DTObject, M>
    implements APermissionDTOFactory<DTObject, M> {

    public PermissionDTOFactoryImpl(final Class<DTObject> dtObjectClass, final Class<M> mClass) {
        super(dtObjectClass, mClass);
    }

}
