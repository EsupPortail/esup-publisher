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

import com.querydsl.core.types.dsl.Expressions;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.QSubjectContextKey;
import org.esupportail.publisher.domain.QSubscriber;

import com.querydsl.core.types.Predicate;
import org.esupportail.publisher.domain.SubjectContextKey;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.SubjectType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GIP RECIA - Julien Gribonvald 29 oct. 2014
 */
public final class SubscriberPredicates {

	public static Predicate onCtx(final ContextKey ctx) {
		final QSubscriber qsubscriber = QSubscriber.subscriber;

		return qsubscriber.subjectCtxId.context.eq(ctx);
	}

    public static Predicate inIdList(final List<Long> ids) {
        final QSubscriber qsubscriber = QSubscriber.subscriber;

        if (ids == null || ids.isEmpty()) {
            return Expressions.FALSE; // Renvoie un prédicat toujours faux si la liste est vide ou nulle
        }

        return qsubscriber.subjectCtxId.context.keyId.in(ids).and(qsubscriber.subjectCtxId.context.keyType.eq(ContextType.ITEM)); // Vérifie que l'ID du Subscriber est dans la liste d'IDs
    }



}
