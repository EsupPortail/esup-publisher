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
/**
 *
 */
package org.esupportail.publisher.domain;

import java.io.Serializable;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.ItemStatus;
import java.time.Instant;
import java.time.LocalDate;

/**
 * @author GIP RECIA - Julien Gribonvald 2 juin 2017
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonTypeName("ATTACHMENT")
@Entity
public class Attachment extends AbstractItem implements Serializable {

    /**
     * Empty constructor of Attachment.
     */
    public Attachment() {
        super();
    }

    /**
     * Constructor of Attachment.
     *
     * @param title
     * @param enclosure
     * @param startDate
     * @param endDate
     * @param validatedDate
     * @param validatedBy
     * @param status
     * @param summary
     * @param rssAllowed
     * @param organization
     * @param redactor
     */
    public Attachment(final String title, final String enclosure,
                      final LocalDate startDate, final LocalDate endDate,
                      final Instant validatedDate, final User validatedBy,
                      final ItemStatus status, final String summary, final boolean rssAllowed,
                      final boolean highlight, final Organization organization, final Redactor redactor) {
        super(title, enclosure, startDate, endDate, validatedDate, validatedBy,
            status, summary, rssAllowed, highlight, organization, redactor);
    }

}
