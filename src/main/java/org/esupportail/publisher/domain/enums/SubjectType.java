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
package org.esupportail.publisher.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GIP RECIA - Julien Gribonvald 14 juin 2014
 */
// We should persist these entries in a specific table.
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SubjectType {

	/** Type of userid. */
	PERSON(0, "PERSON", "enum.subject.person.title"),
	/** Type of group. */
	GROUP(1, "GROUP", "enum.subject.group.title");

	/** Identifier. */
    @Getter
    @Setter
	private int id;
	/** The code. */
    @Getter
    @Setter
	private String code;
	/** The I18N key. */
    @Getter
    @Setter
	private String descKey;

	/**
	 * Contructor of the object SubjectType.java.
	 *
	 * @param id
	 * @param code
	 * @param descKey
	 */
	private SubjectType(final int id, final String code, final String descKey) {
		this.id = id;
		this.code = code;
		this.descKey = descKey;
	}

    @JsonCreator
    public static SubjectType fromName(final String name) {
        if (name != null) {
            for (SubjectType val : SubjectType.values()) {
                if (name.equalsIgnoreCase(val.toString())) {
                    return val;
                }
            }
        }
        return null;
    }

	/**
	 * Retrive the Enum Value from identifier.
	 *
	 * @param id
	 * @return SubjectType
	 */
	public static SubjectType valueOf(final int id) {
		if (id == SubjectType.PERSON.getId()) {
			return SubjectType.PERSON;
		} else if (id == SubjectType.GROUP.getId()) {
			return SubjectType.GROUP;
		}
		return null;
	}

    @Override
    public String toString() {
        return this.code;
    }
}
