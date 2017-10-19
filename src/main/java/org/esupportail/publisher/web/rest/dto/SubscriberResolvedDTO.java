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

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.SubscribeType;
import org.esupportail.publisher.domain.util.CustomEnumSerializer;
import org.hibernate.validator.constraints.ScriptAssert;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 29 Oct. 2014
 */
@Data
@ToString
@EqualsAndHashCode
@ScriptAssert(lang = "javascript", script = "org.esupportail.publisher.web.rest.dto.SubscriberResolvedDTO.complexeValidation(_this)",
    message = "Not valid SubscriberResolvedDTO : the subject is not provided or can't be defined twice")
public class SubscriberResolvedDTO implements Serializable {

    /** */
    private static final long serialVersionUID = 3469782122300041730L;
    /** the SubjectDTO or the SubjectKeyExtendedDTO should be defined, but not the two at the same time. */
    private SubjectDTO subjectDTO;

    private SubjectKeyExtendedDTO subjectKeyExtendedDTO;

    @NonNull
    private ContextKeyDTO contextKeyDTO;

    @NonNull
    @JsonSerialize(using = CustomEnumSerializer.class)
    private SubscribeType subscribeType;

    /**
     * @param subject
     * @param contextKey
     * @param subscribeType
     */
    public SubscriberResolvedDTO(@NotNull final SubjectDTO subject, @NotNull final ContextKeyDTO contextKey, @NotNull final SubscribeType subscribeType) {
        this.subjectDTO = subject;
        this.contextKeyDTO = contextKey;
        this.subscribeType = subscribeType;
    }

    /**
     * @param subject
     * @param contextKey
     * @param subscribeType
     */
    public SubscriberResolvedDTO(@NotNull final SubjectKeyExtendedDTO subject, @NotNull final ContextKeyDTO contextKey, @NotNull final SubscribeType subscribeType) {
        this.subjectKeyExtendedDTO = subject;
        this.contextKeyDTO = contextKey;
        this.subscribeType = subscribeType;
    }

    public static boolean complexeValidation(final SubscriberResolvedDTO subscriber) {
        return subscriber.getSubjectDTO() == null &&  subscriber.getSubjectKeyExtendedDTO() != null ||
            subscriber.getSubjectKeyExtendedDTO() == null && subscriber.getSubjectDTO() != null;
    }

}
