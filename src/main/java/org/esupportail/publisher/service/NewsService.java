package org.esupportail.publisher.service;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.PublisherStructureTree;
import org.esupportail.publisher.domain.enums.StringEvaluationMode;
import org.esupportail.publisher.domain.evaluators.UserAttributesEvaluator;
import org.esupportail.publisher.domain.evaluators.UserMultivaluedAttributesEvaluator;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.externals.ldap.LdapUserDaoImpl;
import org.esupportail.publisher.repository.predicates.PublisherPredicates;
import org.esupportail.publisher.service.evaluators.IEvaluationFactory;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.ItemVOFactory;
import org.esupportail.publisher.service.factories.RubriqueVOFactory;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.esupportail.publisher.web.rest.vo.ItemVO;
import org.esupportail.publisher.web.rest.vo.RubriqueVO;
import org.esupportail.publisher.web.rest.vo.VisibilityRegular;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class NewsService {

    @Inject
    private LdapUserDaoImpl ldapUserDao;

    @Inject
    private PublisherRepository publisherRepository;

    @Inject
    private RubriqueVOFactory rubriqueVOFactory;

    @Inject
    private ItemVOFactory itemVOFactory;

    @Inject
    private TreeGenerationService treeGenerationService;

    @Inject
    private IEvaluationFactory evalFactory;


    public Actualite getNewsByUserOnContext(Map<String, Object> payload, Long readerId,
        HttpServletRequest request) throws Exception {

        List<PublisherStructureTree> publisherStructureTreeList = this.generateNewsTreeByReader(readerId, request);

        // Récupération de l'utilisateur courant, accession à ses autorisations
        IExternalUser user = ldapUserDao.getUserByUid(payload.get("sub").toString());

        if (user == null) {
            throw new ObjectNotFoundException((Serializable) user, IExternalUser.class);
        }

        UserDTO userDTO = new UserDTO(user.getId(), user.getDisplayName(), user.getEmail(), user.getAttributes());

        Set<ItemVO> itemVOSet = new HashSet<>();
        Set<RubriqueVO> rubriqueVOSet = new HashSet<>();

        publisherStructureTreeList.forEach(publisherStructureTree -> {

            publisherStructureTree.getActualites().forEach(actualite -> {

                actualite.getItems().forEach(itemVO -> {
                    Map<String, RubriqueVO> rubriquesMap = actualite.getRubriques().stream().collect(
                        Collectors.toMap(RubriqueVO::getUuid, rubriqueVO -> rubriqueVO));

                    itemVO.getVisibility().getObliged().forEach(obliged -> {

                        if (obliged instanceof VisibilityRegular) {

                            VisibilityRegular visibilityRegular = (VisibilityRegular) obliged;

                            UserMultivaluedAttributesEvaluator umae = new UserMultivaluedAttributesEvaluator(
                                "isMemberOf", visibilityRegular.getValue(), StringEvaluationMode.CONTAINS);

                            UserAttributesEvaluator uae = new UserAttributesEvaluator("uid",
                                visibilityRegular.getValue(), StringEvaluationMode.EQUALS);

                            if (("uid".equals(visibilityRegular.getAttribute()) && evalFactory.from(uae).isApplicable(
                                userDTO)) || ("isMemberOf".equals(visibilityRegular.getAttribute()) && evalFactory.from(
                                umae).isApplicable(userDTO))) {
                                itemVO.setSource(
                                    publisherStructureTree.getPublisher().getContext().getOrganization().getDisplayName());
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


    public List<PublisherStructureTree> generateNewsTreeByReader(Long readerId,
        final HttpServletRequest request) throws Exception {

        BooleanBuilder builder = new BooleanBuilder(PublisherPredicates.AllOfUsedState(true)).and(
            PublisherPredicates.AllOfReader(readerId));

        final List<Publisher> publishers = Lists.newArrayList(publisherRepository.findAll(builder));

        if (!publishers.isEmpty()) {
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
        } else {
            log.debug("Aucun publishers trouvé pour le reader {}", readerId);
            throw new ObjectNotFoundException((Serializable) publishers, Publisher.class);
        }
    }
}
