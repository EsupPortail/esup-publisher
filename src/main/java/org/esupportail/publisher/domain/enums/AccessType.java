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
package org.esupportail.publisher.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * @author GIP RECIA - Julien Gribonvald 1 juil. 2014
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonPropertyOrder({ "id", "name", "label" })
@XmlType
public enum AccessType {

	/** No checked access, free access. */
    @XmlEnumValue("public")
	PUBLIC(1, "PUBLIC", "enum.access.public.title"),
	/** Check access : if the user is authenticated. */
    @XmlEnumValue("cas")
	AUTHENTICATED(2, "AUTHENTICATED", "enum.access.authenticated.title"),
	/**
	 * Check access : if the user is authenticated and if he is in the list of
	 * authorized.
	 */
    @XmlEnumValue("cas")
	AUTHORIZED(3, "AUTHORIZED", "enum.access.authorized.title");

    /** Identifier. */
    @Getter
    @Setter
    private int id;
    /** Name of Status. */
    @Getter
    @Setter
    private String name;
    /** Label for I18N. */
    @Getter
    @Setter
    private String label;

    private AccessType(final int id, final String name, final String label) {
        this.id = id;
        this.name = name;
        this.label = label;
    }

    @JsonCreator
    public static AccessType fromName(final String name) {
        if (name != null) {
            for (AccessType type : AccessType.values()) {
                if (name.equalsIgnoreCase(type.toString())) {
                    return type;
                }
            }
        }
        return null;
    }

    public static AccessType valueOf(final int id) {
		if (id == AccessType.PUBLIC.getId()) {
			return AccessType.PUBLIC;
		} else if (id == AccessType.AUTHENTICATED.getId()) {
			return AccessType.AUTHENTICATED;
		} else if (id == AccessType.AUTHORIZED.getId()) {
			return AccessType.AUTHORIZED;
		}
		return null;
	}

    @Override
    public String toString() {
        return this.name;
    }
}
