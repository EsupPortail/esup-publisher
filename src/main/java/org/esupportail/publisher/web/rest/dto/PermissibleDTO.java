package org.esupportail.publisher.web.rest.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.ContextType;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

@ToString(callSuper=true)
public abstract class PermissibleDTO extends AuditableDTO {

    @NotNull
    @Getter(value= AccessLevel.PROTECTED)
    private ContextType type;

    public PermissibleDTO(@NotNull final Long modelId, @NotNull final DateTime creationDate, final DateTime lastUpdateDate,
            @NotNull final SubjectDTO createdBy, final SubjectDTO lastUpdateBy, @NotNull final ContextType type) {
        super(modelId, creationDate, lastUpdateDate, createdBy, lastUpdateBy);
        this.type = type;
    }

    /**
     * @param createdBy
     * @param type
     */
    public PermissibleDTO(@NotNull final SubjectDTO createdBy, @NotNull final ContextType type) {
        super(createdBy);
        this.type = type;
    }

    public abstract ContextKeyDTO getContextKeyDTO();

}
