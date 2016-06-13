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

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald 16 juin 2014
 */
@Converter
public class StringListConverter implements AttributeConverter<Set, String> {

    @Override
    public String convertToDatabaseColumn(Set list) {
        return Joiner.on(',').join(list);
    }

    @Override
    public Set convertToEntityAttribute(String joined) {
        return Sets.newHashSet(Arrays.asList(joined.split(",")).iterator());
    }
}
