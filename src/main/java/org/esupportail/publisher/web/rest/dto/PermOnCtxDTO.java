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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.web.rest.dto.evaluators.EvaluatorDTO;
import java.time.Instant;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ToString(callSuper=true)
public class PermOnCtxDTO extends PermissionDTO implements Serializable {

    /** */
    private static final long serialVersionUID = -4415576815509825654L;

    /** The role. */
    @NotNull
    @Setter
    @Getter
    private PermissionType role;

    /**
     * Constructor to use when creating the object from JPA model.
     * @param modelId
     * @param creationDate
     * @param lastUpdateDate
     * @param createdBy
     * @param lastUpdateBy
     * @param context
     * @param evaluator
     * @param role
     */
    public PermOnCtxDTO(@NotNull final Long modelId, final Instant creationDate, final Instant lastUpdateDate,
            @NotNull final SubjectDTO createdBy, final SubjectDTO lastUpdateBy,
            @NotNull final ContextKeyDTO context, @NotNull final EvaluatorDTO evaluator, @NotNull final PermissionType role) {
        super(modelId, creationDate, lastUpdateDate, createdBy, lastUpdateBy, context, evaluator);
        this.role = role;
    }

    /**
     * Constructor to use when creating a new object.
     * @param createdBy
     * @param context
     */
    public PermOnCtxDTO(@NotNull final SubjectDTO createdBy, @NotNull final ContextKeyDTO context) {
        super(createdBy, context);
    }
}
