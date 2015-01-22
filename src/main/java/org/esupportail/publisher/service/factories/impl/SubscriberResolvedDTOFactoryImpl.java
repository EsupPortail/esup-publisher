package org.esupportail.publisher.service.factories.impl;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.service.factories.CompositeKeyDTOFactory;
import org.esupportail.publisher.service.factories.SubjectDTOSimpleFactory;
import org.esupportail.publisher.service.factories.SubscriberResolvedDTOFactory;
import org.esupportail.publisher.web.rest.dto.ContextKeyDTO;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;
import org.esupportail.publisher.web.rest.dto.SubscriberResolvedDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly=true)
@Slf4j
public class SubscriberResolvedDTOFactoryImpl implements SubscriberResolvedDTOFactory {

    @Inject
    private SubjectDTOSimpleFactory subjectDTOFactory;
    @Inject
    @Named("contextKeyDTOFactoryImpl")
    private transient CompositeKeyDTOFactory<ContextKeyDTO, ContextKey, Long, ContextType> contextConverter;

    @Override
    public SubscriberResolvedDTO from(@NotNull Subscriber model) {
        SubjectDTO subjectDTO = subjectDTOFactory.from(model.getId().getSubject());

        return new SubscriberResolvedDTO(subjectDTO,
            contextConverter.convertToDTOKey(model.getId().getContext()), model.getSubscribeType());
    }

    @Override
    public List<SubscriberResolvedDTO> asDTOList(@NotNull List<Subscriber> models) {
        List<SubscriberResolvedDTO> list = new ArrayList<>();
        for (Subscriber subscriber: models) {
            list.add(this.from(subscriber));
        }
        return list;
    }
}
