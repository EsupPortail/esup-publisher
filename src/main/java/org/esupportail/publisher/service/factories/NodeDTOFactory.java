package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.IContext;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.web.rest.dto.NodeDTO;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by jgribonvald on 28/05/15.
 */
public interface NodeDTOFactory {

    /**
     * Copy the model to a new DTO.
     *
     * @param model
     *            Model to translate
     * @param minPerm
     *            Childs with min perm to filter
     * @return Enhanced Model Object
     */
    NodeDTO from(@NotNull final IContext model, PermissionType minPerm);

    /**
     * Copy the list of models to a new list of transfer objects.
     *
     * @param models
     *            Models to translate
     * @param minPerm
     *            Childs with min perm to filter
     * @return Enhanced Model Object
     */
    List<NodeDTO> asDTOList(@NotNull final List<? extends IContext> models, PermissionType minPerm);

}
