package org.esupportail.publisher.web;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysema.query.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.enums.WritingMode;
import org.esupportail.publisher.repository.*;
import org.esupportail.publisher.repository.predicates.ClassificationPredicates;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.repository.predicates.PublisherPredicates;
import org.esupportail.publisher.service.SubscriberService;
import org.esupportail.publisher.service.factories.CategoryProfileFactory;
import org.esupportail.publisher.service.factories.FlashInfoVOFactory;
import org.esupportail.publisher.service.factories.ItemVOFactory;
import org.esupportail.publisher.service.factories.RubriqueVOFactory;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.esupportail.publisher.web.rest.vo.CategoryProfilesUrl;
import org.esupportail.publisher.web.rest.vo.FlashInfoVO;
import org.esupportail.publisher.web.rest.vo.ItemVO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Inject
    private ClassificationRepository<AbstractClassification> classificationRepository;

    @Inject
    private CategoryRepository categoryRepository;

//    @Inject
//    private FeedRepository<AbstractFeed> feedRepository;

    @Inject
    private FlashRepository flashRepository;

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
    private SubscriberService subscriberService;


    @RequestMapping(value = "/flash/{organization_uai}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<FlashInfoVO> getFlashInfo(@PathVariable("organization_uai") String uai) throws URISyntaxException {
        log.debug("Entering getFlashInfo with params : uai={}", uai);
        Organization org = organizationRepository.findByIdentifiers(uai);
        if (org != null) {
            DisplayOrderType displayOrder = DisplayOrderType.LAST_CREATED_MODIFIED_FIRST;
            BooleanBuilder builder = new BooleanBuilder(ItemPredicates.FlashItemsOfOrganization(org));
            builder.and(ItemPredicates.OwnedItemsOfStatus(false, ItemStatus.PUBLISHED.getId()));
            return flashInfoVOFactory.asVOList(Lists.newArrayList(flashRepository.findAll(builder, ItemPredicates.orderByItemDefinition(displayOrder))));
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
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Timed
    public CategoryProfilesUrl getAllPlublisherContext(@PathVariable("reader_id") Long readerId, @PathVariable("redactor_id") Long redactorId, HttpServletRequest request ) {
        log.debug("Entering getAllPlublisherContext with params :  reader_id={}, redactor_id={}", readerId, redactorId);
        BooleanBuilder builder = new BooleanBuilder(PublisherPredicates.AllOfUsedState(true))
            .and(PublisherPredicates.AllOfReader(readerId))
            .and(PublisherPredicates.AllOfRedactor(redactorId));
        List<Publisher> publishers = Lists.newArrayList(publisherRepository.findAll(builder));
        CategoryProfilesUrl cpu = new CategoryProfilesUrl();
        if (!publishers.isEmpty()) {
            Redactor redactor = publishers.get(0).getContext().getRedactor();
            String contextPath = !request.getContextPath().isEmpty() ? request.getContextPath() + "/" : "/";
            for (Publisher pub : publishers) {
                String urlCategory = null;
                String urlActualite = null;
                final String port = request.getServerPort() > -1 ? ":" + Integer.toString(request.getServerPort()) : "";
                if (redactor.getNbLevelsOfClassification() == 1 && WritingMode.TARGETS_ON_ITEM.equals(redactor.getWritingMode())) {
                    urlActualite =  request.getScheme() + "://" + request.getServerName() + port + contextPath + "published/items/" + pub.getId();
                } else {
                    urlCategory = request.getScheme() + "://" + request.getServerName() + port + contextPath + "published/categories/" + pub.getId();
                }
                cpu.getCategoryProfilesUrl().add(categoryProfileFactory.from(pub, subscriberService.getDefaultsSubscribersOfContext(pub.getContextKey()), urlActualite, urlCategory));
            }
        }
        return cpu;
    }

    @RequestMapping(value = "/items/{publisher_id}",
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Timed
    public Actualite getItemsFromPublisher(@PathVariable("publisher_id") Long publisherId) {
        //getting items on new way
        log.debug("Entering getItems with param : publisher_id={}", publisherId);
        Publisher publisher = publisherRepository.findOne(publisherId);

        return getItemsOnPulisherNewWay(publisher);
    }

    /*@RequestMapping(value = "/categories/{publisher_id}",
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @PreAuthorize("hasIpAddress(@appIpVariableHolder.getIpRange())")
    @Timed
    public Object getCategories(@PathVariable("publisher_id") Long publisherId) {
        //getting items on new way
        log.debug("Entering getCategories with param : publisher_id={}", publisherId);
        Publisher publisher = publisherRepository.findOne(publisherId);

        log.error("Warning this request isn't yet implemented !");

        return Lists.newArrayList();
    }*/




    private Object getItemsOfPublisher(@NotNull Publisher publisher) {
        if (publisher.getContext().getRedactor().getNbLevelsOfClassification() == 1
            && WritingMode.TARGETS_ON_ITEM.equals(publisher.getContext().getRedactor().getWritingMode())) {
            return getItemsOnPulisherNewWay(publisher);
        }
        log.error("Warning this request isn't yet implemented for !");
        return Lists.newArrayList();
    }


    //@Transactional
    private Actualite getItemsOnPulisherNewWay(@NotNull Publisher publisher) {
        log.debug("getItemsOnPulisherNewWay with publisher {}", publisher);
        Actualite returnedObj = new Actualite();
        BooleanBuilder builder = new BooleanBuilder();
        final QItemClassificationOrder icoQ = QItemClassificationOrder.itemClassificationOrder;
        builder.and(ItemPredicates.itemsClassOfPublisher(publisher.getId()));
        builder.and(ItemPredicates.OwnedItemsClassOfStatus(null, ItemStatus.PUBLISHED));

        List<? extends AbstractClassification> cts = Lists.newArrayList(categoryRepository.findAll(ClassificationPredicates.CategoryOfPublisher(publisher.getId()),
            ClassificationPredicates.categoryOrderByDisplayOrderType(publisher.getDefaultDisplayOrder())));
        log.debug("list of categories associated to publisher : {}", cts);

        List<ItemClassificationOrder> itemsClasss = Lists.newArrayList(itemClassificationOrderRepository.findAll(builder,
            ItemPredicates.orderByPublisherDefinition(publisher.getDefaultDisplayOrder()), ItemPredicates.orderByClassifDefinition(DisplayOrderType.LAST_CREATED_MODIFIED_FIRST)));
        log.debug("list of itemsClasss associated to publisher : {}", itemsClasss);
        if (!itemsClasss.isEmpty()) {
            //Set<AbstractClassification> categories = Sets.newHashSet();
            Map<Long, List<AbstractClassification>> itemsMap= Maps.newHashMap();

            for ( ItemClassificationOrder ico: itemsClasss) {
                final AbstractClassification classif = ico.getItemClassificationId().getAbstractClassification();
                //categories.add(classif);
                final Long itemId = ico.getItemClassificationId().getAbstractItem().getId();
                if (!itemsMap.containsKey(itemId)) {
                    itemsMap.put(itemId, Lists.newArrayList(classif));
                } else {
                    itemsMap.get(itemId).add(classif);
                }
            }
            returnedObj.setRubriques(rubriqueVOFactory.asVOList(cts));
            returnedObj.setItems(new ArrayList<ItemVO>());
            for (ItemClassificationOrder itemClass : itemsClasss) {
                final AbstractItem item = itemClass.getItemClassificationId().getAbstractItem();
                returnedObj.getItems().add(itemVOFactory.from(item, itemsMap.get(item.getId()),
                    subscriberService.getDefinedSubcribersOfContext(item.getContextKey())));
            }

        }

        /*List<Category> cts = Lists.newArrayList(categoryRepository.findAll(ClassificationPredicates.categoryOfPublisher(publisher.getId()),
            ClassificationPredicates.categoryOrderByDisplayOrderType(publisher.getDefaultDisplayOrder())));
        returnedObj.setRubriques(rubriqueVOFactory.asVOList(cts));
        returnedObj.setItems(new ArrayList<ItemVO>());
        Map<AbstractItem,List<Category>> map = Maps.newHashMap();
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
            subscriberService.getDefinedSubcribersOfContext(item.getItemClassificationId().getAbstractItem().getContextKey()));*/

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
