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

import lombok.ToString;
import org.esupportail.publisher.web.rest.dto.AbstractIdDTO;
import org.esupportail.publisher.web.rest.dto.IAbstractDTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ToString(callSuper=true)
public abstract class EvaluatorDTO extends AbstractIdDTO<Long> implements IAbstractDTO<Long>, Serializable {

    /** */
    private static final long serialVersionUID = -683180383538160744L;

    /**
     *Constructor to use when creating a new object.
     */
    public EvaluatorDTO() {
        super();
    }

    /**
     * Constructor to use when creating the object from JPA model.
     * @param modelId
     */
    public EvaluatorDTO(@NotNull final Long modelId) {
        super(modelId);
    }


}
