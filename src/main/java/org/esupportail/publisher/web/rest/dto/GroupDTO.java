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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.SubjectType;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class GroupDTO extends SubjectDTO {

    private boolean hasChilds;

	private Map<String, List<String>> attributes;

	public GroupDTO(@NotNull final String groupId, final String displayName, final boolean foundOnExternalSource) {
        super(new SubjectKeyDTO(groupId, SubjectType.GROUP), displayName, foundOnExternalSource);
	}

	/**
	 * Constructor à utiliser lors de la convertion d'un objet ExternalSource.
	 */
	public GroupDTO(@NotNull final String groupId, final String displayName,
                    final boolean hasChilds, final Map<String, List<String>> attributes) {
        super(new SubjectKeyDTO(groupId, SubjectType.GROUP), displayName, displayName != null);
        this.hasChilds = hasChilds;
        this.attributes = attributes;
	}
}
