package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.AbstractEntity;
import org.esupportail.publisher.web.rest.dto.AbstractIdDTO;

import java.io.Serializable;

/**
 * Data Transfer Object factory for converting to and from models.
 * @author GIP RECIA - Julien Gribonvald
 * @param <DTObject> DTO type for the corresponding model
 * @param <M> Model type
 * @param <ID> Type of id of Model type
 */
public interface DTOFactory<DTObject extends AbstractIdDTO<ID>, M extends AbstractEntity<ID>, ID extends Serializable>
    extends GenericDTOFactory<DTObject, M, ID, ID>{


}
