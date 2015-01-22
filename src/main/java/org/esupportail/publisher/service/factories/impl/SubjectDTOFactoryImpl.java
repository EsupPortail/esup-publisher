/**
 *
 */
package org.esupportail.publisher.service.factories.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.CompositeKeyDTOFactory;
import org.esupportail.publisher.service.factories.SubjectDTOFactory;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;
import org.esupportail.publisher.web.rest.dto.SubjectKeyDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 25 juil. 2014
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class SubjectDTOFactoryImpl implements SubjectDTOFactory {

    @Inject
    @Getter
    private transient UserRepository dao;

    @Inject
    private transient CompositeKeyDTOFactory<SubjectKeyDTO, SubjectKey, String, SubjectType> subjectConverter;

    public SubjectDTOFactoryImpl() {
        super();
    }

    public SubjectDTO from(final User model) {
        log.debug("Model to DTO of {}", model);
        return new SubjectDTO(subjectConverter.convertToDTOKey(model.getSubject()), model.getDisplayName(), true);
    }

    public User from(final SubjectDTO dtObject) throws ObjectNotFoundException {
        log.debug("DTO to Model of {}", dtObject);
        if (dtObject != null && SubjectType.PERSON.equals(dtObject.getModelId().getKeyType())) {
            User user = getDao().findOne(subjectConverter.convertToModelKey(dtObject.getModelId()).getKeyId());
            if (user == null) {
                user = new User(subjectConverter.convertToModelKey(dtObject.getModelId()).getKeyId(), dtObject.getDisplayName());
            }

            return user;
        }
        return null;
    }

    public List<SubjectDTO> asDTOList(final Collection<User> models) {
        final List<SubjectDTO> tos = Lists.newArrayList();

        if ((models != null) && !models.isEmpty()) {
            for (User model : models) {
                tos.add(from(model));
            }
        }

        return tos;
    }

    public Set<SubjectDTO> asDTOSet(final Collection<User> models) {
        final Set<SubjectDTO> dtos = Sets.newHashSet();
        for (User model : models) {
            dtos.add(from(model));
        }
        return dtos;
    }

    public Set<User> asSet(final Collection<SubjectDTO> dtObjects)
            throws ObjectNotFoundException {
        final Set<User> models = Sets.newHashSet();
        for (SubjectDTO dtObject : dtObjects) {
            models.add(from(dtObject));
        }
        return models;
    }

    public User from(final SubjectKeyDTO id) throws ObjectNotFoundException {
        if (SubjectType.PERSON.equals(id.getKeyType()))
            return getDao().findOne(subjectConverter.convertToModelKey(id).getKeyId());
        return null;
    }

}
