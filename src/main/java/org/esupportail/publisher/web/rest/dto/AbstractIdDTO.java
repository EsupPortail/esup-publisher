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

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 24 mai 2014
 */
@ToString
@EqualsAndHashCode
public abstract class AbstractIdDTO<ID extends Serializable> implements IAbstractDTO<ID>, Serializable {

    @NonNull
    @NotNull
    private ID modelId;


    public AbstractIdDTO(final ID modelId) {
        super();
        this.modelId = modelId;
    }

    public AbstractIdDTO() {
        super();
    }


    public ID getModelId() {
        return modelId;
    }

    public void setModelId(ID modelId) {
        this.modelId = modelId;
    }

}
