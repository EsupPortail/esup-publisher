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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.util.CstPropertiesLength;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 13 oct. 2014
 */
@NoArgsConstructor
@ToString(callSuper=true)
public class RedactorDTO extends AbstractIdDTO<Long> implements IAbstractDTO<Long>, Serializable {

    /** This field corresponds to the database column name. */
    @Getter
    @Setter
    @NotNull
    @Size(min=1,max= CstPropertiesLength.NAME)
    private String name;

    /** This field corresponds to the database column display_name. */
    @Getter
    @Setter
    @NotNull
    @Size(min=1, max=CstPropertiesLength.DISPLAYNAME)
    private String displayName;

    /** This field corresponds to the database column description. */
    @Getter
    @Setter
    @NotNull
    @Size(min=5, max=CstPropertiesLength.DESCRIPTION)
    private String description;

    /** This field corresponds to the database column name. */
    @Getter
    @Setter
    @Min(1)
    @Max(2)
    private int nbLevelsOfClassification;

    @Getter
    @Setter
    private boolean optionalPublishTime = false;

    @Getter
    @Setter
    @Min(1)
    @Max(999)
    private int nbDaysMaxDuration;

    /**
     * @param modelId
     * @param name
     * @param displayName
     * @param description
     * @param nbLevelsOfClassification
     */
    public RedactorDTO(@NotNull final Long modelId, @NotNull final String name, @NotNull final String displayName,
            @NotNull final String description, @NotNull final int nbLevelsOfClassification, final boolean optionalPublishTime,
                       final int nbDaysMaxDuration) {
        super(modelId);
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.nbLevelsOfClassification = nbLevelsOfClassification;
        this.optionalPublishTime = optionalPublishTime;
        this.nbDaysMaxDuration = nbDaysMaxDuration;
    }



}
