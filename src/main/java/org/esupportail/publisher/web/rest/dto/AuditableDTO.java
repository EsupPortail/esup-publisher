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
import java.time.Instant;

import javax.validation.constraints.NotNull;
import java.util.Date;

@ToString(callSuper=true)
public abstract class AuditableDTO extends AbstractIdDTO<Long> {

    @Getter
    private Instant creationDate;

    @Getter
    private Instant lastUpdateDate;

    @Getter
    @Setter
    @NotNull
    private SubjectDTO createdBy;

    @Getter
    @Setter
    private SubjectDTO lastUpdateBy;

    /**
     * Constructor to use to create the object from the JPA model.
     * @param creationDate
     * @param lastUpdateDate
     * @param createdBy
     * @param lastUpdateBy
     */
    public AuditableDTO(@NotNull final Long modelId, @NotNull final Instant creationDate, Instant lastUpdateDate,
            @NotNull SubjectDTO createdBy, SubjectDTO lastUpdateBy) {
        super(modelId);
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
        this.createdBy = createdBy;
        this.lastUpdateBy = lastUpdateBy;
    }

    /**
     * Constructor to use to create a new object.
     * @param createdBy
     */
    public AuditableDTO(@NotNull SubjectDTO createdBy) {
        super();
        this.createdBy = createdBy;
    }

}
