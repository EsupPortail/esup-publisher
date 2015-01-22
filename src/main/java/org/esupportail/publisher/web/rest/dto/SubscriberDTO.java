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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.SubscribeType;
import org.esupportail.publisher.domain.util.CustomEnumSerializer;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 29 Oct. 2014
 */
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public class SubscriberDTO extends AbstractIdDTO<SubjectContextKeyDTO> implements IAbstractDTO<SubjectContextKeyDTO>, Serializable {

    /** */
    private static final long serialVersionUID = 3469782122300041730L;

    @NotNull
    @Getter
    @Setter
    @JsonSerialize(using = CustomEnumSerializer.class)
    private SubscribeType subscribeType;

    /**
     * @param subjectKey
     * @param contextKey
     * @param subscribeType
     */
    public SubscriberDTO(@NotNull final SubjectKeyDTO subjectKey, @NotNull final ContextKeyDTO contextKey, @NotNull final SubscribeType subscribeType) {
        super(new SubjectContextKeyDTO(subjectKey, contextKey));
        this.subscribeType = subscribeType;
    }

}
