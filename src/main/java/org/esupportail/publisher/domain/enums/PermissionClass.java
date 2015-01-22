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
import org.esupportail.publisher.domain.*;

/**
 * @author GIP RECIA - Julien Gribonvald 14 juin 2012
 */
// We should persist these entries in a specific table.
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PermissionClass {

	CONTEXT(1, "CONTEXT", PermissionOnContext.class, "enum.permissionclass.context.title"),
	CONTEXT_WITH_SUBJECTS(2, "CONTEXT_WITH_SUBJECTS", PermissionOnClassificationWithSubjectList.class, "enum.permissionclass.contextwithsubjects.title"),
	SUBJECT(3, "SUBJECT", PermissionOnSubjects.class, "enum.permissionclass.subject.title"),
	SUBJECT_WITH_CONTEXT(4, "SUBJECT_WITH_CONTEXT", PermissionOnSubjectsWithClassificationList.class, "enum.permissionclass.subjectwithclassif.title");

	/** Identifier. */
	@Getter
	@Setter
	private int id;
    /** Name. */
    @Getter
    @Setter
    private String name;
	/** class. */
	@Getter
	@Setter
	private Class<? extends AbstractPermission> type;
	/** The I18N key. */
	@Getter
	@Setter
	private String label;

    private PermissionClass(final int id, final String name, final Class<? extends AbstractPermission> type, final String label) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.label = label;
    }

    @JsonCreator
    public static PermissionClass fromName(final String name) {
        if (name != null) {
            for (PermissionClass val : PermissionClass.values()) {
                if (name.equalsIgnoreCase(val.toString())) {
                    return val;
                }
            }
        }
        return null;
    }

    /**
	 * Retrieve the Enum Value from identifier.
	 *
	 * @param id
	 * @return PermissionClass
	 */
	public static PermissionClass valueOf(final int id) {
		if (id == PermissionClass.CONTEXT.getId()) {
			return PermissionClass.CONTEXT;
		} else if (id == PermissionClass.CONTEXT_WITH_SUBJECTS.getId()) {
			return PermissionClass.CONTEXT_WITH_SUBJECTS;
		} else if (id == PermissionClass.SUBJECT.getId()) {
			return PermissionClass.SUBJECT;
		} else if (id == PermissionClass.SUBJECT_WITH_CONTEXT.getId()) {
			return PermissionClass.SUBJECT_WITH_CONTEXT;
		} else
			return null;
	}

    /**
     * Retrieve the Enum Value from identifier.
     *
     * @param type
     * @return PermissionClass
     */
    public static PermissionClass valueOf(final Class<? extends AbstractPermission> type) {
        if (type.isInstance(PermissionClass.CONTEXT.getType())) {
            return PermissionClass.CONTEXT;
        } else if (type.isInstance(PermissionClass.CONTEXT_WITH_SUBJECTS.getType())) {
            return PermissionClass.CONTEXT_WITH_SUBJECTS;
        } else if (type.isInstance(PermissionClass.SUBJECT.getType())) {
            return PermissionClass.SUBJECT;
        } else if (type.isInstance(PermissionClass.SUBJECT_WITH_CONTEXT.getType())) {
            return PermissionClass.SUBJECT_WITH_CONTEXT;
        } else
            return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
