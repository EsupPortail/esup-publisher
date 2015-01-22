package org.esupportail.publisher.web.rest.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 15 oct. 2014
 */
@ToString
@EqualsAndHashCode
public class SubjectContextKeyDTO implements Serializable {

    /** */
    private static final long serialVersionUID = -3963584394811440859L;

    @NotNull
    @NonNull
    @Getter
    @Setter
    private SubjectKeyDTO subjectKey;

    @NotNull
    @NonNull
    @Getter
    @Setter
    private ContextKeyDTO contextKey;
    /**
     * @param subjectKey
     * @param contextKey
     */
    public SubjectContextKeyDTO(@NotNull final SubjectKeyDTO subjectKey, @NotNull final ContextKeyDTO contextKey) {
        super();
        this.subjectKey = subjectKey;
        this.contextKey = contextKey;
    }

}
