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
package org.esupportail.publisher.web.rest.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.util.CstPropertiesLength;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

@ToString
public class UserDTO extends SubjectDTO {

	@Getter
	private String login;
	@Getter
	private boolean enabled = true;
	@Getter
	private boolean acceptNotifications;
	@Getter
	private String email;
	@Getter
	@Setter
	@Size(min = 2, max = CstPropertiesLength.LANG)
	private String langKey;
	@Getter
	private Map<String, List<String>> attributes;
	@Getter
	private boolean foundOnInternalSource;

	// private List<String> roles;

	/**
	 * Constructor à utiliser lors de la convertion d'un objet JPA.
	 *
	 * @param userId
	 * @param enabled
	 * @param displayName
	 * @param acceptNotifications
	 */
	public UserDTO(@NotNull final String userId,
			@NotNull final String displayName, final boolean enabled,
			final boolean acceptNotifications) {
        super(new SubjectKeyDTO(userId, SubjectType.PERSON), displayName, true);
		this.login = userId;
		this.enabled = enabled;
		this.acceptNotifications = acceptNotifications;
	}

	/**
	 * Constructor à utiliser lors de la convertion d'un objet ExternalSource
	 * enregistré dans le ldap par exemple.
	 *
	 * @param userId
	 * @param displayName
	 * @param email
	 * @param attributes
	 */
	public UserDTO(@NotNull final String userId,
			@NotNull final String displayName, String email,
			Map<String, List<String>> attributes) {
        super(new SubjectKeyDTO(userId, SubjectType.PERSON), displayName, true);
		this.login = userId;
		this.acceptNotifications = true;
		this.email = email;
		this.attributes = attributes;
		this.enabled = true;
	}

	/**
	 * Constructor à utiliser lors de la convertion d'un objet JPA enregistré en
	 * BD et en même temps de la convertion d'un objet ExternalSource.
	 *
	 * @param userId
	 * @param displayName
	 * @param enabled
	 * @param acceptNotifications
	 * @param email
	 * @param attributes
	 */
	public UserDTO(@NotNull final String userId,
			@NotNull final String displayName, final boolean enabled,
			final boolean acceptNotifications, final String email,
			@NotNull final Map<String, List<String>> attributes) {
        super(new SubjectKeyDTO(userId, SubjectType.PERSON), displayName, true);
		this.login = userId;
		this.enabled = enabled;
		this.acceptNotifications = acceptNotifications;
		this.email = email;
		this.attributes = attributes;
		this.foundOnInternalSource = true;
	}
}
