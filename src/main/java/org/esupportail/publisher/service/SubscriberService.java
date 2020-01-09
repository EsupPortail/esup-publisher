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
package org.esupportail.publisher.service;

import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.esupportail.publisher.domain.AbstractFeed;
import org.esupportail.publisher.domain.Category;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.repository.CategoryRepository;
import org.esupportail.publisher.repository.FeedRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.SubscriberRepository;
import org.esupportail.publisher.repository.predicates.SubscriberPredicates;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

/**
 * Created by jgribonvald on 04/06/15.
 */
@Service
@Transactional(readOnly = true)
public class SubscriberService {

	@Inject
	private SubscriberRepository subscriberRepository;

	@Inject
	private PublisherRepository publisherRepository;
	@Inject
	private CategoryRepository categoryRepository;
	@Inject
	private FeedRepository<AbstractFeed> feedRepository;

	/*@Inject
	private ItemClassificationOrderRepository itemClassificationOrderRepository;*/

	public List<Subscriber> getDefaultsSubscribersOfContext(@NotNull final ContextKey contextKey) {
		switch (contextKey.getKeyType()) {
		case ORGANIZATION:
			return Lists.newArrayList(subscriberRepository.findAll(SubscriberPredicates.onCtx(contextKey)));
		case PUBLISHER:
			List<Subscriber> subscribers = Lists.newArrayList(subscriberRepository.findAll(SubscriberPredicates
					.onCtx(contextKey)));
			if (subscribers.isEmpty()) {
				Publisher publisher = publisherRepository.getOne(contextKey.getKeyId());
				if (publisher != null) {
					return getDefaultsSubscribersOfContext(publisher.getContext().getOrganization().getContextKey());
				}
			}
			return subscribers;
		case CATEGORY:
			Category category = categoryRepository.getOne(contextKey.getKeyId());
			if (category != null) {
				return getDefaultsSubscribersOfContext(category.getPublisher().getContextKey());
			}
			return Lists.newArrayList();
		case FEED:
			AbstractFeed feed = feedRepository.getOne(contextKey.getKeyId());
			if (feed != null) {
				return getDefaultsSubscribersOfContext(feed.getPublisher().getContextKey());
			}
			return Lists.newArrayList();
			/*case ITEM:
			    // should not come here
			    return Lists.newArrayList();*/
		default:
			return Lists.newArrayList();
		}
	}

	public List<Subscriber> getDefinedSubscribersOfContext(@NotNull final ContextKey contextKey) {
		return Lists.newArrayList(subscriberRepository.findAll(SubscriberPredicates.onCtx(contextKey)));
	}
}
