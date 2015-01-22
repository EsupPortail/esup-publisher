package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.AbstractEntity;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.web.rest.dto.AbstractIdDTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Data Transfer Object factory for converting to and from models.
 *
 * @author GIP RECIA - Julien Gribonvald
 * @param <DTObject>
 *            DTO type for the corresponding model
 * @param <M>
 *            Model type
 * @param <IDMODEL>
 *            Type of id of Model type
 * @param <IDDTO>
 *            Type of id of DTObject type
 */
public interface GenericDTOFactory<DTObject extends AbstractIdDTO<IDDTO>, M extends AbstractEntity<IDMODEL>, IDDTO extends Serializable, IDMODEL extends Serializable> {

	/**
	 * Copy the model to a new DTO.
	 *
	 * @param model
	 *            Model to copy
	 * @return Transfer object equivalent of the specified model
	 */
	DTObject from(@NotNull final M model);

	/**
	 * Copy a transfer object to a new model.
	 *
	 * @param dtObject
	 *            Transfer object to copy
	 * @return Model equivalent of the specified transfer object
	 * @throws ObjectNotFoundException
	 *             If any referenced objects could not be loaded from persistent
	 *             storage
	 */
	M from(@NotNull final DTObject dtObject) throws ObjectNotFoundException;

	/**
	 * Copy a transfer object to a new model.
	 *
	 * @param idDTO
	 *            Transfer object to copy
	 * @return Model equivalent of the specified transfer object
	 * @throws ObjectNotFoundException
	 *             If any referenced objects could not be loaded from persistent
	 *             storage
	 */
	M from(@NotNull final IDDTO idDTO) throws ObjectNotFoundException;

	/**
	 * Copy the list of models to a new list of transfer objects.
	 *
	 * @param models
	 *            Models to copy
	 * @return Transfer object equivalents of the specified models
	 * @see #asDTOSet(java.util.Collection)
	 */
	List<DTObject> asDTOList(@NotNull final Collection<M> models);

	/**
	 * Copy the set of models to a new set of transfer objects.
	 *
	 * @param models
	 *            Models to copy
	 * @return Transfer object equivalents of the specified models
	 * @see #asDTOList(java.util.Collection)
	 */
	Set<DTObject> asDTOSet(@NotNull final Collection<M> models);

	/**
	 * Copy the Page of models to a new Page of transfer objects.
	 *
	 * @param models
	 *            Models to copy
	 * @return Transfer object equivalents of the specified models
	 * @see #asTOPage(Page)
	 */
	// Page<DTObject> asDTOPage(@NotNull final Page<M> models);

	/**
	 * Copy transfer objects to new model instances.
	 *
	 * @param tObjects
	 *            Transfer objects to copy
	 * @return Model equivalents of the specified transfer objects
	 * @throws ObjectNotFoundException
	 *             If any referenced objects could not be loaded from persistent
	 *             storage
	 */
	Set<M> asSet(@NotNull final Collection<DTObject> tObjects) throws ObjectNotFoundException;
}
