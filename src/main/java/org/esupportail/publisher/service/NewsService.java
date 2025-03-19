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


import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.esupportail.publisher.config.bean.CustomLdapProperties;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.ReadingIndicator;
import org.esupportail.publisher.domain.ReadingIndicator;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.enums.StringEvaluationMode;
import org.esupportail.publisher.domain.evaluators.UserAttributesEvaluator;
import org.esupportail.publisher.domain.evaluators.UserMultivaluedAttributesEvaluator;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.ReadingIndicatorRepository;
import org.esupportail.publisher.repository.externals.ldap.LdapUserDaoImpl;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.repository.predicates.PublisherPredicates;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.UserDetailsService;
import org.esupportail.publisher.service.bean.PublisherStructureTree;
import org.esupportail.publisher.service.evaluators.IEvaluationFactory;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.ItemVOFactory;
import org.esupportail.publisher.service.factories.RubriqueVOFactory;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.esupportail.publisher.web.rest.vo.ActualiteWSource;
import org.esupportail.publisher.web.rest.vo.ItemVO;
import org.esupportail.publisher.web.rest.vo.RubriqueVO;
import org.esupportail.publisher.web.rest.vo.VisibilityRegular;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;
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
    private NewsTreeGenerationService treeGenerationService;

    @Inject
    private IEvaluationFactory evalFactory;

    @Inject
    private ReadingIndicatorRepository readingIndicatorRepository;

    @Inject
    private UserDetailsService userDetailsService;

    @Inject
    private ItemRepository<AbstractItem> itemRepository;

    @Inject
    private CustomLdapProperties ldapProperties;

    /**
     * Retourne les actualités d'un utilisateur sur un reader donné
     * @param readerId ID du reader recherché
     * @param reading Filtre de lecture (true = lues, false = non lues, null = tout)
     */
    public Actualite getNewsByUserOnContext(Long readerId, Boolean reading,
                                            HttpServletRequest request) throws Exception {

        final CustomUserDetails user = this.userDetailsService.loadUserByUsername(
            SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

        Set<PublisherStructureTree> publisherStructureTreeList = this.generateNewsTreeByReader(readerId, request);

        Set<ItemVO> itemVOSet = new HashSet<>();
        Set<RubriqueVO> rubriqueVOSet = new HashSet<>();
        Set<String> sources = new HashSet<>();

        final Map<String, Boolean> readingIndincators = this.readingIndicatorRepository.findAll(
            user.getUser().getLogin()).stream().collect(
            Collectors.toMap(indicator -> indicator.getItem().getId().toString(),
                ReadingIndicator::isRead));

        publisherStructureTreeList.forEach(publisherStructureTree -> {

            publisherStructureTree.getActualite().getItems().forEach(itemVO -> {

                // Filtre lu = dans la liste des readingIndincators à true
                // Filtre non lu = pas dans la liste OU dans la liste des readingIndincators à false
                if (reading == null || (reading ?
                    (readingIndincators.containsKey(itemVO.getUuid()) && readingIndincators.get(
                        itemVO.getUuid())) :
                    (!readingIndincators.containsKey(itemVO.getUuid()) || !readingIndincators.get(
                        itemVO.getUuid()))
                )) {

                    final Map<String, RubriqueVO> rubriquesMap = publisherStructureTree.getActualite().getRubriques().stream().collect(
                        Collectors.toMap(RubriqueVO::getUuid, rubriqueVO -> rubriqueVO));

                    itemVO.getVisibility().getObliged().forEach(obliged -> {

                        if (obliged instanceof VisibilityRegular) {

                            VisibilityRegular visibilityRegular = (VisibilityRegular) obliged;

                            UserMultivaluedAttributesEvaluator umae = new UserMultivaluedAttributesEvaluator(
                                ldapProperties.getUserBranch().getGroupAttribute(), visibilityRegular.getValue(), StringEvaluationMode.CONTAINS);

                            UserAttributesEvaluator uae = new UserAttributesEvaluator(ldapProperties.getUserBranch().getIdAttribute(),
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

        ActualiteWSource actualite = new ActualiteWSource();
        actualite.getItems().addAll(itemVOSet);
        actualite.getItems().sort(Comparator.comparing(ItemVO::getPubDate).reversed());
        actualite.getRubriques().addAll(rubriqueVOSet);
        actualite.getSources().addAll(sources);
        Collections.sort(actualite.getSources());
        return actualite;
    }


    public Set<PublisherStructureTree> generateNewsTreeByReader(Long readerId,
        final HttpServletRequest request) throws Exception {

        BooleanBuilder builder = new BooleanBuilder(PublisherPredicates.AllOfUsedState(true)).and(
            PublisherPredicates.AllOfReader(readerId));

        final Set<Publisher> publishers = Sets.newHashSet(publisherRepository.findAll(builder));

        if (!publishers.isEmpty()) {
            Set<PublisherStructureTree> publisherStructureTreeList = new HashSet<>();

            publishers.forEach(publisher -> {

                final Actualite actualite = this.treeGenerationService.getActualiteByPublisher(publisher, request);

                final PublisherStructureTree publisherStructureTree = new PublisherStructureTree(publisher, actualite);

                publisherStructureTreeList.add(publisherStructureTree);
            });

            return publisherStructureTreeList;
        }

        log.debug("Aucun publisher trouvé pour le reader {}", readerId);
        throw new ObjectNotFoundException((Serializable) publishers, Publisher.class);

    }

    public Map<String, Boolean> getAllReadingInfosForCurrentUser() {

        final CustomUserDetails user = this.userDetailsService.loadUserByUsername(
            SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

        return this.readingIndicatorRepository.findAllByUserId(user.getUser().getLogin()).stream().collect(
            Collectors.toMap(indicator -> indicator.getItem().getId().toString(),
                ReadingIndicator::isRead));
    }

    public void readingManagement(Long id, boolean isRead) throws ObjectNotFoundException {

        final CustomUserDetails user = this.userDetailsService.loadUserByUsername(
            SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        Optional<AbstractItem> optionalItem = itemRepository.findOne(
            ItemPredicates.ItemWithStatus(id, ItemStatus.PUBLISHED, null));

        if (isRead) {
            if (!this.readingIndicatorRepository.existsByItemIdAndUserId(id, user.getUser().getLogin())) {
                if (optionalItem.isPresent()) {
                    this.readingIndicatorRepository.save(
                        new ReadingIndicator(optionalItem.get(), user.getInternalUser().getLogin(), true, 1));
                } else {
                    throw new ObjectNotFoundException(id, AbstractItem.class);
                }
            } else {
                this.readingIndicatorRepository.readingManagement(id, user.getInternalUser().getLogin(), true);
                this.readingIndicatorRepository.incrementReadingCounter(id, user.getInternalUser().getLogin());
            }
        } else {
            if (this.readingIndicatorRepository.existsByItemIdAndUserId(id, user.getUser().getLogin())) {
                this.readingIndicatorRepository.readingManagement(id, user.getInternalUser().getLogin(), false);
            } else throw new ObjectNotFoundException(id, ReadingIndicator.class);
        }
    }

}
