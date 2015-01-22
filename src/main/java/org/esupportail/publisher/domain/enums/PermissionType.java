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
 * @author GIP RECIA - Julien Gribonvald 14 juin 2012
 */
// We should persist these entries in a specific table.
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PermissionType {

	// Warning the order should be greater rights to lessers
	/** Admin. */
	ADMIN(1, "ADMIN", 128, "enum.permission.superAdm.title"),
	/** Manager. */
	MANAGER(2, "MANAGER", 64, "enum.permission.manager.title"),
	/** Editor. */
	EDITOR(3, "EDITOR", 32, "enum.permission.editor.title"),
	/** Contributor. */
	CONTRIBUTOR(4, "CONTRIBUTOR", 16, "enum.permission.contributor.title"),
	/** No Permission expect to look over the object and go on his childs. */
	LOOKOVER(5, "LOOKOVER", 0, "enum.permission.lookover.title");
	// ,
	// /** User. */
	// USER(2, 8, "permission.user.desc"),
	// /** Authenticated User And Without Permission. */
	// AUTHENTICATED(1, 4, "permission.authenticated.desc"),
	// /** UnAuthenticated Users */
	// ANONYMOUS(0, 0, "permission.anonymous.desc");

	/** Identifier. */
    @Getter
    @Setter
	private int id;
    @Getter
    @Setter
    private String name;
	/** Mask. */
    @Getter
    @Setter
	private int mask;
	/** The I18N key. */
    @Getter
    @Setter
	private String label;

	/**
	 * Contructor of the object Role.java.
	 *
	 * @param id
     * @param name
	 * @param mask
	 * @param label
	 */
	private PermissionType(final int id, final String name, final int mask, final String label) {
		this.id = id;
        this.name = name;
		this.mask = mask;
		this.label = label;
	}

    @JsonCreator
    public static PermissionType fromName(final String name) {
        if (name != null) {
            for (PermissionType val : PermissionType.values()) {
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
	 * @return Role
	 */
	public static PermissionType valueOf(final int id) {
		/*
		 * if (id == PermissionType.USER.getId()) { return PermissionType.USER;
		 * } else
		 */if (id == PermissionType.CONTRIBUTOR.getId()) {
			return PermissionType.CONTRIBUTOR;
		} else if (id == PermissionType.EDITOR.getId()) {
			return PermissionType.EDITOR;
		} else if (id == PermissionType.MANAGER.getId()) {
			return PermissionType.MANAGER;
		} else if (id == PermissionType.ADMIN.getId()) {
			return PermissionType.ADMIN;
			/*
			 * } else if (id == PermissionType.AUTHENTICATED.getId()) { return
			 * PermissionType.AUTHENTICATED; } else if (id ==
			 * PermissionType.ANONYMOUS.getId()) { return
			 * PermissionType.ANONYMOUS;
			 */
		} else
			return null;
	}

    @Override
    public String toString() {
        return this.name();
    }
}
