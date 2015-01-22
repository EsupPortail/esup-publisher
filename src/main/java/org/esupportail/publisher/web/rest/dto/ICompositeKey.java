package org.esupportail.publisher.web.rest.dto;

import java.io.Serializable;


public interface ICompositeKey<ID extends Serializable,TYPE> {

    /**
     * @return the keyId
     */
    public ID getKeyId();

    /**
     * @param keyId the keyId to set
     */
    //public void setKeyId(final ID keyId);

    /**
     * @return the keyType
     */
    public TYPE getKeyType();

    /**
     * @param keyType the keyType to set
     */
    //public void setKeyType(final TYPE keyType);



}
