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

import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Attachment;
import org.esupportail.publisher.repository.AttachmentRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.AttachmentDTOFactory;
import org.esupportail.publisher.service.factories.OrganizationDTOFactory;
import org.esupportail.publisher.service.factories.RedactorDTOFactory;
import org.esupportail.publisher.web.rest.dto.AttachmentDTO;
import org.esupportail.publisher.web.rest.dto.ItemDTO;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class AttachmentDTOFactoryImpl extends ItemDTOFactoryImpl<AttachmentDTO, Attachment> implements AttachmentDTOFactory {

    @Inject
    @Getter
    private transient AttachmentRepository dao;

    @Inject
    private transient OrganizationDTOFactory orgFactory;
    @Inject
    private transient RedactorDTOFactory redactorFactory;

    public AttachmentDTOFactoryImpl() {
        super(AttachmentDTO.class, Attachment.class);
    }

    @Override
    public AttachmentDTO from(final Attachment model) {
        log.debug("Model to DTO of {}", model);
        if (model != null) {
            SubjectDTO updatedBy = null;
            if (model.getLastModifiedBy() != null ) {
                updatedBy = getSubjectFactory().from(model.getLastModifiedBy());
            }
            return new AttachmentDTO(model.getId(), model.getTitle(), model.getEnclosure(), model.getStartDate(),
                model.getEndDate(), model.getValidatedDate(), getSubjectFactory().from(model.getValidatedBy()),
                model.getStatus(), model.getSummary(), model.isRssAllowed(), model.isHighlight(),
                orgFactory.from(model.getOrganization()), redactorFactory.from(model.getRedactor()),
                model.getCreatedDate(), model.getLastModifiedDate(),
                getSubjectFactory().from(model.getCreatedBy()), updatedBy);
        }
        return null;
    }

    @Override
    public Attachment from(AttachmentDTO dtObject) throws ObjectNotFoundException {
        Attachment model = super.from(dtObject);
        model.setTitle(dtObject.getTitle());
        model.setEnclosure(dtObject.getEnclosure());
        model.setStartDate(dtObject.getStartDate());
        model.setEndDate(dtObject.getEndDate());
        model.setValidatedDate(dtObject.getValidatedDate());
        model.setValidatedBy(getSubjectFactory().from(dtObject.getValidatedBy()));
        model.setStatus(dtObject.getStatus());
        model.setSummary(dtObject.getSummary());
        model.setRssAllowed(dtObject.isRssAllowed());
        model.setHighlight(dtObject.isHighlight());
        model.setOrganization(orgFactory.from(dtObject.getOrganization().getModelId()));
        model.setRedactor(redactorFactory.from(dtObject.getRedactor().getModelId()));

        return model;
    }

    @Override
    public boolean isDTOFactoryImpl(AbstractItem model) {
        return model instanceof Attachment;
    }

    @Override
    public boolean isDTOFactoryImpl(ItemDTO dtoObject) {
        return dtoObject instanceof AttachmentDTO;
    }

    @Override
    public String getFactoryName() {
        return this.getClass().getName();
    }

}
