package org.esupportail.publisher.domain;

import lombok.ToString;

import java.io.Serializable;

@ToString
//@MappedSuperclass
public abstract class AbstractEntity<ID extends Serializable> implements Serializable, IEntity<ID> {

    public AbstractEntity() {
        super();
    }

}
