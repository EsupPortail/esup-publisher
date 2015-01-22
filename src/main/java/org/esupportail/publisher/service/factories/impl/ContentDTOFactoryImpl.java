package org.esupportail.publisher.service.factories.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.repository.ItemClassificationOrderRepository;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.SubscriberRepository;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.repository.predicates.SubscriberPredicates;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.ContentDTOFactory;
import org.esupportail.publisher.service.factories.SubjectDTOSimpleFactory;
import org.esupportail.publisher.web.rest.dto.ContentDTO;
import org.esupportail.publisher.web.rest.dto.SubscriberFormDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jgribonvald on 22/04/15.
 */
@Service
@Transactional(readOnly=true)
@Slf4j
public class ContentDTOFactoryImpl implements ContentDTOFactory {

    @Inject
    private ItemRepository<AbstractItem> itemRepository;

    @Inject
    private ItemClassificationOrderRepository itemClassificationOrderRepository;

    //@Inject
    //private PublisherRepository publisherRepository;

    @Inject
    private SubscriberRepository subscriberRepository;

    @Inject
    private SubjectDTOSimpleFactory subjectDTOSimpleFactory;

    @Override
    public ContentDTO from(@NotNull Long id) throws ObjectNotFoundException {
        AbstractItem model = itemRepository.findOne(id);
        if (model == null) throw new ObjectNotFoundException(id, AbstractItem.class);
        return from(model);
    }

    //@Override
    private ContentDTO from(@NotNull AbstractItem model) {
        if (model.getId() == null) return null;

        ContentDTO dto = new ContentDTO();
        dto.setItem(model);
        //Set<Publisher> publishers = new HashSet<>();
        Set<ItemClassificationOrder> itemClassifs = Sets.newHashSet(itemClassificationOrderRepository.findAll(ItemPredicates.itemsClassOfItem(model.getId())));
        Set<ContextKey> classifications = new HashSet<>();
        if (!itemClassifs.isEmpty()) {
            for (ItemClassificationOrder itemClassif : itemClassifs) {
                AbstractClassification classif = itemClassif.getItemClassificationId().getAbstractClassification();
                classifications.add(classif.getContextKey());
                //publishers.add(classif.getPublisher());
            }
        }
        //dto.setPublishers(publishers);
        dto.setClassifications(classifications);
        Set<Subscriber> subscribers = Sets.newHashSet(subscriberRepository.findAll(SubscriberPredicates.onCtx(model.getContextKey())));
        List<SubscriberFormDTO> targets = Lists.newArrayList();
        if (!subscribers.isEmpty()) {
            for (Subscriber subscriber: subscribers) {
                targets.add(new SubscriberFormDTO(subjectDTOSimpleFactory.from(subscriber.getSubjectCtxId().getSubject()), subscriber.getSubscribeType()));
            }
        }
        dto.setTargets(targets);
        return dto;
    }

    @Override
    public Set<ContentDTO> asDTOSet(final Collection<AbstractItem> models) {
        final Set<ContentDTO> dtos = Sets.newHashSet();
        if ((models != null) && !models.isEmpty()) {
            for (AbstractItem model : models) {
                dtos.add(from(model));
            }
        }
        return dtos;
    }

    @Override
    public List<ContentDTO> asDTOList(final Collection<AbstractItem> models) {
        final List<ContentDTO> dtos = Lists.newArrayList();
        if ((models != null) && !models.isEmpty()) {
            for (AbstractItem model : models) {
                dtos.add(from(model));
            }
        }

        return dtos;
    }
}
