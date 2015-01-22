/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * Copyright (C) 2014 RECIA http://www.recia.fr
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 				http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.domain.evaluators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=false)
@Entity
public abstract class AbstractAttributeValueEvaluator extends AbstractEvaluator {

    /** */
    private static final long serialVersionUID = -1385694165614313364L;
    /** The portlet preference attribute. */
    @NotNull
    @Size(min = 2, max=125)
    @Column(length=125)
    private String attribute;
    /** The portlet preference value. */
    @NotNull
    @Size(min = 1, max=512)
    @Column(length=512)
    private String value;

    /**
     * Constructor.
     */
    public AbstractAttributeValueEvaluator() {
        super();
    }

    /**
     * @param attribute User attribute
     * @param value value of the attribute
     */
    public AbstractAttributeValueEvaluator(String attribute, String value) {
        super();
        this.attribute = attribute;
        this.value = value;
    }



}
