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

import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.QOrganization;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

/**
 * @author GIP RECIA - Julien Gribonvald 9 juil. 2014
 */
public final class OrganizationPredicates {

	public static Predicate sameName(final Organization org) {
		final QOrganization qorg = QOrganization.organization;
		final BooleanExpression orgExistName = qorg.name.eq(org.getName());
		final BooleanExpression orgExistId = qorg.id.eq(org.getId());
		if (org.getId() > 0) {
			return orgExistName.and(orgExistId.not());
		}
		return orgExistName;
	}

    public static Predicate notNull() {
        return QOrganization.organization.isNotNull();
    }

	public static OrderSpecifier<?>[] orderBy() {
		final QOrganization qorg = QOrganization.organization;
		return new OrderSpecifier<?>[]{qorg.displayOrder.asc(), qorg.displayName.asc()};
	}

}
