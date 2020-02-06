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
package org.esupportail.publisher.web.rest.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.web.rest.dto.evaluators.EvaluatorDTO;
import java.time.Instant;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 21 juil. 2014
 */
@Getter
@ToString(callSuper=true)
public abstract class PermissionDTO extends AuditableDTO implements IAbstractDTO<Long> {
    /** */
    private static final long serialVersionUID = -1951934656598078964L;

    @NotNull
    private ContextKeyDTO context;
    /** The evaluator to address permissions */
    @NotNull
    @Setter
    @Valid
    private EvaluatorDTO evaluator;

    public PermissionDTO(@NotNull final Long modelId, final Instant creationDate, final Instant lastUpdateDate,
                                      @NotNull final SubjectDTO createdBy, final SubjectDTO lastUpdateBy,
                                      @NotNull final ContextKeyDTO context, @NotNull final EvaluatorDTO evaluator) {
        super(modelId, creationDate, lastUpdateDate, createdBy, lastUpdateBy);
        this.context = context;
        this.evaluator = evaluator;
    }

    public PermissionDTO(@NotNull final SubjectDTO createdBy, @NotNull final ContextKeyDTO context) {
        super(createdBy);
        this.context = context;
    }

    public String toStringLite() {
        return getClass().getSimpleName() + "{" +
            "context=" + context +
            ", evaluator=" + evaluator +
            '}';
    }
}
