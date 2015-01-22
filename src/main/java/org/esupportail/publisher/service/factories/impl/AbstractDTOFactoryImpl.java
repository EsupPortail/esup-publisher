package org.esupportail.publisher.service.factories.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.AbstractEntity;
import org.esupportail.publisher.repository.AbstractRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.DTOFactory;
import org.esupportail.publisher.web.rest.dto.AbstractIdDTO;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 25 juil. 2014
 * @param <DTObject> DTO Object
 * @param <M> Model Object (JPA Entity)
 * @param <ID> ID type of model object.
 */
@Slf4j
public abstract class AbstractDTOFactoryImpl<DTObject extends AbstractIdDTO<ID>, M extends AbstractEntity<ID>, ID extends Serializable>
        implements DTOFactory<DTObject, M, ID> {

    private final transient Class<DTObject> dtObjectClass;

    protected final transient Class<M> mClass;

    /**
     * Constructor that initializes specific class instances for use by the
     * common base class methods.
     * @param dtObjectClass Data Transfer Object class for the associated model class
     * @param mClass The model class
     */
    public AbstractDTOFactoryImpl(final Class<DTObject> dtObjectClass,
            final Class<M> mClass) {
        this.dtObjectClass = dtObjectClass;
        this.mClass = mClass;
    }

    public abstract DTObject from(final M model);

    public List<DTObject> asDTOList(final Collection<M> models) {
        final List<DTObject> tos = Lists.newArrayList();
        if ((models != null) && !models.isEmpty()) {
            for (M model : models) {
                tos.add(from(model));
            }
        }

        return tos;
    }

    public Set<DTObject> asDTOSet(final Collection<M> models) {
        final Set<DTObject> dtos = Sets.newHashSet();
        if ((models != null) && !models.isEmpty()) {
            for (M model : models) {
                dtos.add(from(model));
            }
        }
        return dtos;
    }

    public Set<M> asSet(final Collection<DTObject> dtObjects)
            throws ObjectNotFoundException {
        final Set<M> models = Sets.newHashSet();
        if ((dtObjects != null) && !dtObjects.isEmpty()) {
            for (DTObject dtObject : dtObjects) {
                models.add(from(dtObject));
            }
        }
        return models;
    }

    protected DTObject newDTObject() {
        try {
            return dtObjectClass.newInstance();
        } catch (InstantiationException e) {
            log.error("unable to instantiate Data Transfer Object in factory.");
        } catch (IllegalAccessException e) {
            log.error("unable to instantiate Data Transfer Object in factory.");
        }
        return null;
    }

    protected M newModel() {
        try {
            return mClass.newInstance();
        } catch (InstantiationException e) {
            log.error("unable to instantiate Model object in factory.");
        } catch (IllegalAccessException e) {
            log.error("unable to instantiate Model object in factory.");
        }

        return null;
    }

    public M from(final DTObject dtObject) throws ObjectNotFoundException {
        log.debug("DTO To Model of {}", dtObject);
        if (dtObject.getModelId() == null) {
            return newModel();
        }
        M model;
        model = getDao().findOne(dtObject.getModelId());
        if (model == null) {
            throw new ObjectNotFoundException(
                    "id provided, but not valid: "
                            + dtObject.getModelId().toString(), mClass);
        }
        return model;
    }

    public M from(final ID id) throws ObjectNotFoundException {
        log.debug("find model of {} whith id {}", this.mClass, id);
        M model = getDao().findOne(id);
        if (model == null) throw new ObjectNotFoundException(id, this.mClass);
        return model;
    }

    /**
     * Gets the associated data-access layer instance
     *
     * @return The associated data-access layer instance
     */
    protected abstract AbstractRepository<M, ID> getDao();

}
