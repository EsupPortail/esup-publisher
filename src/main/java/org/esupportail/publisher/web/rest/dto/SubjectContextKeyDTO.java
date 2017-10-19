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

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 15 oct. 2014
 */
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class SubjectContextKeyDTO implements Serializable {

	/** */
	private static final long serialVersionUID = -3963584394811440859L;

	@NotNull
	@NonNull
	@Getter
	@Setter
	private SubjectKeyExtendedDTO subjectKey;

	@NotNull
	@NonNull
	@Getter
	@Setter
	private ContextKeyDTO contextKey;

	/**
	 * @param subjectKey
	 * @param contextKey
	 */
	public SubjectContextKeyDTO(@NotNull final SubjectKeyExtendedDTO subjectKey, @NotNull final ContextKeyDTO contextKey) {
		super();
		this.subjectKey = subjectKey;
		this.contextKey = contextKey;
	}

	public SubjectContextKeyDTO(@NotNull final SubjectDTO subject, @NotNull final ContextKeyDTO contextKey) {
		super();
		this.subjectKey = new SubjectKeyExtendedDTO(subject.getModelId().getKeyId(), subject.getModelId().getKeyType());
		this.contextKey = contextKey;
	}

}
