package org.esupportail.publisher.service.factories.impl;

import lombok.Getter;
import org.apache.commons.lang.IllegalClassException;
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
        throw new IllegalClassException("No DTOFactoryImpl found for " + dtoObject.getClass().getCanonicalName());
    }

    private APermissionDTOFactory<? extends PermissionDTO, ? extends AbstractPermission> getFactory(final AbstractPermission model) {
        for (APermissionDTOFactory<? extends PermissionDTO, ? extends AbstractPermission> factory : permissionFactories) {
            if (factory.isDTOFactoryImpl(model)) return factory;
        }
        throw new IllegalClassException("No DTOFactoryImpl found for " + model.getClass().getCanonicalName());
    }


}
