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


import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysema.commons.lang.Pair;
import com.querydsl.core.BooleanBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.ItemClassificationOrder;
import org.esupportail.publisher.domain.LinkedFileItem;
import org.esupportail.publisher.domain.ReadingIndicator;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.repository.CategoryRepository;
import org.esupportail.publisher.repository.ItemClassificationOrderRepository;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.LinkedFileItemRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.ReadingIndicatorRepository;
import org.esupportail.publisher.repository.externals.ldap.LdapUserDaoImpl;
import org.esupportail.publisher.repository.predicates.ClassificationPredicates;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.repository.predicates.ReadingIndicatorPredicates;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.UserDetailsService;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.ItemVOFactory;
import org.esupportail.publisher.service.factories.RubriqueVOFactory;
import org.esupportail.publisher.service.factories.SubscriberDTOFactory;
import org.esupportail.publisher.web.rest.dto.PublisherDTO;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.esupportail.publisher.web.rest.vo.ActualiteForRead;
import org.esupportail.publisher.web.rest.vo.ActualiteWithSource;
import org.esupportail.publisher.web.rest.vo.ItemVO;
import org.esupportail.publisher.web.rest.vo.ItemsVOForRead;
import org.esupportail.publisher.web.rest.vo.RubriqueVO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service
@Transactional
public class NewsReaderService {

    @Inject
    private LdapUserDaoImpl ldapUserDao;

    @Inject
    private PublisherRepository publisherRepository;

    @Inject
    private RubriqueVOFactory rubriqueVOFactory;

    @Inject
    private ItemVOFactory itemVOFactory;

    @Inject
    private SubscriberDTOFactory subscriberDTOFactory;

    @Inject
    private PublishersReadLoaderService publishersReadLoader;

    @Inject
    private ReadingIndicatorRepository readingIndicatorRepository;

    @Inject
    private ItemRepository<AbstractItem> itemRepository;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private ItemClassificationOrderRepository itemClassificationOrderRepository;

    @Inject
    private LinkedFileItemRepository linkedFileItemRepository;

    @Inject
    private SubscriberService subscriberService;

    @Inject
    private ViewService viewService;

    @Inject
    private UserDetailsService userDetailsService;

    final Pattern filesPattern = Pattern.compile(".*?(\\/files.*)");

    /**
     * Retourne les actualités d'un utilisateur sur un reader donné
     * @param readerId ID du reader recherché
     * @param reading Filtre de lecture (true = lues, false = non lues, null = tout)
     */
    public ActualiteWithSource getNewsByUserOnContext(Long readerId, Boolean reading,
                                            HttpServletRequest request) throws Exception {

        final CustomUserDetails user = this.userDetailsService.loadUserByUsername(
            SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

        final List<PublisherDTO> publisherDTOList = publishersReadLoader.getUserPublishersToReadOfReader(user.getUser(), readerId);

        Set<ItemVO> itemVOSet = new HashSet<>();
        Set<RubriqueVO> rubriqueVOSet = new HashSet<>();
        Set<String> sources = new HashSet<>();

        final Map<String, Boolean> readingIndicators = getAllReadingInfosForCurrentUser(user.getUser());

        publisherDTOList.forEach(publisherDto -> {
            final ActualiteForRead actualite = this.getActualiteByPublisher(publisherDto, request);

            actualite.getItems().forEach(itemsVOForRead -> {

                // Filtre lu = dans la liste des readingIndincators à true
                // Filtre non lu = pas dans la liste OU dans la liste des readingIndincators à false
                if (reading == null || (reading ?
                    (readingIndicators.containsKey(itemsVOForRead.getItemVO().getUuid()) && readingIndicators.get(
                        itemsVOForRead.getItemVO().getUuid())) :
                    (!readingIndicators.containsKey(itemsVOForRead.getItemVO().getUuid()) || !readingIndicators.get(
                        itemsVOForRead.getItemVO().getUuid()))
                )) {

                    final Map<String, RubriqueVO> rubriquesMap = actualite.getRubriques().stream().collect(
                        Collectors.toMap(RubriqueVO::getUuid, rubriqueVO -> rubriqueVO));

                    if (viewService.evalSubscribing(user.getUser(), itemsVOForRead.getSubscribersDTO())) {

                        itemsVOForRead.getItemVO().getArticle().setLink(
                            itemsVOForRead.getItemVO().getArticle().getLink().replaceAll("\\/view", "/news"));

                        if (itemsVOForRead.getItemVO().getArticle().getEnclosure() != null) {

                            Matcher matcher = filesPattern.matcher(itemsVOForRead.getItemVO().getArticle().getEnclosure());
                            if (matcher.find()) {
                                itemsVOForRead.getItemVO().getArticle().setEnclosure(matcher.group(1));
                            }
                        }
                        itemsVOForRead.getItemVO().setSource(publisherDto.getOrganization().getDisplayName());
                        sources.add(itemsVOForRead.getItemVO().getSource());
                        itemVOSet.add(itemsVOForRead.getItemVO());
                        itemsVOForRead.getItemVO().getRubriques().forEach(
                            r -> rubriqueVOSet.add(rubriquesMap.get(r.toString())));

                    }
                }
            });
        });

        ActualiteWithSource actualite = new ActualiteWithSource();
        actualite.getItems().addAll(itemVOSet);
        actualite.getItems().sort(Comparator.comparing(ItemVO::getPubDate).reversed());
        actualite.getRubriques().addAll(rubriqueVOSet);
        actualite.getSources().addAll(sources);
        Collections.sort(actualite.getSources());
        return actualite;
    }

    public Map<String, Boolean> getAllReadingInfosForCurrentUser() {

        final CustomUserDetails user = this.userDetailsService.loadUserByUsername(
            SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

        return this.getAllReadingInfosForCurrentUser(user.getUser());
    }

    public Map<String, Boolean> getAllReadingInfosForCurrentUser(@NotNull final UserDTO userDto) {
        return StreamSupport.stream(this.readingIndicatorRepository.findAll(
                ReadingIndicatorPredicates.readingIndicationOfUser(userDto)).spliterator(), false)
            .collect(Collectors.toMap(indicator -> indicator.getItem().getId().toString(),
                ReadingIndicator::isRead));
    }

    public void readingManagement(Long id, boolean isRead) throws ObjectNotFoundException {

        final CustomUserDetails user = this.userDetailsService.loadUserByUsername(
            SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        Optional<AbstractItem> optionalItem = itemRepository.findOne(
            ItemPredicates.ItemWithStatus(id, ItemStatus.PUBLISHED, null));

        if (isRead) {
            if (!this.readingIndicatorRepository.exists(ReadingIndicatorPredicates.readingIndicationOfItemAndUser(id, user.getUser()))) {
                if (optionalItem.isPresent()) {
                    this.readingIndicatorRepository.save(
                        new ReadingIndicator(optionalItem.get(), user.getUser().getLogin(), true, 1));
                } else {
                    throw new ObjectNotFoundException(id, AbstractItem.class);
                }
            } else {
                Optional<ReadingIndicator> optional = this.readingIndicatorRepository.findOne(ReadingIndicatorPredicates.readingIndicationOfItemAndUser(id, user.getUser()));
                ReadingIndicator readingIndicator = optional.orElse(null);
                if (optional.isPresent()) {
                    readingIndicator.setRead(true);
                    readingIndicator.setReadingCounter(readingIndicator.getReadingCounter() + 1);
                    this.readingIndicatorRepository.save(readingIndicator);
                }
            }
        } else {
            Optional<ReadingIndicator> optional = this.readingIndicatorRepository.findOne(ReadingIndicatorPredicates.readingIndicationOfItemAndUser(id, user.getUser()));
            ReadingIndicator readingIndicator = optional.orElse(null);
            if (readingIndicator != null) {
                readingIndicator.setRead(false);
                this.readingIndicatorRepository.save(readingIndicator);
            } else throw new ObjectNotFoundException(id, ReadingIndicator.class);
        }
    }

    public void reloadPublishersForReader(final long readerId) {
        publishersReadLoader.reloadPublishersOfReader(readerId);
    }

    @Cacheable(value = "actualiteByPublisher", key = "#publisher")
    public ActualiteForRead getActualiteByPublisher(final PublisherDTO publisher, final HttpServletRequest request) {

        ActualiteForRead actualite = new ActualiteForRead();

        final List<? extends AbstractClassification> categories = Lists.newArrayList(
            categoryRepository.findAll(ClassificationPredicates.CategoryOfPublisher(publisher.getModelId()),
                ClassificationPredicates.categoryOrderByDisplayOrderType(publisher.getDefaultDisplayOrder())));

        actualite.getRubriques().addAll(rubriqueVOFactory.asVOList(categories));

        BooleanBuilder itemBuilder = new BooleanBuilder(ItemPredicates.itemsClassOfPublisher(publisher.getModelId()))
            .and(ItemPredicates.OwnedItemsClassOfStatus(null, ItemStatus.PUBLISHED));

        final List<ItemClassificationOrder> itemsClass = Lists.newArrayList(
            itemClassificationOrderRepository.findAll(itemBuilder,
                ItemPredicates.orderByClassifDefinition(publisher.getDefaultItemsDisplayOrder())));

        Map<Long, Pair<AbstractItem, List<AbstractClassification>>> itemsMap = Maps.newLinkedHashMap();

        for (ItemClassificationOrder ico : itemsClass) {
            final AbstractClassification classif = ico.getItemClassificationId().getAbstractClassification();
            final Long itemId = ico.getItemClassificationId().getAbstractItem().getId();
            if (!itemsMap.containsKey(itemId)) {
                itemsMap.put(itemId, new Pair<>(
                    ico.getItemClassificationId().getAbstractItem(), Lists.newArrayList(classif)));
            } else {
                itemsMap.get(itemId).getSecond().add(classif);
            }
        }

        for (Map.Entry<Long, Pair<AbstractItem, List<AbstractClassification>>> entry : itemsMap.entrySet()) {
            final AbstractItem item = entry.getValue().getFirst();
            final List<LinkedFileItem> linkedFiles =
                linkedFileItemRepository.findByAbstractItemIdAndInBody(item.getId(), false);
            final List<Subscriber> subscribers = subscriberService.getDefinedSubscribersOfContext(item.getContextKey());
            actualite.getItems().add(new ItemsVOForRead(
                    itemVOFactory.from(item, entry.getValue().getSecond(),subscribers, linkedFiles, request),
                    subscriberDTOFactory.asDTOList(subscribers)));
        }
        return actualite;
    }

}
