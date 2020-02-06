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
package org.esupportail.publisher.web.rest.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.ContextType;
import java.time.Instant;

import javax.validation.constraints.NotNull;

@ToString(callSuper=true)
public abstract class PermissibleDTO extends AuditableDTO {

    @NotNull
    @Getter(value= AccessLevel.PROTECTED)
    private ContextType type;

    public PermissibleDTO(@NotNull final Long modelId, @NotNull final Instant creationDate, final Instant lastUpdateDate,
            @NotNull final SubjectDTO createdBy, final SubjectDTO lastUpdateBy, @NotNull final ContextType type) {
        super(modelId, creationDate, lastUpdateDate, createdBy, lastUpdateBy);
        this.type = type;
    }

    /**
     * @param createdBy
     * @param type
     */
    public PermissibleDTO(@NotNull final SubjectDTO createdBy, @NotNull final ContextType type) {
        super(createdBy);
        this.type = type;
    }

    public abstract ContextKeyDTO getContextKeyDTO();

}
