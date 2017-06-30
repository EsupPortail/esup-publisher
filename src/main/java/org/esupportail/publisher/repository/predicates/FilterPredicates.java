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
package org.esupportail.publisher.repository.predicates;

import com.mysema.query.types.Predicate;
import org.esupportail.publisher.domain.QFilter;
import org.esupportail.publisher.domain.QOrganization;
import org.esupportail.publisher.domain.enums.FilterType;

/**
 * @author GIP RECIA - Julien Gribonvald 10 juin 2015
 */
public final class FilterPredicates {

	public static Predicate ofType(final FilterType type) {
		final QFilter qfilter = QFilter.filter;
		return qfilter.type.eq(type);
	}

    public static Predicate notNull() {
        return QOrganization.organization.isNotNull();
    }

	public static Predicate ofOrganization(final long orgId) {
		final QFilter qfilter = QFilter.filter;
        return qfilter.organization.id.eq(orgId);
	}

    public static Predicate ofTypeOfOrganization(final long orgId, final FilterType type) {
		final QFilter qfilter = QFilter.filter;
		return qfilter.organization.id.eq(orgId).and(qfilter.type.eq(type));
	}

}
