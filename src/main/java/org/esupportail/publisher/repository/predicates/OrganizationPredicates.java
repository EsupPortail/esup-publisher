/**
 *
 */
package org.esupportail.publisher.repository.predicates;

import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.QOrganization;

import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;

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

	public static OrderSpecifier<?> orderBy() {
		final QOrganization qorg = QOrganization.organization;
		return qorg.displayOrder.asc();
	}

}
