package org.esupportail.publisher.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.util.CustomDateTimeDeserializer;
import org.esupportail.publisher.domain.util.CustomDateTimeSerializer;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.util.Date;

@ToString(callSuper=true)
public abstract class AuditableDTO extends AbstractIdDTO<Long> {

    @Getter
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private DateTime creationDate;

    @Getter
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private DateTime lastUpdateDate;

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
    public AuditableDTO(@NotNull final Long modelId, @NotNull final DateTime creationDate, DateTime lastUpdateDate,
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
