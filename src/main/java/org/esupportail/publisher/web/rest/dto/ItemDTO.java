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

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.util.CstPropertiesLength;
import org.hibernate.validator.constraints.ScriptAssert;
import java.time.Instant;
import java.time.LocalDate;

@ToString
@ScriptAssert(lang = "javascript", script = "org.esupportail.publisher.domain.AbstractItem.complexeDateValidation(_this.redactor.optionalPublishTime, _this.startDate, _this.endDate, _this.redactor.nbDaysMaxDuration)"
    , message = "Not valid startDate that should be before endDate or with maximum number of days duration")
public abstract class ItemDTO extends PermissibleDTO {

    @Getter
    @Setter
    @NotNull
    @Size(max= CstPropertiesLength.ITEM_TITLE)
    private String title;

    @Getter
    @Setter
    @Size(max=CstPropertiesLength.URL)
    private String enclosure;

    @Getter
    @Setter
    @NotNull
    private LocalDate startDate;

    @Getter
    @Setter
    @Future
    private LocalDate endDate;

    @Getter
    @Setter
    private Instant validatedDate;

    @Getter
    @Setter
    private SubjectDTO validatedBy;

    @NotNull
    @Getter
    @Setter
    private ItemStatus status;

    @NotNull
    @Size(min=5, max=CstPropertiesLength.SUMMARY)
    @Getter
    @Setter
    private String summary;

    @Getter
    @Setter
    private boolean rssAllowed;

    @Getter
    @Setter
    private boolean highlight;

    @Getter
    private OrganizationDTO organization;

    @Getter
    private RedactorDTO redactor;



    /**
     * Constructor to use when creating the object from JPA model.
     * @param creationDate
     * @param lastUpdateDate
     * @param createdBy
     * @param lastUpdateBy
     * @param modelId
     * @param title
     * @param enclosure
     * @param startDate
     * @param endDate
     * @param validatedDate
     * @param validatedBy
     * @param status
     * @param summary
     * @param rssAllowed
     * @param highlight
     * @param organization
     * @param redactor
     */
    public ItemDTO(@NotNull final Long modelId, @NotNull final String title, final String enclosure,
            @NotNull final LocalDate startDate, final LocalDate endDate,
            final Instant validatedDate, final SubjectDTO validatedBy, @NotNull final ItemStatus status,
            @NotNull final String summary, final boolean rssAllowed, final boolean highlight, @NotNull final OrganizationDTO organization,
            @NotNull final RedactorDTO redactor, @NotNull final Instant creationDate, final Instant lastUpdateDate,
            @NotNull final SubjectDTO createdBy, final SubjectDTO lastUpdateBy) {
        super(modelId, creationDate, lastUpdateDate, createdBy, lastUpdateBy, ContextType.ITEM);
        this.title = title;
        this.enclosure = enclosure;
        this.startDate = startDate;
        this.endDate = endDate;
        this.validatedDate = validatedDate;
        this.validatedBy = validatedBy;
        this.status = status;
        this.summary = summary;
        this.rssAllowed = rssAllowed;
        this.highlight = highlight;
        this.organization = organization;
        this.redactor = redactor;
    }

    /**
     * Constructor to use when creating a new object.
     * @param createdBy
     * @param organization
     * @param redactor
     */
    public ItemDTO(@NotNull final SubjectDTO createdBy, @NotNull final OrganizationDTO organization, @NotNull final RedactorDTO redactor) {
        super(createdBy, ContextType.ITEM);
        this.organization = organization;
        this.redactor = redactor;
    }

    @Override
    public ContextKeyDTO getContextKeyDTO() {
        return new ContextKeyDTO(this.getModelId(), this.getType());
    }

}
