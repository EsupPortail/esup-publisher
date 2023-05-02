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

import java.util.List;

import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.QPublisher;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

/**
 * @author GIP RECIA - Julien Gribonvald 22 juil. 2014
 */
public final class PublisherPredicates {

	private static final QPublisher qobj = QPublisher.publisher;

	public static Predicate AllOfOrganization(Organization org) {
		return AllOfOrganization(org.getId());
	}
    public static Predicate AllOfOrganization(long orgId) {
        return qobj.context.organization.id.eq(orgId);
    }
    public static Predicate AllOfOrganizationByIdentifier(String orgIdentifier) {
        return qobj.context.organization.identifiers.contains(orgIdentifier);
    }

    public static Predicate AllFromIds(List<Long> publisherIds) {
        return qobj.id.in(publisherIds);
    }

    public static Predicate notNull() {
        return qobj.isNotNull();
    }

    public static Predicate AllOfUsedState(boolean used) {
        if (used) {
            return qobj.used.isTrue();
        }
        return qobj.used.isFalse();
    }

    public static Predicate AllOfReader(long readerId) {
        return qobj.context.reader.id.eq(readerId);
    }

    public static Predicate AllOfRedactor(long redactorId) {
        return qobj.context.redactor.id.eq(redactorId);
    }

    public static Predicate AllUsedInOrganizationWithReader(long orgId, long readerId) {
        BooleanBuilder builder = new BooleanBuilder(AllOfOrganization(orgId));
        builder.and(AllOfUsedState(true));
        builder.and(AllOfReader(readerId));
        return builder;
    }

    public static Predicate AllUsedInOrganizationWithReaderAndRedactor(long orgId, long readerId, long redactorId) {
        BooleanBuilder builder = new BooleanBuilder(AllUsedInOrganizationWithReader(orgId, readerId));
        builder.and(AllOfRedactor(redactorId));
        return builder;
    }


    public static OrderSpecifier<?> orderByOrganizations() {
        return qobj.context.organization.displayOrder.asc();
    }
    public static OrderSpecifier<?> orderByDisplayOrder() {
        return qobj.displayOrder.asc();
    }

}
