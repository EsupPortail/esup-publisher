package org.esupportail.publisher.web.rest.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.ToString;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

@ToString(callSuper=true)
public class AttachmentDTO extends ItemDTO implements Serializable {

    /**
     * Constructor to use to create the object from the JPA model.
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
     * @param creationDate
     * @param lastUpdateDate
     * @param createdBy
     * @param lastUpdateBy
     */
    public AttachmentDTO(@NotNull final Long modelId, @NotNull final String title, @NotNull final String enclosure,
                         @NotNull final LocalDate startDate, final LocalDate endDate,
                         final DateTime validatedDate, final SubjectDTO validatedBy, @NotNull final ItemStatus status,
                         @NotNull final String summary, final boolean rssAllowed, final boolean highlight, @NotNull final OrganizationDTO organization,
                         @NotNull final RedactorDTO redactor, @NotNull final DateTime creationDate, final DateTime lastUpdateDate,
                         @NotNull final SubjectDTO createdBy, final SubjectDTO lastUpdateBy) {
        super(modelId, title, enclosure, startDate, endDate, validatedDate,
                validatedBy, status, summary, rssAllowed, highlight, organization, redactor, creationDate,
                lastUpdateDate, createdBy, lastUpdateBy);
    }

    /**
     * Constructor to use when creating a new Object.
     * @param createdBy
     * @param organization
     * @param redactor
     */
    public AttachmentDTO(@NotNull final SubjectDTO createdBy, @NotNull final OrganizationDTO organization, @NotNull final RedactorDTO redactor) {
        super(createdBy, organization, redactor);
    }


}
