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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.esupportail.publisher.domain.enums.SubscribeType;
import org.esupportail.publisher.domain.util.CustomEnumSerializer;


/**
 * Created by jgribonvald on 19/06/15.
 */
@Data
@NoArgsConstructor
public class SubscriberFormDTO {

	private SubjectDTO subject;

	private SubjectKeyExtendedDTO extendedSubject;

	@JsonSerialize(using = CustomEnumSerializer.class)
	private SubscribeType subscribeType;

	/**
	 * @param subject
	 * @param subscribeType
	 */
	public SubscriberFormDTO(final SubjectDTO subject, final SubscribeType subscribeType) {
		super();
		this.subject = subject;
		this.subscribeType = subscribeType;
	}

	/**
	 * @param extendedSubject
	 * @param subscribeType
	 */
	public SubscriberFormDTO(final SubjectKeyExtendedDTO extendedSubject, final SubscribeType subscribeType) {
		super();
		this.extendedSubject = extendedSubject;
		this.subscribeType = subscribeType;
	}

}
