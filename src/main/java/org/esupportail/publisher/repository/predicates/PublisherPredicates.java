package org.esupportail.publisher.repository.predicates;

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
		return qobj.context.organization.eq(org);
	}
    public static Predicate AllOfOrganization(long orgId) {
        return qobj.context.organization.id.eq(orgId);
    }

    public static Predicate notNull() {
        return qobj.isNotNull();
    }

	public static Predicate AllUsedInOrganization(Organization org) {
		return qobj.context.organization.eq(org).and(qobj.used.isTrue());
	}
    public static Predicate AllUsedInOrganization(long orgId) {
        return qobj.context.organization.id.eq(orgId).and(qobj.used.isTrue());
    }
    public static Predicate AllUsed() {
        return qobj.used.isTrue();
    }

	public static Predicate AllNotUsedInOrganization(Organization org) {
		return qobj.context.organization.eq(org).and(qobj.used.isFalse());
	}

    public static OrderSpecifier<?> orderByOrganizations() {
        return qobj.context.organization.displayOrder.asc();
    }
    public static OrderSpecifier<?> orderByDisplayOrder() {
        return qobj.displayOrder.asc();
    }

}
