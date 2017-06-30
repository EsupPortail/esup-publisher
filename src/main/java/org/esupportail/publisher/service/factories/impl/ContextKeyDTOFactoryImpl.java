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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.service.factories.CompositeKeyDTOFactory;
import org.esupportail.publisher.web.rest.dto.ContextKeyDTO;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 20 oct. 2014
 */
@Service
public class ContextKeyDTOFactoryImpl implements
    CompositeKeyDTOFactory<ContextKeyDTO, ContextKey, Long, ContextType> {

    public ContextKey convertToModelKey(@NotNull final ContextKeyDTO id) {
        return new ContextKey(id.getKeyId(), id.getKeyType());
    }

    public ContextKeyDTO convertToDTOKey(@NotNull final ContextKey id) {
        return new ContextKeyDTO(id.getKeyId(), id.getKeyType());
    }

    public Set<ContextKey> convertToModelKey(@NotNull final Set<ContextKeyDTO> dtos) {
        final Set<ContextKey> models = Sets.newHashSet();
        for (ContextKeyDTO dto : dtos) {
            models.add(this.convertToModelKey(dto));
        }
        return models;
    }

    public List<ContextKey> convertToModelKey(@NotNull final List<ContextKeyDTO> dtos) {
        final List<ContextKey> models = Lists.newArrayList();
        for (ContextKeyDTO dto : dtos) {
            models.add(this.convertToModelKey(dto));
        }
        return models;
    }

    public Set<ContextKeyDTO> convertToDTOKey(@NotNull final Set<ContextKey> models) {
        final Set<ContextKeyDTO> dtos = Sets.newHashSet();
        for (ContextKey model : models) {
            dtos.add(this.convertToDTOKey(model));
        }
        return dtos;
    }

    public List<ContextKeyDTO> convertToDTOKey(@NotNull final List<ContextKey> models) {
        final List<ContextKeyDTO> dtos = Lists.newArrayList();
        for (ContextKey model : models) {
            dtos.add(this.convertToDTOKey(model));
        }
        return dtos;
    }

}
