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
package org.esupportail.publisher.web.rest.dto.evaluators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.OperatorType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=true)
public class OperatorEvaluatorDTO extends EvaluatorDTO implements Serializable {

    /** */
    private static final long serialVersionUID = 8235163884629100798L;

    @NotNull
    private OperatorType type;

    /** List of evaluators to evaluate, must contains more than one evaluator. */
    @NotNull
    @Size(min=1)
    private Set<EvaluatorDTO> evaluators = new HashSet<EvaluatorDTO>();

    /**
     * @param type
     * @param evaluators
     */
    public OperatorEvaluatorDTO(@NotNull final OperatorType type, @NotNull final Set<EvaluatorDTO> evaluators) {
        super();
        this.type = type;
        this.evaluators = evaluators;
    }

    /**
     * @param modelId
     * @param type
     * @param evaluators
     */
    public OperatorEvaluatorDTO(@NotNull final Long modelId, @NotNull final OperatorType type,
            @NotNull @Size(min=1) final Set<EvaluatorDTO> evaluators) {
        super(modelId);
        this.type = type;
        this.evaluators = evaluators;
    }


}
