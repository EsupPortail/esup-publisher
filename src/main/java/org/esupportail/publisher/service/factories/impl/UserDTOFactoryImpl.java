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
/**
 *
 */
package org.esupportail.publisher.service.factories.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.esupportail.publisher.domain.SubjectContextKey;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.repository.externals.IExternalUserDao;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author GIP RECIA - Julien Gribonvald 25 juil. 2014
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class UserDTOFactoryImpl implements UserDTOFactory {

	@Inject
	@Getter
	private transient UserRepository dao;

	@Inject
	@Getter
	private transient IExternalUserDao extDao;

	public UserDTOFactoryImpl() {
		super();
	}

	@Override
	public UserDTO from(final User model) {
		log.debug("Model to DTO of {}", model);
		IExternalUser extUser = getExtDao().getUserByUid(model.getLogin());
		return from(model, extUser);
	}

	@Override
	public UserDTO from(final String id) {
		log.debug("from login Id to DTO of {}", id);
		IExternalUser extUser = getExtDao().getUserByUid(id);
		return from(extUser, true);
	}

	@Override
	public UserDTO from(final IExternalUser extModel, boolean withInternal) {
		log.debug("External to DTO of {}", extModel);
		User model = null;
		if (extModel != null && withInternal) {
			Optional<User> optionalUser = dao.findById(extModel.getId());
			model = optionalUser == null || !optionalUser.isPresent() ? null : optionalUser.get();
		}
		return from(model, extModel);
	}

	@Override
	public UserDTO from(final User model, final IExternalUser extModel) {
		if (model != null && extModel != null) {
			return new UserDTO(model.getLogin(), extModel.getDisplayName(),
					model.isEnabled(), model.isAcceptNotifications(),
					extModel.getEmail(), extModel.getAttributes());
		} else if (model != null) {
			return new UserDTO(model.getLogin(), model.getDisplayName(),
					model.isEnabled(), model.isAcceptNotifications());
		} else if (extModel != null) {
			return new UserDTO(extModel.getId(), extModel.getDisplayName(),
					extModel.getEmail(), extModel.getAttributes());
		}
		return null;
	}

	@Override
	public User from(final UserDTO dtObject) {
		log.debug("DTO to model of {}", dtObject);
		if (dtObject != null) {
			Optional<User> optionalUser = getDao().findById(dtObject.getLogin());
			User user = optionalUser == null || !optionalUser.isPresent() ? null : optionalUser.get();
			if (user == null) {
				user = new User(dtObject.getLogin(), dtObject.getDisplayName());
				// set UID from LDAP UserDTO
			}
			user.setAcceptNotifications(dtObject.isAcceptNotifications());
			user.setEnabled(dtObject.isEnabled());
			user.setEmail(dtObject.getEmail());
			user.setLangKey(dtObject.getLangKey());

			return user;
		}
		return null;
	}

	public List<UserDTO> asDTOList(final Collection<User> models) {
		final List<UserDTO> tos = Lists.newLinkedList();

		if ((models != null) && !models.isEmpty()) {
			for (User model : models) {
				tos.add(from(model));
			}
		}

		return tos;
	}

	@Override
	public List<UserDTO> asDTOList(final Collection<IExternalUser> models,
			boolean withInternal) {
		final List<UserDTO> tos = Lists.newLinkedList();

		if ((models != null) && !models.isEmpty()) {
			for (IExternalUser model : models) {
				tos.add(from(model, withInternal));
			}
		}

		return tos;
	}

	public Set<UserDTO> asDTOSet(final Collection<User> models) {
		final Set<UserDTO> dtos = Sets.newLinkedHashSet();
		for (User model : models) {
			dtos.add(from(model));
		}
		return dtos;
	}

	@Override
	public Set<UserDTO> asDTOSet(final Collection<IExternalUser> models,
			boolean withInternal) {
		final Set<UserDTO> tos = Sets.newLinkedHashSet();

		if ((models != null) && !models.isEmpty()) {
			for (IExternalUser model : models) {
				tos.add(from(model, withInternal));
			}
		}

		return tos;
	}

	public Set<User> asSet(final Collection<UserDTO> dtObjects) {
		final Set<User> models = Sets.newLinkedHashSet();
		for (UserDTO dtObject : dtObjects) {
			models.add(from(dtObject));
		}
		return models;
	}

	// @Override
	// public User from(final String id) {
	// return getDao().findOne(id);
	// }

}
