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
package org.esupportail.publisher.service.factories.impl;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.esupportail.publisher.domain.SubjectKeyExtended;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.service.factories.CompositeKeyExtendedDTOFactory;
import org.esupportail.publisher.service.factories.SubjectDTOToExtendedKeyConverterFactory;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;
import org.esupportail.publisher.web.rest.dto.SubjectKeyExtendedDTO;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Service
public class SubjectKeyExtendedDTOFactoryImpl implements
		CompositeKeyExtendedDTOFactory<SubjectKeyExtendedDTO, SubjectKeyExtended, String, String, SubjectType>,
		SubjectDTOToExtendedKeyConverterFactory {

	public SubjectKeyExtended convertToModelKey(@NotNull final SubjectKeyExtendedDTO id) {
		return new SubjectKeyExtended(id.getKeyValue(), id.getKeyAttribute(), id.getKeyType());
	}

    public SubjectKeyExtended convertToModelKey(@NotNull SubjectDTO dto) {
        return new SubjectKeyExtended(dto.getModelId().getKeyId(), dto.getModelId().getKeyType());
    }

    public SubjectKeyExtendedDTO convertToDTOKey(@NotNull final SubjectKeyExtended id) {
		return new SubjectKeyExtendedDTO(id.getKeyValue(), id.getKeyAttribute(), id.getKeyType());
	}

	public Set<SubjectKeyExtended> convertToModelKey(@NotNull final Set<SubjectKeyExtendedDTO> dtos) {
		final Set<SubjectKeyExtended> models = Sets.newHashSet();
		for (SubjectKeyExtendedDTO dto : dtos) {
			models.add(this.convertToModelKey(dto));
		}
		return models;
	}

	public List<SubjectKeyExtended> convertToModelKey(@NotNull final List<SubjectKeyExtendedDTO> dtos) {
		final List<SubjectKeyExtended> models = Lists.newArrayList();
		for (SubjectKeyExtendedDTO dto : dtos) {
			models.add(this.convertToModelKey(dto));
		}
		return models;
	}

	public Set<SubjectKeyExtendedDTO> convertToDTOKey(@NotNull final Set<SubjectKeyExtended> models) {
		final Set<SubjectKeyExtendedDTO> dtos = Sets.newHashSet();
		for (SubjectKeyExtended model : models) {
			dtos.add(this.convertToDTOKey(model));
		}
		return dtos;
	}

	public List<SubjectKeyExtendedDTO> convertToDTOKey(@NotNull final List<SubjectKeyExtended> models) {
		final List<SubjectKeyExtendedDTO> dtos = Lists.newArrayList();
		for (SubjectKeyExtended model : models) {
			dtos.add(this.convertToDTOKey(model));
		}
		return dtos;
	}

}
