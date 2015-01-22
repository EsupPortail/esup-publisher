package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 17 oct. 2014
 */
public interface SubjectDTOSimpleFactory {

    SubjectDTO from(@NotNull SubjectKey modelKey);
    List<SubjectDTO> asDTOList(@NotNull Collection<SubjectKey> modelKey);

}
