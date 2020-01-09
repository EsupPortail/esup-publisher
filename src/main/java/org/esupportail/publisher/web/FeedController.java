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

import java.util.List;

import javax.inject.Inject;

import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.ItemClassificationOrder;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.repository.ClassificationRepository;
import org.esupportail.publisher.repository.ItemClassificationOrderRepository;
import org.esupportail.publisher.repository.OrganizationRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.service.factories.impl.PublisherRssFeedView;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by jgribonvald on 28/06/17.
 */
@Controller
@RequestMapping(FeedController.FEED_CONTROLLER_PATH)
@Slf4j
public class FeedController {

    public static final String FEED_CONTROLLER_PATH = "/feed";
    public static final String PRIVATE_RSS_METHOD_PATH = "/privaterss/";


    public static final String PRIVATE_RSS_FEED_URL_PATH = FEED_CONTROLLER_PATH + PRIVATE_RSS_METHOD_PATH;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private PublisherRepository publisherRepository;

    @Inject
    private ClassificationRepository<AbstractClassification> classificationRepository;

    @Inject
    private ItemClassificationOrderRepository itemClassificationOrderRepository;

    @RequestMapping(value = "/rss/{organization_id}", method = RequestMethod.GET, produces = "application/*")
    public ModelAndView getRssFeed(@PathVariable("organization_id") String id,
                                @RequestParam(value = "pid", required = false) Long publisherId,
                                @RequestParam(value = "cid", required = false) Long classifId) {
        log.debug("Entering getRssFeed with params : organization identifier={}, publisher_id={}, classification_id={}", id, publisherId, classifId);
        ModelAndView mav = getObjects(id, publisherId, classifId);
        mav.setViewName("publisherRssFeedView");
        return mav;
    }

    @RequestMapping(value = "/atom/{organization_id}", method = RequestMethod.GET, produces = "application/*")
    public ModelAndView getAtomFeed(@PathVariable("organization_id") String id,
                                @RequestParam(value = "pid", required = false) Long publisherId,
                                @RequestParam(value = "cid", required = false) Long classifId) {
        log.debug("Entering getAtomFeed with params : organization identifier={}, publisher_id={}, classification_id={}", id, publisherId, classifId);
        ModelAndView mav = getObjects(id, publisherId, classifId);
        mav.setViewName("publisherAtomFeedView");
        return mav;
    }

    //@PreAuthorize("hasIpAddress(@appIpVariableHolder.getIpRange())")
    @RequestMapping(value = FeedController.PRIVATE_RSS_METHOD_PATH + "{classification_id}", method = RequestMethod.GET, produces = "application/*")
    public ModelAndView getPrivateRssFeed(@PathVariable("classification_id") Long classifId) {
        log.debug("Entering getPrivateRssFeed with params : classification_id={}", classifId);
        ModelAndView mav = getAllObjects(classifId);
        mav.setViewName("publisherRssFeedView");
        return mav;
    }

    @Cacheable(value = "feed", key = "#id.concat('-').concat(#publisherId.toString()).concat('-').concat(#classifId.toString())")
    private ModelAndView getObjects(String id, Long publisherId, Long classifId) {
        ModelAndView mav = new ModelAndView();

        Organization org;
        try {
            long orgId = Long.parseLong(id);
            org = organizationRepository.getOne(orgId);
        } catch (NumberFormatException e) {
            org = organizationRepository.findByIdentifiers(id);
        }
        if (org == null) return mav;

        mav.addObject(PublisherRssFeedView.ORG_PARAM, org);
        BooleanBuilder builder = new BooleanBuilder(ItemPredicates.itemsClassOfOrganization(org));
        builder.and(ItemPredicates.OwnedItemsClassOfStatus(false, ItemStatus.PUBLISHED));
        builder.and(ItemPredicates.OwnedItemsClassOfRSSAllowed(true));
        OrderSpecifier<?> orderSpecifier = ItemPredicates.orderByClassifDefinition(DisplayOrderType.START_DATE);
        if (publisherId != null) {
            builder.and(ItemPredicates.itemsClassOfPublisher(publisherId));
            Publisher pub = publisherRepository.getOne(publisherId);
            if (pub != null) {
                mav.addObject(PublisherRssFeedView.PUB_PARAM, pub);
                orderSpecifier = ItemPredicates.orderByPublisherDefinition(pub.getDefaultDisplayOrder());
            }
        }
        if (classifId != null) {
            builder.and(ItemPredicates.itemsClassOfClassification(classifId));
            AbstractClassification classif = classificationRepository.getOne(classifId);
            if (classif != null) {
                mav.addObject(PublisherRssFeedView.CLASSIF_PARAM, classif);
                orderSpecifier = ItemPredicates.orderByClassifDefinition(classif.getDefaultDisplayOrder());
            }
        }
        log.debug("The sql filter to obtain items for the rss feed is {}", builder.toString());
        List<ItemClassificationOrder> items = Lists.newArrayList(itemClassificationOrderRepository.findAll(
            builder, orderSpecifier));
        log.debug("Rss feed wil show {} ItemClassificationOrder", items.size());

        mav.addObject(PublisherRssFeedView.ITEMS_PARAM, items);
        return mav;
    }

    @Cacheable(value = "feed", key = "#classifId.toString()")
    private ModelAndView getAllObjects(final Long classifId) {
        ModelAndView mav = new ModelAndView();

        AbstractClassification classif = classificationRepository.getOne(classifId);
        if (classif == null) {
            return mav;
        }

        mav.addObject(PublisherRssFeedView.ORG_PARAM, classif.getPublisher().getContext().getOrganization());
        mav.addObject(PublisherRssFeedView.PUB_PARAM, classif.getPublisher());
        mav.addObject(PublisherRssFeedView.CLASSIF_PARAM, classif);

        OrderSpecifier<?> orderSpecifier = ItemPredicates.orderByClassifDefinition(classif.getDefaultDisplayOrder());
        BooleanBuilder builder = new BooleanBuilder(ItemPredicates.itemsClassOfClassification(classifId));
        builder.and(ItemPredicates.OwnedItemsClassOfStatus(false, ItemStatus.PUBLISHED));

        log.debug("The sql filter to obtain items for the rss feed is {}", builder.toString());
        List<ItemClassificationOrder> items = Lists.newArrayList(itemClassificationOrderRepository.findAll(
            builder, orderSpecifier));
        log.debug("Rss feed wil show {} ItemClassificationOrder", items.size());

        mav.addObject(PublisherRssFeedView.ITEMS_PARAM, items);
        return mav;
    }


}
