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
