package org.esupportail.publisher.service.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.enums.SubjectType;

/**
 * Created by jgribonvald on 14/04/17.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SubjectKeyWithAttribute extends SubjectKey {

    @NonNull
    private String attributeName;

    /**
     * Empty constructor of SubjectKeyWithAttribute.
     */
    public SubjectKeyWithAttribute() {
        super();
    }

    /**
     * Constructor of SubjectKeyWithAttribute.
     * @param keyId
     * @param keyType
     * @param attributeName
     */
    public SubjectKeyWithAttribute(final String keyId, final SubjectType keyType, final String attributeName) {
        super(keyId, keyType);
        this.attributeName = attributeName;
    }
}
