/**
 *
 */
package org.esupportail.publisher.repository.predicates;

import org.esupportail.publisher.domain.QRedactor;
import org.esupportail.publisher.domain.Redactor;

import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * @author GIP RECIA - Julien Gribonvald 29 sept. 2014
 */
public final class RedactorPredicates {

	public static Predicate sameName(final Redactor redactor) {
		final QRedactor qredactor = QRedactor.redactor;
		final BooleanExpression redactorExistName = qredactor.name.eq(redactor
				.getName());
		final BooleanExpression redactorExistId = qredactor.id.eq(redactor
				.getId());
		if (redactor.getId() > 0) {
			return redactorExistName.and(redactorExistId.not());
		}
		return redactorExistName;
	}
}
