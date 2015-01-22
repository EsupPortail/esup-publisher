package org.esupportail.publisher.web.rest.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by jgribonvald on 09/06/15.
 */
@ToString
@EqualsAndHashCode
public class ValueResource {

    private Object value;

    public ValueResource(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
