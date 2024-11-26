package org.esupportail.publisher.service;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysema.commons.lang.Pair;
import com.querydsl.core.BooleanBuilder;
import lombok.Data;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.enums.ItemType;
import org.esupportail.publisher.repository.*;
import org.esupportail.publisher.repository.predicates.ClassificationPredicates;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.repository.predicates.PublisherPredicates;
import org.esupportail.publisher.service.factories.ItemVOFactory;
import org.esupportail.publisher.service.factories.RubriqueVOFactory;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@Service
public class TreeGenerationService {


    @Inject
    private ReaderRepository readerRepository;

    @Inject
    private PublisherRepository publisherRepository;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private RubriqueVOFactory rubriqueVOFactory;

    @Inject
    private ItemClassificationOrderRepository itemClassificationOrderRepository;

    @Inject
    private LinkedFileItemRepository linkedFileItemRepository;

    @Inject
    private ItemVOFactory itemVOFactory;

    @Inject
    private SubscriberService subscriberService;

    private final CacheManager cacheManager;



    @Cacheable(value = "publisherStructureTreeList", key = "#readerId")
    public List<PublisherStructureTree> generateNewsTreeByReader(Long readerId, final HttpServletRequest request) {
        BooleanBuilder readerBuilder = new BooleanBuilder();

        readerRepository.findAll().stream().filter(reader -> reader.getAuthorizedTypes().contains(ItemType.NEWS))
            .forEach(reader -> {
                readerBuilder.or(PublisherPredicates.AllOfReader(reader.getId()));
            });

        BooleanBuilder builder = new BooleanBuilder(PublisherPredicates.AllOfUsedState(true))
            .and(PublisherPredicates.AllOfReader(readerId));
        //.and(PublisherPredicates.AllOfRedactor(redactorId));
        final List<Publisher> publishers = Lists.newArrayList(publisherRepository.findAll(builder));

        System.out.println("Nombre de publishers : " + publishers.size());

        List<PublisherStructureTree> publisherStructureTreeList = new ArrayList<>();

        publishers.forEach(publisher -> {

            PublisherStructureTree publisherStructureTree = new PublisherStructureTree();
            publisherStructureTree.setActualites(new ArrayList<>());

            Actualite actualite = new Actualite();
            publisherStructureTree.setPublisher(publisher);

            List<? extends AbstractClassification> categories = Lists.newArrayList(categoryRepository.findAll(ClassificationPredicates.CategoryOfPublisher(publisher.getId()),
                ClassificationPredicates.categoryOrderByDisplayOrderType(publisher.getDefaultDisplayOrder())));

            actualite.getRubriques().addAll(rubriqueVOFactory.asVOList(categories));

            BooleanBuilder itemBuilder = new BooleanBuilder();
            //final QItemClassificationOrder icoQ = QItemClassificationOrder.itemClassificationOrder;
            itemBuilder.and(ItemPredicates.itemsClassOfPublisher(publisher.getId()));
            itemBuilder.and(ItemPredicates.OwnedItemsClassOfStatus(null, ItemStatus.PUBLISHED));
            //itemBuilder.and(ItemPredicates.excludeItemsWithEndDateBeforeNow());
            //itemBuilder.and(ItemPredicates.ItemsToRemove());


            List<ItemClassificationOrder> itemsClasss = Lists.newArrayList(itemClassificationOrderRepository.findAll(itemBuilder,
                ItemPredicates.orderByClassifDefinition(publisher.getDefaultItemsDisplayOrder())));

            Map<Long, Pair<AbstractItem, List<AbstractClassification>>> itemsMap = Maps.newLinkedHashMap();

            for (ItemClassificationOrder ico : itemsClasss) {
                final AbstractClassification classif = ico.getItemClassificationId().getAbstractClassification();
                //categories.add(classif);
                final Long itemId = ico.getItemClassificationId().getAbstractItem().getId();
                if (!itemsMap.containsKey(itemId)) {
                    itemsMap.put(itemId, new Pair<AbstractItem, List<AbstractClassification>>(ico.getItemClassificationId().getAbstractItem(), Lists.newArrayList(classif)));
                } else {
                    itemsMap.get(itemId).getSecond().add(classif);
                }
            }

            for (Map.Entry<Long, Pair<AbstractItem, List<AbstractClassification>>> entry : itemsMap.entrySet()) {
                final AbstractItem item = entry.getValue().getFirst();
                final List<LinkedFileItem> linkedFiles = linkedFileItemRepository.findByAbstractItemIdAndInBody(item.getId(), false);
                actualite.getItems().add(itemVOFactory.from(item, entry.getValue().getSecond(),
                    subscriberService.getDefinedSubscribersOfContext(item.getContextKey()), linkedFiles, request));
            }

            publisherStructureTree.getActualites().add(actualite);

            publisherStructureTreeList.add(publisherStructureTree);

        });
        Objects.requireNonNull(cacheManager.getCache("publisherStructureTreeList")).putIfAbsent(readerId, publisherStructureTreeList);

        return publisherStructureTreeList;
    }



}
