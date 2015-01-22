/**
 *
 */
package org.esupportail.publisher.service.factories.impl;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.service.factories.CompositeKeyDTOFactory;
import org.esupportail.publisher.service.factories.GroupDTOFactory;
import org.esupportail.publisher.service.factories.SubjectDTOSimpleFactory;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;
import org.esupportail.publisher.web.rest.dto.SubjectKeyDTO;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.List;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 25 juil. 2014
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class SubjectDTOSimpleFactoryImpl implements SubjectDTOSimpleFactory {

    @Inject
    @Getter
    private transient UserDTOFactory userDTOFactory;

    @Inject
    @Getter
    private transient GroupDTOFactory groupDTOFactory;

    @Inject
    @Named("subjectKeyDTOFactoryImpl")
    private transient CompositeKeyDTOFactory<SubjectKeyDTO, SubjectKey, String, SubjectType> subjectConverter;

    public SubjectDTOSimpleFactoryImpl() {
        super();
    }

    public SubjectDTO from(final SubjectKey modelKey) {
        log.debug("Model to DTO of {}", modelKey);
        if (SubjectType.PERSON.equals(modelKey.getKeyType())) {
            UserDTO user = userDTOFactory.from(modelKey.getKeyId());
            if (user == null) {
                return new SubjectDTO(subjectConverter.convertToDTOKey(modelKey), null, false);
            }
            return new SubjectDTO(subjectConverter.convertToDTOKey(modelKey), user.isFoundOnExternalSource() ? user.getDisplayName() : null, user.isFoundOnExternalSource());
        } else if (SubjectType.GROUP.equals(modelKey.getKeyType())) {
            return groupDTOFactory.liteFrom(modelKey.getKeyId());
        }
        return new SubjectDTO(subjectConverter.convertToDTOKey(modelKey), null, false);
    }

    public List<SubjectDTO> asDTOList(final Collection<SubjectKey> models) {
        final List<SubjectDTO> tos = Lists.newArrayList();

        if ((models != null) && !models.isEmpty()) {
            for (SubjectKey model : models) {
                tos.add(from(model));
            }
        }

        return tos;
    }

}
