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
import org.esupportail.publisher.domain.enums.ItemType;
import org.esupportail.publisher.domain.enums.StringEvaluationMode;
import org.esupportail.publisher.domain.evaluators.UserAttributesEvaluator;
import org.esupportail.publisher.domain.evaluators.UserMultivaluedAttributesEvaluator;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.*;
import org.esupportail.publisher.repository.externals.ldap.LdapUserDaoImpl;
import org.esupportail.publisher.repository.predicates.ClassificationPredicates;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.repository.predicates.PublisherPredicates;
import org.esupportail.publisher.service.evaluators.IEvaluationFactory;
import org.esupportail.publisher.service.factories.ItemVOFactory;
import org.esupportail.publisher.service.factories.RubriqueVOFactory;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.esupportail.publisher.web.rest.vo.ItemVO;
import org.esupportail.publisher.web.rest.vo.RubriqueVO;
import org.esupportail.publisher.web.rest.vo.VisibilityRegular;
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

        List<PublisherStructureTree> publisherStructureTreeList = this.generateNewsTreeByReader(readerId, request);

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

                            UserAttributesEvaluator uae = new UserAttributesEvaluator("uid", visibilityRegular.getValue(), StringEvaluationMode.EQUALS);

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

        List<PublisherStructureTree> publisherStructureTreeList = new ArrayList<>();

        publishers.forEach(publisher -> {

            PublisherStructureTree publisherStructureTree = new PublisherStructureTree();
            publisherStructureTree.setActualites(new ArrayList<>());
            publisherStructureTree.setPublisher(publisher);

            Actualite actualite = this.treeGenerationService.getActualiteByPublisher(publisher, request);

            publisherStructureTree.getActualites().add(actualite);

            publisherStructureTreeList.add(publisherStructureTree);

        });

        return publisherStructureTreeList;
    }
}
