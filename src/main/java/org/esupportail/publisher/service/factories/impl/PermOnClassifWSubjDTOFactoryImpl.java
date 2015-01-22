package org.esupportail.publisher.service.factories.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.AbstractPermission;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.PermissionOnClassificationWithSubjectList;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.repository.PermOnClassifWithSubjectsRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.CompositeKeyDTOFactory;
import org.esupportail.publisher.service.factories.EvaluatorDTOSelectorFactory;
import org.esupportail.publisher.service.factories.PermOnCLassifWSubjDTOFactory;
import org.esupportail.publisher.web.rest.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 23 oct. 2014
 */
@Service
@Transactional(readOnly=true)
@Slf4j
public class PermOnClassifWSubjDTOFactoryImpl extends PermissionDTOFactoryImpl<PermOnClassifWSubjDTO, PermissionOnClassificationWithSubjectList>
implements PermOnCLassifWSubjDTOFactory {

    @Inject
    @Getter
    private transient PermOnClassifWithSubjectsRepository dao;

    @Inject
    private transient EvaluatorDTOSelectorFactory evaluatorFactory;

    @Inject
    @Named("contextKeyDTOFactoryImpl")
    private transient CompositeKeyDTOFactory<ContextKeyDTO, ContextKey, Long, ContextType> contextConverter;

    @Inject
    @Named("subjectKeyDTOFactoryImpl")
    private transient CompositeKeyDTOFactory<SubjectKeyDTO, SubjectKey, String, SubjectType> subjectConverter;

    public PermOnClassifWSubjDTOFactoryImpl() {
        super(PermOnClassifWSubjDTO.class, PermissionOnClassificationWithSubjectList.class);
    }

    @Override
    public PermOnClassifWSubjDTO from(PermissionOnClassificationWithSubjectList model) {
        log.debug("Model to DTO of {}", model);
        if (model != null) {
            SubjectDTO updatedBy = null;
            if (model.getLastModifiedBy() != null ) {
                updatedBy = getSubjectFactory().from(model.getLastModifiedBy());
            }
            return new PermOnClassifWSubjDTO(model.getId(),model.getCreatedDate(), model.getLastModifiedDate(),
                    getSubjectFactory().from(model.getCreatedBy()), updatedBy ,
                    contextConverter.convertToDTOKey(model.getContext()), evaluatorFactory.from(model.getEvaluator()),
                    model.getRole(), subjectConverter.convertToDTOKey(model.getAuthorizedSubjects()));
        }
        return null;
    }

    @Override
    public PermissionOnClassificationWithSubjectList from(PermOnClassifWSubjDTO dtObject) throws ObjectNotFoundException {
        PermissionOnClassificationWithSubjectList model = super.from(dtObject);
        model.setContext(contextConverter.convertToModelKey(dtObject.getContext()));
        model.setRole(dtObject.getRole());
        model.setEvaluator(evaluatorFactory.from(dtObject.getEvaluator()));
        model.setAuthorizedSubjects(subjectConverter.convertToModelKey(dtObject.getAuthorizedSubjects()));
        return model;
    }

    public boolean isDTOFactoryImpl(PermissionDTO dtoObject) {
        if (dtoObject instanceof PermOnClassifWSubjDTO) {
            log.debug("PermOnClassifWSubjDTOFactoryImpl selected : {}", this.getClass().getCanonicalName());
            return true;
       }
       return false;
    }

    public boolean isDTOFactoryImpl(AbstractPermission model) {
        if (model instanceof PermissionOnClassificationWithSubjectList) {
            log.debug("PermOnClassifWSubjDTOFactoryImpl selected : {}", this.getClass().getCanonicalName());
            return true;
        }
        return false;
    }

}
