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
package org.esupportail.publisher.service.factories.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.ItemClassificationOrder;
import org.esupportail.publisher.domain.LinkedFileItem;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.repository.ItemClassificationOrderRepository;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.LinkedFileItemRepository;
import org.esupportail.publisher.repository.SubscriberRepository;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.repository.predicates.SubscriberPredicates;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.ContentDTOFactory;
import org.esupportail.publisher.service.factories.SubjectDTOSimpleFactory;
import org.esupportail.publisher.web.rest.dto.ContentDTO;
import org.esupportail.publisher.web.rest.dto.LinkedFileItemDTO;
import org.esupportail.publisher.web.rest.dto.SubscriberFormDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private LinkedFileItemRepository linkedFileItemRepository;

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
        Set<LinkedFileItem> files = Sets.newHashSet(linkedFileItemRepository.findByAbstractItemId(model.getId()));
        Set<LinkedFileItemDTO> pathFiles = Sets.newLinkedHashSet();
        if (!files.isEmpty()) {
            for (LinkedFileItem file: files) {
                pathFiles.add(new LinkedFileItemDTO(file.getUri(), file.getFilename(), file.isInBody(), file.getContentType()));
            }
        }
        dto.setLinkedFiles(pathFiles);
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
