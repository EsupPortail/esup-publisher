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
package org.esupportail.publisher.web;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractFeed;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Flash;
import org.esupportail.publisher.domain.ItemClassificationOrder;
import org.esupportail.publisher.domain.LinkedFileItem;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.QAbstractItem;
import org.esupportail.publisher.domain.QItemClassificationOrder;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.enums.WritingMode;
import org.esupportail.publisher.domain.util.Views;
import org.esupportail.publisher.repository.CategoryRepository;
import org.esupportail.publisher.repository.FeedRepository;
import org.esupportail.publisher.repository.ItemClassificationOrderRepository;
import org.esupportail.publisher.repository.LinkedFileItemRepository;
import org.esupportail.publisher.repository.OrganizationRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.predicates.ClassificationPredicates;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.repository.predicates.PublisherPredicates;
import org.esupportail.publisher.service.HighlightedClassificationService;
import org.esupportail.publisher.service.SubscriberService;
import org.esupportail.publisher.service.bean.HighlightedClassification;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.esupportail.publisher.service.factories.CategoryFactory;
import org.esupportail.publisher.service.factories.CategoryProfileFactory;
import org.esupportail.publisher.service.factories.FlashInfoVOFactory;
import org.esupportail.publisher.service.factories.ItemVOFactory;
import org.esupportail.publisher.service.factories.RubriqueVOFactory;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.esupportail.publisher.web.rest.vo.Category;
import org.esupportail.publisher.web.rest.vo.CategoryProfilesUrl;
import org.esupportail.publisher.web.rest.vo.FlashInfoVO;
import org.esupportail.publisher.web.rest.vo.ItemVO;
import org.esupportail.publisher.web.rest.vo.RubriqueVO;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysema.commons.lang.Pair;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by jgribonvald on 22/01/16.
 * protected by IpChecking configured on SecurityConfiguration
 */
@RestController
@RequestMapping("/published")
@Slf4j
public class PublishController {

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private PublisherRepository publisherRepository;

//    @Inject
//    private ClassificationRepository<AbstractClassification> classificationRepository;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private FeedRepository<AbstractFeed> feedRepository;

//    @Inject
//    private ItemRepository itemRepository;

    @Inject
    private ItemClassificationOrderRepository itemClassificationOrderRepository;

    @Inject
    private FlashInfoVOFactory flashInfoVOFactory;

    @Inject
    private RubriqueVOFactory rubriqueVOFactory;

    @Inject
    private ItemVOFactory itemVOFactory;

    @Inject
    private CategoryProfileFactory categoryProfileFactory;

    @Inject
    private CategoryFactory categoryFactory;

    @Inject
    private SubscriberService subscriberService;

    @Inject
    private ServiceUrlHelper urlHelper;

    @Inject
    private HighlightedClassificationService highlightedClassificationService;

    @Inject
    private LinkedFileItemRepository linkedFileItemRepository;

    @RequestMapping(value = "/flash/{organization_id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Deprecated
    @JsonView(Views.Flash.class)
    @Timed
    public List<FlashInfoVO> getFlashInfo(@PathVariable("organization_id") String uai, final HttpServletRequest request ) throws URISyntaxException {
        log.debug("Entering getFlashInfo with params : organization identifier={}", uai);
        Organization org = organizationRepository.findByIdentifiers(uai);
        if (org != null) {
            DisplayOrderType displayOrder = DisplayOrderType.LAST_CREATED_MODIFIED_FIRST;
            final BooleanExpression builder = QItemClassificationOrder.itemClassificationOrder.itemClassificationId.abstractItem.id
            	.in(JPAExpressions.selectDistinct(QAbstractItem.abstractItem.id)
            		.from(QAbstractItem.abstractItem)
                    .where(ItemPredicates.FlashItemsOfOrganization(org), ItemPredicates.OwnedItemsOfStatus(false, ItemStatus.PUBLISHED.getId())));
            List<ItemClassificationOrder> itemsClasss = Lists.newArrayList(itemClassificationOrderRepository.findAll(builder, ItemPredicates.orderByClassifDefinition(displayOrder)));

            return getFlashInfo(itemsClasss, request);
        }
        return Lists.newArrayList();
    }

    private List<FlashInfoVO> getFlashInfo(final List<ItemClassificationOrder> itemsClasss, final HttpServletRequest request){
        if (!itemsClasss.isEmpty()) {
            Map<Flash, List<AbstractClassification>> itemsMap= Maps.newLinkedHashMap();
            for (ItemClassificationOrder ico: itemsClasss) {
                final AbstractClassification classif = ico.getItemClassificationId().getAbstractClassification();
                //categories.add(classif);
                final Flash flash = (Flash) ico.getItemClassificationId().getAbstractItem();
                if (!itemsMap.containsKey(flash)) {
                    itemsMap.put(flash, Lists.newArrayList(classif));
                } else {
                    itemsMap.get(flash).add(classif);
                }
            }
            return flashInfoVOFactory.asVOList(itemsMap, request);
        }
        return Lists.newArrayList();
    }

    @RequestMapping(value = "/flash/{reader_id}/{redactor_id}/{organization_id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView(Views.Flash.class)
    @Timed
    public List<FlashInfoVO> getFlashInfo(@PathVariable("reader_id") Long readerId, @PathVariable("redactor_id") Long redactorId,
                                          @PathVariable("organization_id") String uai, final HttpServletRequest request ) throws URISyntaxException {
        log.debug("Entering getFlashInfo with params : organization identifier={}, reader_id={}, redactor_id={}", uai, readerId, redactorId);
        final Organization org = organizationRepository.findByIdentifiers(uai);
        if (org != null) {
            final BooleanBuilder builder = new BooleanBuilder(PublisherPredicates.AllOfUsedState(true))
                .and(PublisherPredicates.AllOfReader(readerId))
                .and(PublisherPredicates.AllOfRedactor(redactorId))
                .and(PublisherPredicates.AllOfOrganization(org));
            try {
                Optional<Publisher> optionalPublisher =  publisherRepository.findOne(builder);
                final Publisher publisher = optionalPublisher.orElse(null);
                if (publisher != null) {
                    final BooleanExpression builderExp = QItemClassificationOrder.itemClassificationOrder.itemClassificationId.abstractItem.id
                        .in(JPAExpressions.selectDistinct(QAbstractItem.abstractItem.id)
                    		.from(QAbstractItem.abstractItem)
                        	.where(ItemPredicates.FlashItemsOfOrganization(org), ItemPredicates.OwnedItemsOfStatus(false, ItemStatus.PUBLISHED.getId())))
                    	.and(ItemPredicates.itemsClassOfPublisher(publisher));
                    final List<ItemClassificationOrder> itemsClasss = Lists.newArrayList(itemClassificationOrderRepository.findAll(builderExp,
                        ItemPredicates.orderByClassifDefinition(publisher.getDefaultDisplayOrder())));

                    return getFlashInfo(itemsClasss, request);
                }
            }catch(IncorrectResultSizeDataAccessException e){
                log.error("The request to obtain all Flash-Info on an organization found more than one Flash context. Solve this problem !");
            }
        }
        return Lists.newArrayList();
    }

    /* not used
    @RequestMapping(value = "/items/{organization_id}/{reader_id}",
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @PreAuthorize("hasIpAddress(@appIpVariableHolder.getIpRange())")
    @Timed
    public Object getItems(@PathVariable("organization_id") String uai, @PathVariable("reader_id") Long reader, @RequestParam(value = "redactor_id", required = false) Long redactor) {
        log.debug("Entering getItems with params : uai={}, reader_id={}, redactor_id={}", uai, reader, redactor);
        Organization org = organizationRepository.findByIdentifiers(uai);
        if (org == null) {
            log.error("Getting Items of an unregistred organization {} !", uai);
            return Lists.newArrayList();
        }
        List<Publisher> publishers;
        if (redactor != null) {
            publishers = Lists.newArrayList(publisherRepository.findAll(PublisherPredicates.AllUsedInOrganizationWithReaderAndRedactor(org.getId(), reader, redactor)));
        } else {
            publishers = Lists.newArrayList(publisherRepository.findAll(PublisherPredicates.AllUsedInOrganizationWithReader(org.getId(), reader)));
        }
        if (publishers.size() > 1) {
            log.error("Try to provide Items of several publishers, warning thisn't well implemented !, parameters provided are uai={}, reader_id={}, redactor_id={}", uai, reader, redactor);
            return Lists.newArrayList();
        }

        return getItemsOfPublisher(publishers.get(0));
    }*/

    @RequestMapping(value = "/contexts/{reader_id}/{redactor_id}",
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_XML_VALUE})
    @Timed
    public CategoryProfilesUrl getAllPublisherContext(@PathVariable("reader_id") Long readerId, @PathVariable("redactor_id") Long redactorId, final HttpServletRequest request ) {
        log.debug("Entering getAllPublisherContext with params :  reader_id={}, redactor_id={}", readerId, redactorId);
        BooleanBuilder builder = new BooleanBuilder(PublisherPredicates.AllOfUsedState(true))
            .and(PublisherPredicates.AllOfReader(readerId))
            .and(PublisherPredicates.AllOfRedactor(redactorId));
        final List<Publisher> publishers = Lists.newArrayList(publisherRepository.findAll(builder));
        CategoryProfilesUrl cpu = new CategoryProfilesUrl();
        if (!publishers.isEmpty()) {
            final Redactor redactor = publishers.get(0).getContext().getRedactor();
            final String base = urlHelper.getRootAppUrl(request);
            for (Publisher pub : publishers) {
                String urlCategory = null;
                String urlActualite = null;
                if (redactor.getNbLevelsOfClassification() == 1 && WritingMode.TARGETS_ON_ITEM.equals(redactor.getWritingMode())) {
                    urlActualite =  base + "published/items/" + pub.getId();
                } else {
                    urlCategory = base + "published/categories/" + pub.getId();
                }
                cpu.getCategoryProfilesUrl().add(categoryProfileFactory.from(pub, subscriberService.getDefaultsSubscribersOfContext(pub.getContextKey()), urlActualite, urlCategory));
            }
        }
        return cpu;
    }

    @RequestMapping(value = "/contextOf/{organization_id}/{reader_id}/{redactor_id}",
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_XML_VALUE})
    @Timed
    public CategoryProfilesUrl getPublisherContextOf(@PathVariable("organization_id") String id, @PathVariable("reader_id") Long readerId,
                                                     @PathVariable("redactor_id") Long redactorId, final HttpServletRequest request ) {
        log.debug("Entering getPublisherContextOf with params : organization_id={}, reader_id={}, redactor_id={}", id, readerId, redactorId);
        BooleanBuilder builder = new BooleanBuilder(PublisherPredicates.AllOfUsedState(true))
            .and(PublisherPredicates.AllOfReader(readerId))
            .and(PublisherPredicates.AllOfRedactor(redactorId))
            .and(PublisherPredicates.AllOfOrganizationByIdentifier(id));
        final List<Publisher> publishers = Lists.newArrayList(publisherRepository.findAll(builder));
        CategoryProfilesUrl cpu = new CategoryProfilesUrl();
        if (!publishers.isEmpty()) {
            final Redactor redactor = publishers.get(0).getContext().getRedactor();
            final String base = urlHelper.getRootAppUrl(request);
            for (Publisher pub : publishers) {
                String urlCategory = null;
                String urlActualite = null;
                if (redactor.getNbLevelsOfClassification() == 1) {
                    if (WritingMode.TARGETS_ON_ITEM.equals(redactor.getWritingMode())) {
                        urlActualite = base + "published/items/" + pub.getId();
                    } else {
                        urlCategory = base + "published/categories/" + pub.getId();
                    }
                    cpu.getCategoryProfilesUrl().add(categoryProfileFactory.from(pub, subscriberService.getDefaultsSubscribersOfContext(pub.getContextKey()), urlActualite, urlCategory));
                } else {
                    // we don't manager more than redactor.getNbLevelsOfClassification() > 2
                    // systeme classic esup-lecture/esup-news
                    if (WritingMode.TARGETS_ON_ITEM.equals(redactor.getWritingMode())) throw new NotYetImplementedException();
                    List<? extends AbstractClassification> cts = Lists.newArrayList(categoryRepository.findAll(ClassificationPredicates.CategoryOfPublisher(pub.getId()),
                        ClassificationPredicates.categoryOrderByDisplayOrderType(pub.getDefaultDisplayOrder())));
                    log.debug("list of categories associated to publisher : {}", cts);
                    for (AbstractClassification classif: cts) {
                        final String urlFeeds = base + "published/feeds/" + classif.getId();
                        cpu.getCategoryProfilesUrl().add(categoryProfileFactory.from(pub, classif, subscriberService.getDefinedSubscribersOfContext(classif.getContextKey()), urlFeeds, true));
                    }
                }
            }
        }
        return cpu;
    }

    @RequestMapping(value = "/items/{publisher_id}",
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_XML_VALUE})
    @Timed
    public Actualite getItemsFromPublisher(@PathVariable("publisher_id") Long publisherId, HttpServletRequest request ) {
        //getting items on new way
        log.debug("Entering getItems with param : publisher_id={}", publisherId);
        Optional<Publisher> optionalPublisher =  publisherRepository.findById(publisherId);
        Publisher publisher = optionalPublisher.orElse(null);

        return getItemsOnPublisherNewWay(publisher, request);
    }

    @RequestMapping(value = "/categories/{publisher_id}",
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Timed
    public Category getCategories(@PathVariable("publisher_id") Long publisherId, final HttpServletRequest request) {
        log.debug("Entering getCategories with param : publisher_id={}", publisherId);
        Optional<Publisher> optionalPublisher =  publisherRepository.findById(publisherId);
        Publisher publisher = optionalPublisher.orElse(null);

        if (publisher != null && publisher.isUsed() && WritingMode.STATIC.equals(publisher.getContext().getRedactor().getWritingMode())) {
            final String baseUrl = urlHelper.getRootAppUrl(request);
            if (publisher.getContext().getRedactor().getNbLevelsOfClassification() == 1) {
                // systeme classic esup-lecture/esup-news mais sans le niveau des thèmes
                List<? extends AbstractClassification> cts = Lists.newArrayList(categoryRepository.findAll(ClassificationPredicates.CategoryOfPublisher(publisher.getId()),
                    ClassificationPredicates.categoryOrderByDisplayOrderType(publisher.getDefaultDisplayOrder())));
                log.debug("list of categories associated to publisher : {}", cts);
                Map<AbstractClassification, List<Subscriber>> classifSubscribers = Maps.newHashMap();
                for (AbstractClassification classif: cts) {
                    classifSubscribers.put(classif, subscriberService.getDefinedSubscribersOfContext(classif.getContextKey()));
                }
                return categoryFactory.fromCategoriesClassifs(publisher, baseUrl, subscriberService.getDefaultsSubscribersOfContext(publisher.getContextKey()), classifSubscribers);
            } else {

                throw new IllegalAccessError("Wrong call of methods with more than one level in classifications, you should call instead getAbstractFeeds method");
            }
        }

        return new Category();
    }

    @RequestMapping(value = "/feeds/{category_id}",
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Timed
    public Category getAbstractFeeds(@PathVariable("category_id") Long categoryId, final HttpServletRequest request) {
        // systeme classic esup-lecture/esup-news
        log.debug("Entering getAbstractFeeds with param : category_id={}", categoryId);
        Optional<org.esupportail.publisher.domain.Category> optionalCategory =  categoryRepository.findById(categoryId);
        org.esupportail.publisher.domain.Category category = optionalCategory.orElse(null);

        if (category != null && category.getPublisher().isUsed() && WritingMode.STATIC.equals(category.getPublisher().getContext().getRedactor().getWritingMode())) {
            final String baseUrl = urlHelper.getRootAppUrl(request);
            List<? extends AbstractFeed> cts = Lists.newArrayList(feedRepository.findAll(ClassificationPredicates.AbstractFeedsOfCategory(categoryId),
                ClassificationPredicates.feedOrderByDisplayOrderType(category.getDefaultDisplayOrder())));
            log.debug("list of feeds associated to category : {}", cts);
            Map<AbstractFeed, List<Subscriber>> classifSubscribers = Maps.newHashMap();
            for (AbstractFeed feed: cts) {
                classifSubscribers.put(feed, subscriberService.getDefinedSubscribersOfContext(feed.getContextKey()));
            }
            return categoryFactory.fromAbstractFeeds(category, baseUrl, true, subscriberService.getDefaultsSubscribersOfContext(category.getContextKey()), classifSubscribers);

        }

        return new Category();
    }

    //@Transactional
    private Actualite getItemsOnPublisherNewWay(@NotNull Publisher publisher, final HttpServletRequest request  ) {
        log.debug("getItemsOnPublisherNewWay with publisher {}", publisher);
        Actualite returnedObj = new Actualite();
        BooleanBuilder builder = new BooleanBuilder();
        //final QItemClassificationOrder icoQ = QItemClassificationOrder.itemClassificationOrder;
        builder.and(ItemPredicates.itemsClassOfPublisher(publisher.getId()));
        builder.and(ItemPredicates.OwnedItemsClassOfStatus(null, ItemStatus.PUBLISHED));

        List<? extends AbstractClassification> cts = Lists.newArrayList(categoryRepository.findAll(ClassificationPredicates.CategoryOfPublisher(publisher.getId()),
            ClassificationPredicates.categoryOrderByDisplayOrderType(publisher.getDefaultDisplayOrder())));
        log.debug("list of categories associated to publisher : {}", cts);

        List<ItemClassificationOrder> itemsClasss = Lists.newArrayList(itemClassificationOrderRepository.findAll(builder,
            ItemPredicates.orderByPublisherDefinition(publisher.getDefaultDisplayOrder()), ItemPredicates.orderByClassifDefinition(DisplayOrderType.LAST_CREATED_MODIFIED_FIRST)));
        log.debug("list of itemsClasss associated to publisher : {}", itemsClasss);

        Map<Long, Pair<AbstractItem, List<AbstractClassification>>> itemsMap = Maps.newLinkedHashMap();

        // get unique items associated to all his classifs
        for ( ItemClassificationOrder ico: itemsClasss) {
            final AbstractClassification classif = ico.getItemClassificationId().getAbstractClassification();
            //categories.add(classif);
            final Long itemId = ico.getItemClassificationId().getAbstractItem().getId();
            if (!itemsMap.containsKey(itemId)) {
                itemsMap.put(itemId, new Pair<AbstractItem, List<AbstractClassification>>(ico.getItemClassificationId().getAbstractItem(),Lists.newArrayList(classif)));
            } else {
                itemsMap.get(itemId).getSecond().add(classif);
            }
        }
        List<RubriqueVO> rubriques;
        if (publisher.isDoHighlight()) {
            final HighlightedClassification specialClassif = highlightedClassificationService.getClassification();
            // to get the order of HighlightedClassification as first
            rubriques = Lists.newArrayList(rubriqueVOFactory.from(specialClassif));
            rubriques.addAll(rubriqueVOFactory.asVOList(cts));
        } else {
           rubriques = Lists.newArrayList(rubriqueVOFactory.asVOList(cts));
        }

        returnedObj.setRubriques(rubriques);
        returnedObj.setItems(new ArrayList<ItemVO>());
        for (Map.Entry<Long, Pair<AbstractItem, List<AbstractClassification>>> entry : itemsMap.entrySet()) {
            final AbstractItem item = entry.getValue().getFirst();
            final List<LinkedFileItem> linkedFiles = linkedFileItemRepository.findByAbstractItemIdAndInBody(item.getId(), false);
            returnedObj.getItems().add(itemVOFactory.from(item, entry.getValue().getSecond(),
                subscriberService.getDefinedSubscribersOfContext(item.getContextKey()), linkedFiles, request));
        }

        /*List<Category> cts = Lists.newArrayList(categoryRepository.findAll(ClassificationPredicates.categoryOfPublisher(publisher.getId()),
            ClassificationPredicates.categoryOrderByDisplayOrderType(publisher.getDefaultDisplayOrder())));
        returnedObj.setRubriques(rubriqueVOFactory.asVOList(cts));
        returnedObj.setItems(new ArrayList<ItemVO>());
        Map<AbstractItem,List<Category>> map = Maps.newLinkedHashMap();
        for (Category cat: cts) {
            BooleanBuilder builder = new BooleanBuilder(ItemPredicates.itemsClassOfClassification(cat));
            builder.and(ItemPredicates.OwnedItemsOfStatus(null, ItemStatus.PUBLISHED.getId()));
            final DisplayOrderType displayOrder = cat.getDefaultDisplayOrder();
            final List<ItemClassificationOrder> itemsClass =
                Lists.newArrayList(itemClassificationOrderRepository.findAll(builder, ItemPredicates.orderByClassifDefinition(displayOrder)));
            for (ItemClassificationOrder item : itemsClass) {

            }
        }

        returnedObj.getItems().add(itemVOFactory.from(item.getItemClassificationId().getAbstractItem(),
            subscriberService.getDefinedSubscribersOfContext(item.getItemClassificationId().getAbstractItem().getContextKey()));*/

        return returnedObj;
    }


    /*private List<? extends IContext> getContextChilds(@NotNull ContextKey ctx) {
        Predicate filter;
        DisplayOrderType displayOrder;
        BooleanBuilder builder = new BooleanBuilder();
        switch (ctx.getKeyType()) {
            case PUBLISHER :
                displayOrder = publisherRepository.findOne(ctx.getKeyId()).getDefaultDisplayOrder();
                filter = ClassificationPredicates.CategoryOfPublisher(ctx.getKeyId());
                builder.and(filter);
                return Lists.newArrayList(categoryRepository.findAll(builder, ClassificationPredicates.categoryOrderByDisplayOrderType(displayOrder)));
            case CATEGORY :
                Category category = categoryRepository.findOne(ctx.getKeyId());
                displayOrder = category.getDefaultDisplayOrder();
                if (category.getPublisher().getContext().getRedactor().getNbLevelsOfClassification() > 1) {
                    filter = ClassificationPredicates.AbstractFeedsOfCategory(category);
                    builder.and(filter);
                    return Lists.newArrayList(feedRepository.findAll(builder, ClassificationPredicates.feedOrderByDisplayOrderType(displayOrder)));
                } else {
                    filter = ItemPredicates.itemsClassOfClassification(category);
                    builder.and(filter);
                    List<ItemClassificationOrder> itemsClass =
                        Lists.newArrayList(itemClassificationOrderRepository.findAll(builder, ItemPredicates.orderByClassifDefinition(displayOrder)));
                    List<AbstractItem> items = Lists.newArrayList();
                    for (ItemClassificationOrder item : itemsClass) {
                        items.add(item.getItemClassificationId().getAbstractItem());
                    }
                    return items;
                }

            case FEED :
                displayOrder = feedRepository.findOne(ctx.getKeyId()).getDefaultDisplayOrder();
                filter = ItemPredicates.itemsClassOfClassification(ctx.getKeyId());
                builder.and(filter);
                List<ItemClassificationOrder> itemsClass = Lists.newArrayList(itemClassificationOrderRepository.findAll(builder, ItemPredicates.orderByClassifDefinition(displayOrder)));
                List<AbstractItem> items = Lists.newArrayList();
                for (ItemClassificationOrder item : itemsClass) {
                    items.add(item.getItemClassificationId().getAbstractItem());
                }
                return items;
            default : throw new IllegalArgumentException("The context Type " + ctx.getKeyType() + " is not managed") ;
        }
    }*/


}
