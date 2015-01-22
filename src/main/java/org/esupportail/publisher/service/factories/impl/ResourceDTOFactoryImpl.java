package org.esupportail.publisher.service.factories.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Resource;
import org.esupportail.publisher.repository.ResourceRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.OrganizationDTOFactory;
import org.esupportail.publisher.service.factories.RedactorDTOFactory;
import org.esupportail.publisher.service.factories.ResourceDTOFactory;
import org.esupportail.publisher.web.rest.dto.ItemDTO;
import org.esupportail.publisher.web.rest.dto.ResourceDTO;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ResourceDTOFactoryImpl extends ItemDTOFactoryImpl<ResourceDTO, Resource> implements ResourceDTOFactory {

    @Inject
    @Getter
    private transient ResourceRepository dao;

    @Inject
    private transient OrganizationDTOFactory orgFactory;
    @Inject
    private transient RedactorDTOFactory redactorFactory;

    public ResourceDTOFactoryImpl() {
        super(ResourceDTO.class, Resource.class);
    }

    @Override
    public ResourceDTO from(final Resource model) {
        log.debug("Model to DTO of {}", model);
        if (model != null) {
            SubjectDTO updatedBy = null;
            if (model.getLastModifiedBy() != null ) {
                updatedBy = getSubjectFactory().from(model.getLastModifiedBy());
            }
            return new ResourceDTO(model.getId(), model.getTitle(), model.getEnclosure(), model.getRessourceUrl(), model.getStartDate(),
                    model.getEndDate(), model.getValidatedDate(), getSubjectFactory().from(model.getValidatedBy()),
                    model.getStatus(), model.getSummary(), orgFactory.from(model.getOrganization()), redactorFactory.from(model.getRedactor()),
                    model.getCreatedDate(), model.getLastModifiedDate(),
                    getSubjectFactory().from(model.getCreatedBy()), updatedBy);
        }
        return null;
    }

    @Override
    public Resource from(ResourceDTO dtObject) throws ObjectNotFoundException {
        Resource model = super.from(dtObject);
        model.setTitle(dtObject.getTitle());
        model.setEnclosure(dtObject.getEnclosure());
        model.setRessourceUrl(dtObject.getRessourceUrl());
        model.setStartDate(dtObject.getStartDate());
        model.setEndDate(dtObject.getEndDate());
        model.setValidatedDate(dtObject.getValidatedDate());
        model.setValidatedBy(getSubjectFactory().from(dtObject.getValidatedBy()));
        model.setStatus(dtObject.getStatus());
        model.setSummary(dtObject.getSummary());
        model.setOrganization(orgFactory.from(dtObject.getOrganization().getModelId()));
        model.setRedactor(redactorFactory.from(dtObject.getRedactor().getModelId()));

        return model;
    }

    @Override
    public boolean isDTOFactoryImpl(AbstractItem model) {
        return model instanceof Resource;
    }

    @Override
    public boolean isDTOFactoryImpl(ItemDTO dtoObject) {
        return dtoObject instanceof ResourceDTO;
    }

    @Override
    public String getFactoryName() {
        return this.getClass().getName();
    }
}
