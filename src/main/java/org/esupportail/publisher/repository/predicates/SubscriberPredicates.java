/**
 *
 */
package org.esupportail.publisher.repository.predicates;

import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.QSubscriber;

import com.mysema.query.types.Predicate;

/**
 * @author GIP RECIA - Julien Gribonvald 29 oct. 2014
 */
public final class SubscriberPredicates {

	public static Predicate onCtx(final ContextKey ctx) {
		final QSubscriber qsubscriber = QSubscriber.subscriber;

		return qsubscriber.subjectCtxId.context.eq(ctx);
	}
}
