/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.service.factories.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.SubjectContextKey;
import org.esupportail.publisher.domain.SubjectKeyExtended;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.repository.SubscriberRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.CompositeKeyDTOFactory;
import org.esupportail.publisher.service.factories.CompositeKeyExtendedDTOFactory;
import org.esupportail.publisher.service.factories.SubjectDTOFactory;
import org.esupportail.publisher.service.factories.SubscriberDTOFactory;
import org.esupportail.publisher.web.rest.dto.ContextKeyDTO;
import org.esupportail.publisher.web.rest.dto.SubjectContextKeyDTO;
import org.esupportail.publisher.web.rest.dto.SubjectKeyExtendedDTO;
import org.esupportail.publisher.web.rest.dto.SubscriberDTO;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class SubscriberDTOFactoryImpl implements SubscriberDTOFactory {

	@Inject
	@Getter
	private transient SubscriberRepository dao;

	@Inject
	private transient CompositeKeyExtendedDTOFactory<SubjectKeyExtendedDTO, SubjectKeyExtended, String, String, SubjectType> subjectConverter;
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
			return new SubscriberDTO(subjectConverter.convertToDTOKey(model.getSubjectCtxId().getSubject()),
					contextConverter.convertToDTOKey(model.getSubjectCtxId().getContext()), model.getSubscribeType());
		}
		return null;
	}

	@Override
	public Subscriber from(final SubscriberDTO dtObject) throws ObjectNotFoundException {
		log.debug("DTO to Model of {}", dtObject);
		if (dtObject != null) {
			Optional<Subscriber> optionalSubscriber = dao.findById(new SubjectContextKey(subjectConverter.convertToModelKey(dtObject
					.getModelId().getSubjectKey()), contextConverter.convertToModelKey(dtObject.getModelId()
					.getContextKey())));
			Subscriber model = optionalSubscriber.orElse(null);
			if (model == null) {
				model = new Subscriber(subjectConverter.convertToModelKey(dtObject.getModelId().getSubjectKey()),
						contextConverter.convertToModelKey(dtObject.getModelId().getContextKey()),
						dtObject.getSubscribeType());
			} else {
				model.setSubscribeType(dtObject.getSubscribeType());
			}

			return model;
		}
		return null;
	}

	@Override
	public Subscriber from(final SubjectContextKeyDTO id) throws ObjectNotFoundException {
		Optional<Subscriber> optionalSubscriber = dao.findById(new SubjectContextKey(subjectConverter.convertToModelKey(id.getSubjectKey()),
				contextConverter.convertToModelKey(id.getContextKey())));
		Subscriber model = optionalSubscriber.orElse(null);
		return model;
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
	public Set<Subscriber> asSet(final Collection<SubscriberDTO> dtObjects) throws ObjectNotFoundException {
		final Set<Subscriber> models = Sets.newHashSet();
		for (SubscriberDTO dtObject : dtObjects) {
			models.add(from(dtObject));
		}
		return models;
	}

	@Override
	public SubjectContextKey convertToModelKey(SubjectContextKeyDTO dtoKey) {
		return new SubjectContextKey(subjectConverter.convertToModelKey(dtoKey.getSubjectKey()),
				contextConverter.convertToModelKey(dtoKey.getContextKey()));
	}

	@Override
	public SubjectContextKeyDTO convertToDTOKey(SubjectContextKey modelKey) {
		return new SubjectContextKeyDTO(subjectConverter.convertToDTOKey(modelKey.getSubject()),
				contextConverter.convertToDTOKey(modelKey.getContext()));
	}
}