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
package org.esupportail.publisher.repository.predicates;

import com.querydsl.core.types.Predicate;
import org.esupportail.publisher.domain.QReadingIndicator;
import org.esupportail.publisher.web.rest.dto.UserDTO;

public class ReadingIndicatorPredicates {

    public static Predicate readingIndicationOfUser(final UserDTO userDto) {
        final QReadingIndicator qobj = QReadingIndicator.readingIndicator;
        return qobj.user.eq(userDto.getLogin());
    }

    public static Predicate readingIndicationOfItem(final long id) {
        final QReadingIndicator qobj = QReadingIndicator.readingIndicator;
        return qobj.item.id.eq(id);
    }

    public static Predicate readingIndicationOfItemAndUser(final long id, final UserDTO userDto) {
        final QReadingIndicator qobj = QReadingIndicator.readingIndicator;
        return qobj.item.id.eq(id).and(qobj.user.eq(userDto.getLogin()));
    }
}
