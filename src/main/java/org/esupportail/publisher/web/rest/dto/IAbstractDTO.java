package org.esupportail.publisher.web.rest.dto;

import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 18 juil. 2014
 */
public interface IAbstractDTO<ID extends Serializable> {

    ID getModelId();

}
