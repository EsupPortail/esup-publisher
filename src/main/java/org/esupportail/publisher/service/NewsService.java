package org.esupportail.publisher.service;


import java.io.Serializable;
import java.util.ArrayList;
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

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.PublisherStructureTree;
import org.esupportail.publisher.domain.ReadingIndincator;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.enums.StringEvaluationMode;
import org.esupportail.publisher.domain.evaluators.UserAttributesEvaluator;
import org.esupportail.publisher.domain.evaluators.UserMultivaluedAttributesEvaluator;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.ReadingIndincatorRepository;
import org.esupportail.publisher.repository.externals.ldap.LdapUserDaoImpl;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.repository.predicates.PublisherPredicates;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.UserDetailsService;
import org.esupportail.publisher.service.evaluators.IEvaluationFactory;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.ItemVOFactory;
import org.esupportail.publisher.service.factories.RubriqueVOFactory;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.esupportail.publisher.web.rest.vo.ActualiteExtended;
import org.esupportail.publisher.web.rest.vo.ItemVO;
import org.esupportail.publisher.web.rest.vo.RubriqueVO;
import org.esupportail.publisher.web.rest.vo.VisibilityRegular;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
@Transactional
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

    @Inject
    private ReadingIndincatorRepository readingIndincatorRepository;

    @Inject
    private UserDetailsService userDetailsService;

    @Inject
    private ItemRepository<AbstractItem> itemRepository;


    public Actualite getNewsByUserOnContext(Long readerId, Boolean reading,
        HttpServletRequest request) throws Exception {

        final CustomUserDetails user = this.userDetailsService.loadUserByUsername(
            SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

        if (user == null) {
            throw new ObjectNotFoundException(user, CustomUserDetails.class);
        }

        List<PublisherStructureTree> publisherStructureTreeList = this.generateNewsTreeByReader(readerId, request);


        Set<ItemVO> itemVOSet = new HashSet<>();
        Set<RubriqueVO> rubriqueVOSet = new HashSet<>();
        Set<String> sources = new HashSet<>();

        Map<String, Boolean> readingIndincators = this.readingIndincatorRepository.findAllByUserId(
            user.getUser().getLogin()).stream().collect(
            Collectors.toMap(indicator -> indicator.getItem().getId().toString(),
                ReadingIndincator::isRead));

        publisherStructureTreeList.forEach(publisherStructureTree -> {

            publisherStructureTree.getActualites().forEach(actualite -> {

                actualite.getItems().forEach(itemVO -> {

                    if (reading == null || (reading ?
                        (readingIndincators.containsKey(itemVO.getUuid()) && readingIndincators.get(
                            itemVO.getUuid()).equals(true)) :
                        (!readingIndincators.containsKey(itemVO.getUuid()) || readingIndincators.get(
                            itemVO.getUuid()).equals(false))
                    )) {

                        Map<String, RubriqueVO> rubriquesMap = actualite.getRubriques().stream().collect(
                            Collectors.toMap(RubriqueVO::getUuid, rubriqueVO -> rubriqueVO));

                        itemVO.getVisibility().getObliged().forEach(obliged -> {

                            if (obliged instanceof VisibilityRegular) {

                                VisibilityRegular visibilityRegular = (VisibilityRegular) obliged;

                                UserMultivaluedAttributesEvaluator umae = new UserMultivaluedAttributesEvaluator(
                                    "isMemberOf", visibilityRegular.getValue(), StringEvaluationMode.CONTAINS);

                                UserAttributesEvaluator uae = new UserAttributesEvaluator("uid",
                                    visibilityRegular.getValue(), StringEvaluationMode.EQUALS);

                                if (("uid".equals(visibilityRegular.getAttribute()) && evalFactory.from(
                                    uae).isApplicable(
                                    user.getUser())) || ("isMemberOf".equals(
                                    visibilityRegular.getAttribute()) && evalFactory.from(
                                    umae).isApplicable(user.getUser()))) {


                                    itemVO.getArticle().setLink(
                                        itemVO.getArticle().getLink().replaceAll("\\/view", "/news"));

                                    if (itemVO.getArticle().getEnclosure() != null) {

                                        Pattern pattern = Pattern.compile(".*?(\\/files.*)");
                                        Matcher matcher = pattern.matcher(itemVO.getArticle().getEnclosure());
                                        if (matcher.find()) {
                                            itemVO.getArticle().setEnclosure(matcher.group(1));
                                        }
                                    }
                                    itemVO.setSource(
                                        publisherStructureTree.getPublisher().getContext().getOrganization().getDisplayName());
                                    sources.add(itemVO.getSource());
                                    itemVOSet.add(itemVO);
                                    itemVO.getRubriques().forEach(
                                        r -> rubriqueVOSet.add(rubriquesMap.get(r.toString())));
                                }
                            }
                        });
                    }
                });
            });
        });

        ActualiteExtended actualite = new ActualiteExtended();
        actualite.getItems().addAll(itemVOSet);
        actualite.getItems().sort(Comparator.comparing(ItemVO::getCreatedDate).reversed());
        actualite.getRubriques().addAll(rubriqueVOSet);
        actualite.getSources().addAll(sources);
        Collections.sort(actualite.getSources());
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

    public Map<String, Boolean> getAllReadindInfosForCurrentUser() {

        final CustomUserDetails user = this.userDetailsService.loadUserByUsername(
            SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

        return this.readingIndincatorRepository.findAllByUserId(user.getUser().getLogin()).stream().collect(
            Collectors.toMap(indicator -> indicator.getItem().getId().toString(),
                ReadingIndincator::isRead));
    }

    public void readingManagement(Long id, boolean isRead) throws ObjectNotFoundException {

        final CustomUserDetails user = this.userDetailsService.loadUserByUsername(
            SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        Optional<AbstractItem> optionnalItem = itemRepository.findOne(
            ItemPredicates.ItemWithStatus(id, ItemStatus.PUBLISHED, null));

        if (isRead) {
            if (!this.readingIndincatorRepository.existsByItemIdAndUserId(id, user.getUser().getLogin())) {

                if (optionnalItem.isPresent()) {
                    this.readingIndincatorRepository.save(
                        new ReadingIndincator(optionnalItem.get(), user.getInternalUser().getLogin(), true, 1));
                } else {
                    throw new ObjectNotFoundException(id, AbstractItem.class);
                }
            } else {
                this.readingIndincatorRepository.readingManagement(id, user.getInternalUser().getLogin(), true);
                this.readingIndincatorRepository.incrementReadingCounter(id, user.getInternalUser().getLogin());
            }
        } else {
            if (this.readingIndincatorRepository.existsByItemIdAndUserId(id, user.getUser().getLogin())) {
                this.readingIndincatorRepository.readingManagement(id, user.getInternalUser().getLogin(), false);
            } else throw new ObjectNotFoundException(id, ReadingIndincator.class);
        }
    }

}
