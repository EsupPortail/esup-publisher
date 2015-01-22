package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.SubjectPermKey;
import org.esupportail.publisher.web.rest.dto.SubjectPermKeyDTO;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * Data Transfer Object factory for converting to and from models.
 * @author GIP RECIA - Julien Gribonvald
 */
public interface SubjectPermKeyDTOFactory {


    SubjectPermKey convertToModelKey(@NotNull final SubjectPermKeyDTO dto);

    Set<SubjectPermKey> convertToModelKey(@NotNull final Set<SubjectPermKeyDTO> dtos);

    List<SubjectPermKey> convertToModelKey(@NotNull final List<SubjectPermKeyDTO> dtos);


    SubjectPermKeyDTO convertToDTOKey(@NotNull final SubjectPermKey model);

    Set<SubjectPermKeyDTO> convertToDTOKey(@NotNull final Set<SubjectPermKey> models);

    List<SubjectPermKeyDTO> convertToDTOKey(@NotNull final List<SubjectPermKey> models);

}
