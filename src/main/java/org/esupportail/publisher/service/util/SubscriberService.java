package org.esupportail.publisher.service.util;

import com.google.common.collect.Lists;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.repository.*;
import org.esupportail.publisher.repository.predicates.SubscriberPredicates;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by jgribonvald on 04/06/15.
 */
@Service
@Transactional(readOnly=true)
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



    public List<Subscriber> getDefaultsSubscribersOfContext(final ContextKey contextkey){
        switch (contextkey.getKeyType()) {
            case ORGANIZATION:
                return Lists.newArrayList(subscriberRepository.findAll(SubscriberPredicates.onCtx(contextkey)));
            case PUBLISHER:
                List<Subscriber> subscribers = Lists.newArrayList(subscriberRepository.findAll(SubscriberPredicates.onCtx(contextkey)));
                if (subscribers.isEmpty()) {
                    Publisher publisher = publisherRepository.findOne(contextkey.getKeyId());
                    if (publisher != null) {
                        return getDefaultsSubscribersOfContext(publisher.getContext().getOrganization().getContextKey());
                    }
                }
                return subscribers;
            case CATEGORY:
                Category category = categoryRepository.findOne(contextkey.getKeyId());
                if (category != null) {
                    return getDefaultsSubscribersOfContext(category.getPublisher().getContextKey());
                }
                return Lists.newArrayList();
            case FEED:
                AbstractFeed feed = feedRepository.findOne(contextkey.getKeyId());
                if (feed != null) {
                    return getDefaultsSubscribersOfContext(feed.getPublisher().getContextKey());
                }
                return Lists.newArrayList();
            /*case ITEM:
                // should not come here
                return Lists.newArrayList();*/
            default:return Lists.newArrayList();
        }
    }
}
