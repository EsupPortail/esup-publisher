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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.ContextType;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 14 juin 2012
 */
@ToString
@EqualsAndHashCode
public class ContextKeyDTO implements ICompositeKey<Long, ContextType>, Serializable {

    @NotNull
    @Getter
    private Long keyId;

    @NotNull
    @Getter
    private ContextType keyType;

    /**
     * Contructor of the object ContextKey.java.
     * @param keyId
     * @param keyType
     */
    public ContextKeyDTO(@NotNull final long keyId, @NotNull final ContextType keyType) {
        this.keyId = keyId;
        this.keyType = keyType;
    }

}
