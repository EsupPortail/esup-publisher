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
 * 30 juin 2014
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DisplayOrderType {
    LAST_CREATED_MODIFIED_FIRST(0, "LAST_CREATED_MODIFIED_FIRST", "enum.displayOrder.lastCreatedModifiedFirst.title"),
    ONLY_LAST_CREATED_FIRST(1, "ONLY_LAST_CREATED_FIRST", "enum.displayOrder.onlyLastCreatedFirst.title"),
    NAME(2, "NAME", "enum.displayOrder.byName.title"),
    START_DATE(3, "START_DATE", "enum.displayOrder.byStartDate.title"),
    /** using int weigth. Hight number will place it first */
    CUSTOM(4, "CUSTOM", "enum.displayOrder.custom.title");

    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String label;

    private DisplayOrderType(final int id, final String name, final String label) {
        this.id = id;
        this.label =label;
        this.name = name;
    }

    @JsonCreator
    public static DisplayOrderType fromName(final String name) {
        if (name != null) {
            for (DisplayOrderType type : DisplayOrderType.values()) {
                if (name.equalsIgnoreCase(type.toString())) {
                    return type;
                }
            }
        }
        return null;
    }

    /**
     * @param id
     * @return
     */
    public static DisplayOrderType valueOf(final int id){
        if (id == DisplayOrderType.LAST_CREATED_MODIFIED_FIRST.getId()) {
            return DisplayOrderType.LAST_CREATED_MODIFIED_FIRST;
        } else if (id == DisplayOrderType.ONLY_LAST_CREATED_FIRST.getId()) {
            return DisplayOrderType.ONLY_LAST_CREATED_FIRST;
        } else if (id == DisplayOrderType.NAME.getId()) {
            return DisplayOrderType.NAME;
        } else if (id == DisplayOrderType.START_DATE.getId()) {
            return DisplayOrderType.START_DATE;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
