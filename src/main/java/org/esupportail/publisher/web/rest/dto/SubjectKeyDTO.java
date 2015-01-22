package org.esupportail.publisher.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.util.CustomEnumSerializer;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 15 oct. 2014
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class SubjectKeyDTO implements ICompositeKey<String, SubjectType>, Serializable {

    @NotNull
    @NonNull
    private String keyId;
    @NotNull
    @NonNull
    @JsonSerialize(using = CustomEnumSerializer.class)
    private SubjectType keyType;
    /**
     * @param keyId
     * @param keyType
     */
    public SubjectKeyDTO(@NonNull final String keyId, @NonNull final SubjectType keyType) {
        this.keyId = keyId;
        this.keyType = keyType;
    }

}
