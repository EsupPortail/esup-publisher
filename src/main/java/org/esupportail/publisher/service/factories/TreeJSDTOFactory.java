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
