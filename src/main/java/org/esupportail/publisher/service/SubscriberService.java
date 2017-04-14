package org.esupportail.publisher.service;

import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;
import org.esupportail.publisher.domain.AbstractFeed;
import org.esupportail.publisher.domain.Category;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.enums.SubscribeType;
import org.esupportail.publisher.domain.externals.ExternalUserHelper;
import org.esupportail.publisher.repository.CategoryRepository;
import org.esupportail.publisher.repository.FeedRepository;
import org.esupportail.publisher.repository.OrganizationRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.SubscriberRepository;
import org.esupportail.publisher.repository.predicates.SubscriberPredicates;
import org.esupportail.publisher.service.bean.SubjectKeyWithAttribute;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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



    public List<Subscriber> getDefaultsSubscribersOfContext(@NotNull final ContextKey contextKey){
        switch (contextKey.getKeyType()) {
            case ORGANIZATION:
                return getSpecialSubscribersOfOrganization(contextKey);
                //return Lists.newArrayList(subscriberRepository.findAll(SubscriberPredicates.onCtx(contextKey)));
            case PUBLISHER:
                List<Subscriber> subscribers = Lists.newArrayList(subscriberRepository.findAll(SubscriberPredicates.onCtx(contextKey)));
                if (subscribers.isEmpty()) {
                    Publisher publisher = publisherRepository.findOne(contextKey.getKeyId());
                    if (publisher != null) {
                        return getDefaultsSubscribersOfContext(publisher.getContext().getOrganization().getContextKey());
                    }
                }
                return subscribers;
            case CATEGORY:
                Category category = categoryRepository.findOne(contextKey.getKeyId());
                if (category != null) {
                    return getDefaultsSubscribersOfContext(category.getPublisher().getContextKey());
                }
                return Lists.newArrayList();
            case FEED:
                AbstractFeed feed = feedRepository.findOne(contextKey.getKeyId());
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

    public List<Subscriber> getDefinedSubcribersOfContext(@NotNull final ContextKey contextKey) {
        return Lists.newArrayList(subscriberRepository.findAll(SubscriberPredicates.onCtx(contextKey)));
    }

    /**
     * À supprimer quand gestion propre.
     */
    @Inject
    private OrganizationRepository organizationRepository;
    @Inject
    private ExternalUserHelper externalUserHelper;
    private List<Subscriber> getSpecialSubscribersOfOrganization(@NotNull final ContextKey contextKey){
        List<Subscriber> subscribers = Lists.newArrayList();
        if (ContextType.ORGANIZATION.equals(contextKey.getKeyType())) {
            final Organization organization = organizationRepository.findOne(contextKey.getKeyId());
            if (organization != null && organization.getIdentifiers() != null) {
                for (String id : organization.getIdentifiers()) {
                    subscribers.add(new Subscriber(
                        new SubjectKeyWithAttribute(id, SubjectType.PERSON, externalUserHelper.getUserEntityIdentifierAttribute()),
                        contextKey, SubscribeType.FORCED));
                }
            }
        }
        return subscribers;
    }
}
