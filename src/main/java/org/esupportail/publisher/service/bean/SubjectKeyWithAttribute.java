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
package org.esupportail.publisher.service.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.enums.SubjectType;

/**
 * Created by jgribonvald on 14/04/17.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SubjectKeyWithAttribute extends SubjectKey {

    @NonNull
    private String attributeName;

    /**
     * Empty constructor of SubjectKeyWithAttribute.
     */
    public SubjectKeyWithAttribute() {
        super();
    }

    /**
     * Constructor of SubjectKeyWithAttribute.
     * @param keyId
     * @param keyType
     * @param attributeName
     */
    public SubjectKeyWithAttribute(final String keyId, final SubjectType keyType, final String attributeName) {
        super(keyId, keyType);
        this.attributeName = attributeName;
    }
}
