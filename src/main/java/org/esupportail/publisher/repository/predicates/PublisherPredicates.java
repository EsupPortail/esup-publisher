package org.esupportail.publisher.repository.predicates;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.OrderSpecifier;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.QPublisher;

import com.mysema.query.types.Predicate;

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
