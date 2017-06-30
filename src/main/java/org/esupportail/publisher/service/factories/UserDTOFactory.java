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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.web.rest.dto.UserDTO;

/**
 * @author GIP RECIA - Julien Gribonvald 25 juil. 2014
 */
public interface UserDTOFactory {

	User from(@NotNull final UserDTO dtObject);

	UserDTO from(final IExternalUser extModel, final boolean withInternal);

	UserDTO from(final User model, final IExternalUser extModel);

	UserDTO from(@NotNull final User model);

	UserDTO from(@NotNull final String id);

	Set<UserDTO> asDTOSet(final Collection<IExternalUser> models,
			boolean withInternal);

	List<UserDTO> asDTOList(final Collection<IExternalUser> models,
			boolean withInternal);

}
