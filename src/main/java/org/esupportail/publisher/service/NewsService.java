package org.esupportail.publisher.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysema.commons.lang.Pair;


import com.querydsl.core.BooleanBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.enums.StringEvaluationMode;
import org.esupportail.publisher.domain.evaluators.UserAttributesEvaluator;
import org.esupportail.publisher.domain.evaluators.UserMultivaluedAttributesEvaluator;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.*;
import org.esupportail.publisher.repository.externals.ldap.LdapUserDaoImpl;
import org.esupportail.publisher.repository.predicates.ClassificationPredicates;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.service.evaluators.IEvaluationFactory;
import org.esupportail.publisher.service.factories.ItemVOFactory;
import org.esupportail.publisher.service.factories.RubriqueVOFactory;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.esupportail.publisher.web.rest.vo.ItemVO;
import org.esupportail.publisher.web.rest.vo.RubriqueVO;
import org.esupportail.publisher.web.rest.vo.VisibilityRegular;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Slf4j
@Service
public class NewsService {

    @Inject
    private LdapUserDaoImpl ldapUserDao;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private ItemRepository itemRepository;

    @Inject
    private SubscriberRepository subscriberRepository;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private PublisherRepository publisherRepository;

    @Inject
    private RubriqueVOFactory rubriqueVOFactory;

    @Inject
    private ReaderRepository readerRepository;

    @Inject
    private HighlightedClassificationService highlightedClassificationService;

    @Inject
    private ItemClassificationOrderRepository itemClassificationOrderRepository;

    @Inject
    private LinkedFileItemRepository linkedFileItemRepository;

    @Inject
    private ItemVOFactory itemVOFactory;

    @Inject
    private SubscriberService subscriberService;

    @Inject
    private TreeGenerationService treeGenerationService;

    @Inject
    private IEvaluationFactory evalFactory;


    public Actualite getNewsByUserOnContext(
        Map<String, Object> payload,
        Long readerId,
        HttpServletRequest request){

        List<PublisherStructureTree> publisherStructureTreeList = this.treeGenerationService.generateNewsTreeByReader(readerId, request);

        IExternalUser user = ldapUserDao.getUserByUid(payload.get("sub").toString());
        UserDTO userDTO = new UserDTO(user.getId(), user.getDisplayName(), user.getEmail(), user.getAttributes());

        Set<ItemVO> itemVOSet = new HashSet<>();
        Set<RubriqueVO> rubriqueVOSet = new HashSet<>();

        publisherStructureTreeList.forEach(publisherStructureTree -> {

            publisherStructureTree.getActualites().forEach(actualite -> {

                actualite.getItems().forEach(itemVO -> {

                    Map<String, RubriqueVO> rubriquesMap = actualite.getRubriques().stream().collect(Collectors.toMap(RubriqueVO::getUuid, rubriqueVO -> rubriqueVO));

                    itemVO.getVisibility().getObliged().forEach(obliged -> {

                        if (obliged instanceof VisibilityRegular) {

                            VisibilityRegular visibilityRegular = (VisibilityRegular) obliged;

                            UserMultivaluedAttributesEvaluator umae = new UserMultivaluedAttributesEvaluator("isMemberOf", visibilityRegular.getValue(), StringEvaluationMode.CONTAINS);

                            UserAttributesEvaluator uae = new UserAttributesEvaluator(visibilityRegular.getAttribute(), visibilityRegular.getValue(), StringEvaluationMode.EQUALS);

                            if (("uid".equals(visibilityRegular.getAttribute()) && evalFactory.from(uae).isApplicable(userDTO)) || ("isMemberOf".equals(visibilityRegular.getAttribute()) && evalFactory.from(umae).isApplicable(userDTO))) {
                                itemVO.setSource(publisherStructureTree.getPublisher().getContext().getOrganization().getDisplayName());
                                itemVOSet.add(itemVO);
                                itemVO.getRubriques().forEach(r -> rubriqueVOSet.add(rubriquesMap.get(r.toString())));
                            }
                        }
                    });
                });
            });
        });
        Actualite actualite = new Actualite();
        actualite.getItems().addAll(itemVOSet);
        actualite.getItems().sort(Comparator.comparing(ItemVO::getCreatedDate).reversed());
        actualite.getRubriques().addAll(rubriqueVOSet);
        return actualite;
    }




    public void getRubriques(List<Publisher> publishers, Actualite actualites) {

        Set<AbstractClassification> rubriques = new HashSet<>();
        publishers.forEach(publisher -> {

            List<? extends AbstractClassification> categories = Lists.newArrayList(categoryRepository.findAll(ClassificationPredicates.CategoryOfPublisher(publisher.getId()), ClassificationPredicates.categoryOrderByDisplayOrderType(publisher.getDefaultDisplayOrder())));

            rubriques.addAll(categories);


            //TODO : Attention, demander comment gérer les highlight et si l'ordonnancement de la liste est bien utile ??
//            if (publisher.isDoHighlight()) {
//                final HighlightedClassification specialClassif = highlightedClassificationService.getClassification();
//                // to get the order of HighlightedClassification as first
//                actualites.getRubriques().addAll(Lists.newArrayList(rubriqueVOFactory.from(specialClassif)));
//                actualites.getRubriques().addAll(rubriqueVOFactory.asVOList(categories));
//            } else {
//                actualites.getRubriques().addAll(rubriqueVOFactory.asVOList(rubriques));
//            }

        });
        actualites.getRubriques().addAll(rubriqueVOFactory.asVOList(rubriques));
    }


    public void getItemsOnPublisherNewWay(List<Publisher> publishers, Actualite actualites, HttpServletRequest request) {

        Set<ItemClassificationOrder> set = new HashSet<>();
        actualites.setItems(new ArrayList<ItemVO>());
        publishers.forEach(publisher -> {

            BooleanBuilder builder = new BooleanBuilder();
            //final QItemClassificationOrder icoQ = QItemClassificationOrder.itemClassificationOrder;
            builder.and(ItemPredicates.itemsClassOfPublisher(publisher.getId()));
            builder.and(ItemPredicates.OwnedItemsClassOfStatus(null, ItemStatus.PUBLISHED));

            set.addAll(Lists.newArrayList(itemClassificationOrderRepository.findAll(builder, ItemPredicates.orderByClassifDefinition(publisher.getDefaultItemsDisplayOrder()))));

            List<ItemClassificationOrder> itemsClasss = Lists.newArrayList(itemClassificationOrderRepository.findAll(builder, ItemPredicates.orderByClassifDefinition(publisher.getDefaultItemsDisplayOrder())));

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
                actualites.getItems().add(itemVOFactory.from(item, entry.getValue().getSecond(), subscriberService.getDefinedSubscribersOfContext(item.getContextKey()), linkedFiles, request));
            }
        });

    }


    private Set<String> getOrganizationsFromPayload(Map<String, Object> payload) throws JsonProcessingException {


        String s = payload.get("ESCOSIREN").toString().substring(1, payload.get("ESCOSIREN").toString().length() - 1);

        String[] rawValues = s.split(",\\s*");
        Set<String> set = new HashSet<>(Arrays.asList(rawValues));
        set.add(payload.get("ESCOSIRENCourant").toString());
        System.out.println("list des organisations : " + set);
        return set;
    }
}
