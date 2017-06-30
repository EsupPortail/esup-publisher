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
