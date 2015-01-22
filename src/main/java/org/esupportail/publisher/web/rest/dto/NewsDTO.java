package org.esupportail.publisher.web.rest.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@ToString(callSuper=true)
public class NewsDTO extends ItemDTO implements Serializable {

    @Getter
    @Setter
    @NotNull
    @NonNull
    @Size(min=5)
    private String body;

    /**
     * Constructor to use when creating the object from JPA model.
     * @param modelId
     * @param title
     * @param enclosure
     * @param startDate
     * @param endDate
     * @param validatedDate
     * @param validatedBy
     * @param status
     * @param summary
     * @param organization
     * @param redactor
     * @param creationDate
     * @param lastUpdateDate
     * @param createdBy
     * @param lastUpdateBy
     */
    public NewsDTO(@NotNull final Long modelId, @NotNull final String title, final String enclosure,
            @NotNull final String body, @NotNull final LocalDate startDate, @NotNull final LocalDate endDate,
            final DateTime validatedDate, final SubjectDTO validatedBy, @NotNull final ItemStatus status,
            @NotNull final String summary, @NotNull final OrganizationDTO organization,
            @NotNull final RedactorDTO redactor, @NotNull final DateTime creationDate, final DateTime lastUpdateDate,
            @NotNull final SubjectDTO createdBy, final SubjectDTO lastUpdateBy) {
        super(modelId, title, enclosure, startDate, endDate, validatedDate,
                validatedBy, status, summary, organization, redactor, creationDate,
                lastUpdateDate, createdBy, lastUpdateBy);
        this.body = body;
    }

    /**
     * Constructor to use when creating a new object.
     * @param createdBy
     * @param organization
     * @param redactor
     */
    public NewsDTO(@NotNull final SubjectDTO createdBy, @NotNull final OrganizationDTO organization,
            @NotNull final RedactorDTO redactor) {
        super(createdBy, organization, redactor);
    }



}
