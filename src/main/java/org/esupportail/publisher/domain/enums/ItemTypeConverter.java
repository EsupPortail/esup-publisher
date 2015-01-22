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

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald 16 juin 2014
 */
@Slf4j
@Converter
public class ItemTypeConverter implements AttributeConverter<Set<ItemType>, String> {

    @Override
    public String convertToDatabaseColumn(Set<ItemType> attribute) {
        if (attribute != null && !attribute.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (ItemType type : attribute) {
                if (i > 0) sb.append(",");
                sb.append(type.name());
                i++;
            }
            return sb.toString();
        }
        return null;
    }

    @Override
    public Set<ItemType> convertToEntityAttribute(String dbData) {
        Set<String> types = Sets.newHashSet();
        if (dbData != null) {
            types = Sets.newHashSet(dbData.split(","));
        }
        Set<ItemType> set = Sets.newHashSet();
        for (String str : types) {
            try {
                ItemType type = ItemType.valueOf(str);
                set.add(type);
            } catch (IllegalArgumentException iae) {
                log.error("Unknown ItemType : " + str, iae.getMessage());
            }
        }
        return set;
    }
}
