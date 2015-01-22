/**
 *
 */
package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.web.rest.dto.GroupDTO;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 * @author GIP RECIA - Julien Gribonvald 25 juil. 2014
 */
public interface GroupDTOFactory {


    GroupDTO from(@NotNull final IExternalGroup model);

    SubjectDTO liteFrom(@NotNull final String id);

    GroupDTO from(@NotNull final String id);

	List<GroupDTO> asDTOList(final Collection<IExternalGroup> models);

    List<SubjectDTO> asLiteDTOList(final Collection<IExternalGroup> models);

}
