package org.esupportail.publisher.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.util.*;
import org.hibernate.validator.constraints.ScriptAssert;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ToString
@ScriptAssert(lang = "javascript", script = "_this.startDate.before(_this.endDate)")
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
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    private LocalDate startDate;

    @Getter
    @Setter
    @NotNull
    @Future
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    private LocalDate endDate;

    @Getter
    @Setter
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private DateTime validatedDate;

    @Getter
    @Setter
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
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
     * @param organization
     * @param redactor
     */
    public ItemDTO(@NotNull final Long modelId, @NotNull final String title, final String enclosure,
            @NotNull final LocalDate startDate, @NotNull final LocalDate endDate,
            final DateTime validatedDate, final SubjectDTO validatedBy, @NotNull final ItemStatus status,
            @NotNull final String summary, final boolean rssAllowed, @NotNull final OrganizationDTO organization,
            @NotNull final RedactorDTO redactor, @NotNull final DateTime creationDate, final DateTime lastUpdateDate,
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
