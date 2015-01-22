package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.IContext;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.web.rest.dto.TreeJS;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by jgribonvald on 28/05/15.
 */
public interface TreeJSDTOFactory {

    /**
     * Copy the model to a new DTO.
     *
     * @param model
     *            Model to translate
     * @param minPerm
     *            Childs with min perm to filter
     * @return TreeJS object
     */
    TreeJS from(@NotNull final IContext model, PermissionType minPerm);

    /**
     * Copy the list of models to a new list of transfer objects.
     *
     * @param models
     *            Models to translate
     * @param minPerm
     *            Childs with min perm to filter
     * @return List of TreeJS object
     */
    List<TreeJS> asDTOList(@NotNull final List<? extends IContext> models, PermissionType minPerm);

    /**
     * Copy the model to a new DTO.
     *
     * @param model
     *            TreeJS object
     * @return Enhanced Model Object
     */
    TreeJS from(@NotNull final IExternalGroup model);

    /**
     * Copy the list of models to a new list of transfer objects.
     *
     * @param models
     *            Models to translate
     * @return List of TreeJS object
     */
    List<TreeJS> asDTOList(@NotNull final List<IExternalGroup> models);

}
