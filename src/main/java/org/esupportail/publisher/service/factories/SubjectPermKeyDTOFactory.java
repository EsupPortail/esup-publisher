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
