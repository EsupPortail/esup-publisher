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
