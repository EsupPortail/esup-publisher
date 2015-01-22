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
 * @author GIP RECIA - Julien Gribonvald 20 juin 2012
 */
// We should persist these entries in a specific table.
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SubscribeType {
	/** Subcription Obliged. */
	FORCED(0, "FORCED", "enum.subscribe.forced.title"),
	/** Subscription Free. */
	FREE(1, "FREE", "enum.subscribe.free.title"),
	/** Subscription FREE but Pre-registered. */
	PRE(2, "PRE", "enum.subscribe.pre.title");

    @Getter
    @Setter
	private int id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String label;

    private SubscribeType(int id, String name, String label) {
        this.id = id;
        this.name = name;
        this.label = label;
    }

    @JsonCreator
    public static SubscribeType fromName(final String name) {
        if (name != null) {
            for (SubscribeType val : SubscribeType.values()) {
                if (name.equalsIgnoreCase(val.toString())) {
                    return val;
                }
            }
        }
        return null;
    }

    public static SubscribeType valueOf(int id) {
		if (id == SubscribeType.FORCED.getId()) {
			return SubscribeType.FORCED;
		} else if (id == SubscribeType.FREE.getId()) {
			return SubscribeType.FREE;
		} else if (id == SubscribeType.PRE.getId()) {
			return SubscribeType.PRE;
		}
		return null;
	}

    @Override
    public String toString() {
        return this.name;
    }
}
