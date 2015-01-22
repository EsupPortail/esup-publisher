package org.esupportail.publisher.service.factories.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.service.factories.CompositeKeyDTOFactory;
import org.esupportail.publisher.web.rest.dto.SubjectKeyDTO;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Service
public class SubjectKeyDTOFactoryImpl implements
    CompositeKeyDTOFactory<SubjectKeyDTO, SubjectKey, String, SubjectType> {

    public SubjectKey convertToModelKey(@NotNull final SubjectKeyDTO id) {
        return new SubjectKey(id.getKeyId(), id.getKeyType());
    }

    public SubjectKeyDTO convertToDTOKey(@NotNull final SubjectKey id) {
        return new SubjectKeyDTO(id.getKeyId(), id.getKeyType());
    }

    public Set<SubjectKey> convertToModelKey(@NotNull final Set<SubjectKeyDTO> dtos) {
        final Set<SubjectKey> models = Sets.newHashSet();
        for (SubjectKeyDTO dto : dtos) {
            models.add(this.convertToModelKey(dto));
        }
        return models;
    }

    public List<SubjectKey> convertToModelKey(@NotNull final List<SubjectKeyDTO> dtos) {
        final List<SubjectKey> models = Lists.newArrayList();
        for (SubjectKeyDTO dto : dtos) {
            models.add(this.convertToModelKey(dto));
        }
        return models;
    }

    public Set<SubjectKeyDTO> convertToDTOKey(@NotNull final Set<SubjectKey> models) {
        final Set<SubjectKeyDTO> dtos = Sets.newHashSet();
        for (SubjectKey model : models) {
            dtos.add(this.convertToDTOKey(model));
        }
        return dtos;
    }

    public List<SubjectKeyDTO> convertToDTOKey(@NotNull final List<SubjectKey> models) {
        final List<SubjectKeyDTO> dtos = Lists.newArrayList();
        for (SubjectKey model : models) {
            dtos.add(this.convertToDTOKey(model));
        }
        return dtos;
    }

}
