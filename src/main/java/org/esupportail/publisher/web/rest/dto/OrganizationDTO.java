package org.esupportail.publisher.web.rest.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.util.CstPropertiesLength;
import org.joda.time.DateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 21 sept. 2014
 */
@ToString
public class OrganizationDTO extends PermissibleDTO implements IAbstractDTO<Long>, Serializable {

    @Getter
    @Setter
    @NotNull
    @Size(min=1,max= CstPropertiesLength.ORG_NAME)
    private String name;
    @Getter
    @Setter
    @Size(max=CstPropertiesLength.ORG_DISPLAYNAME)
    private String displayName;
    @Getter
    @Setter
    @NotNull
    @Size(min=5, max=CstPropertiesLength.DESCRIPTION)
    private String description;
    @Getter
    @Setter
    @NotNull
    @Max(999)
    private int displayOrder;
    @Getter
    @Setter
    private boolean allowNotifications;

    /**
     * @param creationDate
     * @param lastUpdateDate
     * @param createdBy
     * @param lastUpdateBy
     * @param modelId
     * @param name
     * @param displayName
     * @param description
     * @param displayOrder
     * @param allowNotifications
     */
    public OrganizationDTO(@NotNull final Long modelId, @NonNull final DateTime creationDate, final DateTime lastUpdateDate,
            @NonNull final SubjectDTO createdBy, final SubjectDTO lastUpdateBy,
            @NonNull final String name, final String displayName,
            @NonNull final String description,final int displayOrder, final boolean allowNotifications) {
        super(modelId, creationDate, lastUpdateDate, createdBy, lastUpdateBy, ContextType.ORGANIZATION);
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.displayOrder = displayOrder;
        this.allowNotifications = allowNotifications;
    }

    /**
     * @param createdBy
     */
    public OrganizationDTO(@NonNull final SubjectDTO createdBy) {
        super(createdBy, ContextType.ORGANIZATION);
    }

    @Override
    public ContextKeyDTO getContextKeyDTO() {
        return new ContextKeyDTO(this.getModelId(), this.getType());
    }

}
