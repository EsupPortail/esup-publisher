package org.esupportail.publisher.web.rest.dto;

import lombok.*;
import org.esupportail.publisher.domain.enums.SubjectType;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 15 oct. 2014
 */
@Data
@NoArgsConstructor
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true, exclude={"displayName","foundOnExternalSource"})
public class SubjectDTO extends AbstractIdDTO<SubjectKeyDTO> implements IAbstractDTO<SubjectKeyDTO>, Serializable {

    private String displayName;

    private boolean foundOnExternalSource;


    public SubjectDTO(@NotNull final SubjectKeyDTO modelId, final String displayName, final boolean foundOnExternalSource) {
        super(modelId);
        this.displayName = displayName;
        this.foundOnExternalSource = foundOnExternalSource;
    }

    public SubjectDTO(@NotNull final String keyId, @NotNull final SubjectType keyType, final String displayName, final boolean foundOnExternalSource) {
        super(new SubjectKeyDTO(keyId, keyType));
        this.displayName = displayName;
        this.foundOnExternalSource = foundOnExternalSource;
    }

    public SubjectDTO(@NotNull final SubjectKeyDTO modelId) {
        super(modelId);
    }

    public SubjectDTO(@NotNull final String keyId, @NotNull final SubjectType keyType) {
        super(new SubjectKeyDTO(keyId, keyType));
    }

}
