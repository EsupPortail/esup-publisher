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
import lombok.Getter;
import lombok.Setter;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 26 juin 2012
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ItemStatus {
    /** Status of Item is Pending. */
    PENDING(0, "PENDING", "enum.itemStatus.pending.title"),
    /** Status of Item is Scheduled. */
    SCHEDULED(1, "SCHEDULED", "enum.itemStatus.scheduled.title"),
    /** Status of Item is Published. */
    PUBLISHED(2, "PUBLISHED", "enum.itemStatus.published.title"),
    /** Status of Item is Archived. */
    ARCHIVED(3, "ARCHIVED", "enum.itemStatus.archived.title"),
    /** Status of Item is DRAFT. */
    DRAFT(4,"DRAFT", "enum.itemStatus.draft.title");

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

    /**
     * Contructor of the object ItemStatus.java.
     * @param id
     * @param name
     * @param label
     */
    private ItemStatus(final int id, final String name, final String label) {
        this.id = id;
        this.name = name;
        this.label = label;
    }

    @JsonCreator
    public static ItemStatus fromName(final String name) {
        if (name != null) {
            for (ItemStatus type : ItemStatus.values()) {
                if (name.equalsIgnoreCase(type.toString())) {
                    return type;
                }
            }
        }
        return null;
    }

    /**
     * Retrive the Enum Value from identifier.
     * @param id
     * @return ItemStatus
     */
    public static ItemStatus valueOf(final int id){
        if (id == ItemStatus.ARCHIVED.getId()) {
            return ItemStatus.ARCHIVED;
        } else if (id == ItemStatus.PENDING.getId()) {
            return ItemStatus.PENDING;
        } else if (id == ItemStatus.PUBLISHED.getId()) {
            return ItemStatus.PUBLISHED;
        } else if (id == ItemStatus.SCHEDULED.getId()) {
            return ItemStatus.SCHEDULED;
        } else if (id == ItemStatus.DRAFT.getId()) {
            return ItemStatus.DRAFT;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
