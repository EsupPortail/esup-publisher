package org.esupportail.publisher.service.factories.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.SubjectContextKey;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.repository.SubscriberRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.CompositeKeyDTOFactory;
import org.esupportail.publisher.service.factories.SubjectDTOFactory;
import org.esupportail.publisher.service.factories.SubscriberDTOFactory;
import org.esupportail.publisher.web.rest.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@Slf4j
public class SubscriberDTOFactoryImpl implements SubscriberDTOFactory {

	@Inject
	@Getter
	private transient SubscriberRepository dao;

	@Inject
    @Named("subjectKeyDTOFactoryImpl")
	private transient CompositeKeyDTOFactory<SubjectKeyDTO, SubjectKey, String, SubjectType> subjectConverter;
	@Inject
    @Named("contextKeyDTOFactoryImpl")
	private transient CompositeKeyDTOFactory<ContextKeyDTO, ContextKey, Long, ContextType> contextConverter;

    @Inject
    private SubjectDTOFactory subjectDTOFactory;

	public SubscriberDTOFactoryImpl() {
		super();
	}

	@Override
	public SubscriberDTO from(final Subscriber model) {
		log.debug("Model to DTO of {}", model);
		if (model != null) {
            return new SubscriberDTO( subjectConverter.convertToDTOKey(model
                .getSubjectCtxId().getSubject()),
					contextConverter.convertToDTOKey(model.getSubjectCtxId()
							.getContext()), model.getSubscribeType());
		}
		return null;
	}

	@Override
	public Subscriber from(final SubscriberDTO dtObject)
			throws ObjectNotFoundException {
		log.debug("DTO to Model of {}", dtObject);
		if (dtObject != null) {
			Subscriber model = dao.findOne(new SubjectContextKey(
					subjectConverter.convertToModelKey(dtObject.getModelId()
							.getSubjectKey()), contextConverter
							.convertToModelKey(dtObject.getModelId()
									.getContextKey())));
			if (model == null) {
				model = new Subscriber(
						subjectConverter.convertToModelKey(dtObject
								.getModelId().getSubjectKey()),
						contextConverter.convertToModelKey(dtObject
								.getModelId().getContextKey()),
						dtObject.getSubscribeType());
			} else {
				model.setSubscribeType(dtObject.getSubscribeType());
			}

			return model;
		}
		return null;
	}

	@Override
	public Subscriber from(final SubjectContextKeyDTO id)
			throws ObjectNotFoundException {
		return dao.findOne(new SubjectContextKey(subjectConverter
				.convertToModelKey(id.getSubjectKey()), contextConverter
				.convertToModelKey(id.getContextKey())));
	}

	@Override
	public List<SubscriberDTO> asDTOList(final Collection<Subscriber> models) {
		final List<SubscriberDTO> tos = Lists.newArrayList();

		if ((models != null) && !models.isEmpty()) {
			for (Subscriber model : models) {
				tos.add(from(model));
			}
		}

		return tos;
	}

	@Override
	public Set<SubscriberDTO> asDTOSet(final Collection<Subscriber> models) {
		final Set<SubscriberDTO> dtos = Sets.newHashSet();
		for (Subscriber model : models) {
			dtos.add(from(model));
		}
		return dtos;
	}

	@Override
	public Set<Subscriber> asSet(final Collection<SubscriberDTO> dtObjects)
			throws ObjectNotFoundException {
		final Set<Subscriber> models = Sets.newHashSet();
		for (SubscriberDTO dtObject : dtObjects) {
			models.add(from(dtObject));
		}
		return models;
	}

	@Override
	public SubjectContextKey convertToModelKey(SubjectContextKeyDTO dtoKey) {
		return new SubjectContextKey(subjectConverter.convertToModelKey(dtoKey
				.getSubjectKey()), contextConverter.convertToModelKey(dtoKey
				.getContextKey()));
	}

	@Override
	public SubjectContextKeyDTO convertToDTOKey(SubjectContextKey modelKey) {
		return new SubjectContextKeyDTO(
				subjectConverter.convertToDTOKey(modelKey.getSubject()),
				contextConverter.convertToDTOKey(modelKey.getContext()));
	}
}
