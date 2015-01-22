package org.esupportail.publisher.service.factories.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.SubjectPermKey;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.service.factories.CompositeKeyDTOFactory;
import org.esupportail.publisher.service.factories.SubjectPermKeyDTOFactory;
import org.esupportail.publisher.web.rest.dto.SubjectKeyDTO;
import org.esupportail.publisher.web.rest.dto.SubjectPermKeyDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Service
public class SubjectPermKeyDTOFactoryImpl implements SubjectPermKeyDTOFactory {

    @Inject
    @Named("subjectKeyDTOFactoryImpl")
    private transient CompositeKeyDTOFactory<SubjectKeyDTO, SubjectKey, String, SubjectType> subjectConverter;

    public SubjectPermKey convertToModelKey(@NotNull final SubjectPermKeyDTO dto) {
        return new SubjectPermKey(subjectConverter.convertToModelKey(dto.getSubjectKey()), dto.isNeedValidation());
    }

    public SubjectPermKeyDTO convertToDTOKey(@NotNull final SubjectPermKey model) {
        return new SubjectPermKeyDTO(subjectConverter.convertToDTOKey(model.getSubjectKey()), model.isNeedValidation());
    }

    public Set<SubjectPermKey> convertToModelKey(@NotNull final Set<SubjectPermKeyDTO> dtos) {
        final Set<SubjectPermKey> models = Sets.newHashSet();
        for (SubjectPermKeyDTO dto : dtos) {
            models.add(this.convertToModelKey(dto));
        }
        return models;
    }

    public List<SubjectPermKey> convertToModelKey(@NotNull final List<SubjectPermKeyDTO> dtos) {
        final List<SubjectPermKey> models = Lists.newArrayList();
        for (SubjectPermKeyDTO dto : dtos) {
            models.add(this.convertToModelKey(dto));
        }
        return models;
    }

    public Set<SubjectPermKeyDTO> convertToDTOKey(@NotNull final Set<SubjectPermKey> models) {
        final Set<SubjectPermKeyDTO> dtos = Sets.newHashSet();
        for (SubjectPermKey model : models) {
            dtos.add(this.convertToDTOKey(model));
        }
        return dtos;
    }

    public List<SubjectPermKeyDTO> convertToDTOKey(@NotNull final List<SubjectPermKey> models) {
        final List<SubjectPermKeyDTO> dtos = Lists.newArrayList();
        for (SubjectPermKey model : models) {
            dtos.add(this.convertToDTOKey(model));
        }
        return dtos;
    }

}
