package org.esupportail.publisher.web.rest.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.util.CstPropertiesLength;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 15 oct. 2014
 */
@ToString(callSuper=true)
public class ResourceDTO extends ItemDTO implements Serializable {

    /** */
    private static final long serialVersionUID = -9093926804044066760L;

    @Getter
    @Setter
    @NotNull
    @NonNull
    @Size(min=5, max= CstPropertiesLength.URL)
    private String ressourceUrl;

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
     * @param organization
     * @param redactor
     * @param creationDate
     * @param lastUpdateDate
     * @param createdBy
     * @param lastUpdateBy
     * @param ressourceUrl
     */
    public ResourceDTO(@NotNull final Long modelId, @NotNull final String title, final String enclosure,
            @NotNull final String ressourceUrl, @NotNull final LocalDate startDate, @NotNull final LocalDate endDate,
            final DateTime validatedDate, final SubjectDTO validatedBy, @NotNull final ItemStatus status,
            @NotNull final String summary, final boolean rssAllowed, @NotNull final OrganizationDTO organization,
            @NotNull final RedactorDTO redactor, @NotNull final DateTime creationDate, final DateTime lastUpdateDate,
            @NotNull final SubjectDTO createdBy, final SubjectDTO lastUpdateBy) {
        super(modelId, title, enclosure, startDate, endDate, validatedDate,
                validatedBy, status, summary, rssAllowed, organization, redactor,
                creationDate, lastUpdateDate, createdBy, lastUpdateBy);
        this.ressourceUrl = ressourceUrl;
    }

    /**
     * Constructor to use when creating a new Object.
     * @param createdBy
     * @param organization
     * @param redactor
     */
    public ResourceDTO(@NotNull final SubjectDTO createdBy, @NotNull final OrganizationDTO organization,
            @NotNull final RedactorDTO redactor) {
        super(createdBy, organization, redactor);
    }





}
