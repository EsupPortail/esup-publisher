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

import java.util.List;

import javax.validation.constraints.NotNull;

import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.web.rest.dto.SubscriberResolvedDTO;

public interface SubscriberResolvedDTOFactory {

	/**
	 * Copy the model to a new DTO.
	 *
	 * @param model
	 *            Model to translate
	 * @return Enhanced Model Object
	 */
	SubscriberResolvedDTO from(@NotNull final Subscriber model);

	/**
	 * Copy the list of models to a new list of transfer objects.
	 *
	 * @param models
	 *            Models to translate
	 * @return Enhanced Model Object
	 */
	List<SubscriberResolvedDTO> asDTOList(@NotNull final List<Subscriber> models);
}
