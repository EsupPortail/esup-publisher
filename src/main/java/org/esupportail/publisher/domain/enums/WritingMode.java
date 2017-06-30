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
 * Created by jgribonvald on 20/05/15.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum WritingMode {

    STATIC(1,"STATIC", "enum.writingMode.static.title"),
    TARGETS_ON_ITEM(2, "TARGETS_ON_ITEM", "enum.writingMode.targetsOnItem.title");

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

    private WritingMode(final int id, final String name, final String label) {
        this.id = id;
        this.name = name;
        this.label = label;
    }

    @JsonCreator
    public static WritingMode fromName(final String name) {
        if (name != null) {
            for (WritingMode type : WritingMode.values()) {
                if (name.equalsIgnoreCase(type.toString())) {
                    return type;
                }
            }
        }
        return null;
    }

    public static WritingMode valueOf(final int id) {
        if (id == WritingMode.STATIC.getId()) {
            return WritingMode.STATIC;
        } else if (id == WritingMode.TARGETS_ON_ITEM.getId()) {
            return WritingMode.TARGETS_ON_ITEM;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
