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
