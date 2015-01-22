package org.esupportail.publisher.service.factories.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.AbstractPermission;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.PermissionOnSubjectsWithClassificationList;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.repository.PermOnSubjectsWithClassifsRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.CompositeKeyDTOFactory;
import org.esupportail.publisher.service.factories.EvaluatorDTOSelectorFactory;
import org.esupportail.publisher.service.factories.PermOnSubjWClassifDTOFactory;
import org.esupportail.publisher.service.factories.SubjectPermKeyDTOFactory;
import org.esupportail.publisher.web.rest.dto.ContextKeyDTO;
import org.esupportail.publisher.web.rest.dto.PermOnSubjWClassifDTO;
import org.esupportail.publisher.web.rest.dto.PermissionDTO;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;
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
public class PermOnSubjWClassifDTOFactoryImpl extends PermissionDTOFactoryImpl<PermOnSubjWClassifDTO, PermissionOnSubjectsWithClassificationList>
implements PermOnSubjWClassifDTOFactory {

    @Inject
    @Getter
    private transient PermOnSubjectsWithClassifsRepository dao;

    @Inject
    private transient EvaluatorDTOSelectorFactory evaluatorFactory;

    @Inject
    @Named("contextKeyDTOFactoryImpl")
    private transient CompositeKeyDTOFactory<ContextKeyDTO, ContextKey, Long, ContextType> contextConverter;

    @Inject
    private transient SubjectPermKeyDTOFactory subjectPermKeyConverter;

    public PermOnSubjWClassifDTOFactoryImpl() {
        super(PermOnSubjWClassifDTO.class, PermissionOnSubjectsWithClassificationList.class);
    }

    @Override
    public PermOnSubjWClassifDTO from(PermissionOnSubjectsWithClassificationList model) {
        log.debug("Model to DTO of {}", model);
        if (model != null) {
            SubjectDTO updatedBy = null;
            if (model.getLastModifiedBy() != null ) {
                updatedBy = getSubjectFactory().from(model.getLastModifiedBy());
            }
            return new PermOnSubjWClassifDTO(model.getId(),model.getCreatedDate(), model.getLastModifiedDate(),
                    getSubjectFactory().from(model.getCreatedBy()), updatedBy ,
                    contextConverter.convertToDTOKey(model.getContext()), evaluatorFactory.from(model.getEvaluator()),
                    subjectPermKeyConverter.convertToDTOKey(model.getRolesOnSubjects()),
                    contextConverter.convertToDTOKey(model.getAuthorizedContexts()));
        }
        return null;
    }

    @Override
    public PermissionOnSubjectsWithClassificationList from(PermOnSubjWClassifDTO dtObject) throws ObjectNotFoundException {
        PermissionOnSubjectsWithClassificationList model = super.from(dtObject);
        model.setContext(contextConverter.convertToModelKey(dtObject.getContext()));
        model.setEvaluator(evaluatorFactory.from(dtObject.getEvaluator()));
        model.setRolesOnSubjects(subjectPermKeyConverter.convertToModelKey(dtObject.getRolesOnSubjects()));
        model.setAuthorizedContexts(contextConverter.convertToModelKey(dtObject.getAuthorizedContexts()));
        return model;
    }

    public boolean isDTOFactoryImpl(PermissionDTO dtoObject) {
        if (dtoObject instanceof PermOnSubjWClassifDTO) {
            log.debug("PermOnSubjWClassifDTOFactoryImpl selected : {}", this.getClass().getCanonicalName());
            return true;
       }
       return false;
    }

    public boolean isDTOFactoryImpl(AbstractPermission model) {
        if (model instanceof PermissionOnSubjectsWithClassificationList) {
            log.debug("PermOnSubjWClassifDTOFactoryImpl selected : {}", this.getClass().getCanonicalName());
            return true;
        }
        return false;
    }

}
