package org.esupportail.publisher.service.bean;

import lombok.*;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.SubjectKey;

/**
 * Created by jgribonvald on 20/04/15.
 * Only used for permission as wrapper to add owner information
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OwnerContextKey extends ContextKey {

    private SubjectKey owner;

    public OwnerContextKey(ContextKey context, SubjectKey ownerId) {
        super(context.getKeyId(), context.getKeyType());
        this.owner = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
