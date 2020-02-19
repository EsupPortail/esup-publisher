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
package org.esupportail.publisher.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.AbstractPermission;
import org.esupportail.publisher.domain.Category;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.ItemClassificationOrder;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.PermissionOnContext;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.QUser;
import org.esupportail.publisher.domain.Reader;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.enums.OperatorType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.enums.StringEvaluationMode;
import org.esupportail.publisher.domain.enums.SubscribeType;
import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.esupportail.publisher.repository.CategoryRepository;
import org.esupportail.publisher.repository.ItemClassificationOrderRepository;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.ObjTest;
import org.esupportail.publisher.repository.OrganizationRepository;
import org.esupportail.publisher.repository.PermissionRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.ReaderRepository;
import org.esupportail.publisher.repository.RedactorRepository;
import org.esupportail.publisher.repository.SubscriberRepository;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.service.ContentService;
import org.esupportail.publisher.service.factories.ContentDTOFactory;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.ContentDTO;
import org.esupportail.publisher.web.rest.dto.SubscriberFormDTO;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by jgribonvald on 08/06/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Slf4j
public class ContentResourceTest {

    private static final String USER_ADMIN = "admin";
    private static final String USER = "user";

    private MockMvc restContentMockMvc;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private PublisherRepository publisherRepository;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private ReaderRepository readerRepository;

    @Inject
    private RedactorRepository redactorRepository;

    @Inject
    private ItemRepository<AbstractItem> itemRepository;

    @Inject
    private ItemClassificationOrderRepository itemClassificationOrderRepository;

    @Inject
    private ContentService contentService;
    @Inject
    private ContentDTOFactory contentDTOFactory;

    @Inject
    private SubscriberRepository subscriberRepository;

    @Inject
    private UserRepository userRepo;
    @Inject
    private UserDTOFactory userDTOFactory;

    @Inject
    @Named("permissionRepository")
    private PermissionRepository<AbstractPermission> permissionRepository;


    private Authentication authUserAdmin;
    private CustomUserDetails userAdminDetails;
    private Authentication authUserContributor;
    private CustomUserDetails userContributorDetails;
    private Authentication authUserSubLevelEditor;
    private CustomUserDetails userUserSubLevelEditorDetails;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ContentResource contentResource = new ContentResource();

        ReflectionTestUtils.setField(contentResource, "contentService", this.contentService);
        ReflectionTestUtils.setField(contentResource, "contentDTOFactory", this.contentDTOFactory);

        this.restContentMockMvc = MockMvcBuilders.standaloneSetup(contentResource).build();

        Optional<User> optionalUser = userRepo.findOne(QUser.user.login.like(USER_ADMIN));
        User userPart1 = optionalUser == null || !optionalUser.isPresent() ? null : optionalUser.get();
        Map<String, List<String>> userAttrs1 = Maps.newHashMap();
        userAttrs1.put("uid", Lists.newArrayList(USER_ADMIN));
        userAttrs1.put("ENTPersonJointure", Lists.newArrayList("ENT$INCONNU"));
        userAttrs1.put("mail", Lists.newArrayList(userPart1.getDisplayName()));
        userAttrs1.put("displayName",Lists.newArrayList("Admin User"));
        userAttrs1.put("isMemberOf", Lists.newArrayList("esco:Applications:Publication_contenus:ADMIN:Mon_ETAB1","esco:Etablissements:Mon_ETAB1:Profs"));
        UserDTO userDTOPart1 = new UserDTO(USER_ADMIN, userPart1.getDisplayName(), true, true, userPart1.getEmail(), userAttrs1);
        userAdminDetails = new CustomUserDetails(userDTOPart1, userPart1, Lists.newArrayList(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN)));
        authUserAdmin = new TestingAuthenticationToken(userAdminDetails, "password", Lists.newArrayList(userAdminDetails.getAuthorities()));

        Optional<User> optionalUser2 = userRepo.findOne(QUser.user.login.like(USER));
        User userPart2 = !optionalUser2.isPresent() ? null : optionalUser2.get();
        Map<String, List<String>> userAttrs2 = Maps.newHashMap();
        userAttrs2.put("uid", Lists.newArrayList(USER));
        userAttrs2.put("ENTPersonJointure", Lists.newArrayList("ENT$INCONNU"));
        userAttrs2.put("mail", Lists.newArrayList(userPart2.getDisplayName()));
        userAttrs2.put("displayName",Lists.newArrayList("Contributor User"));
        userAttrs2.put("isMemberOf", Lists.newArrayList("esco:Applications:Publication_contenus:USER:Mon_ETAB2", "esco:Etablissements:Mon_ETAB2:Profs"));
        UserDTO userDTOPart2 = new UserDTO(USER, userPart2.getDisplayName(), true, true, userPart2.getEmail(), userAttrs2);
        userContributorDetails = new CustomUserDetails(userDTOPart2, userPart2, Lists.newArrayList(new SimpleGrantedAuthority(AuthoritiesConstants.USER)));
        authUserContributor = new TestingAuthenticationToken(userContributorDetails, "password", Lists.newArrayList(userContributorDetails.getAuthorities()));

        Optional<User> optionalUser3 = userRepo.findOne(QUser.user.login.like(USER));
        User userPart3 = !optionalUser3.isPresent() ? null : optionalUser3.get();
        Map<String, List<String>> userAttrs3 = Maps.newHashMap();
        userAttrs3.put("uid", Lists.newArrayList(USER));
        userAttrs3.put("ENTPersonJointure", Lists.newArrayList("ENT$INCONNU"));
        userAttrs3.put("mail", Lists.newArrayList(userPart3.getDisplayName()));
        userAttrs3.put("displayName",Lists.newArrayList("Contributor User"));
        userAttrs3.put("isMemberOf", Lists.newArrayList("esco:Applications:Publication_contenus:USER:Mon_ETAB3", "esco:Etablissements:Mon_ETAB3:Profs"));
        UserDTO userDTOPart3 = new UserDTO(USER, userPart3.getDisplayName(), true, true, userPart3.getEmail(), userAttrs3);
        userUserSubLevelEditorDetails = new CustomUserDetails(userDTOPart3, userPart3, Lists.newArrayList(new SimpleGrantedAuthority(AuthoritiesConstants.USER)));
        authUserSubLevelEditor = new TestingAuthenticationToken(userUserSubLevelEditorDetails, "password", Lists.newArrayList(userUserSubLevelEditorDetails.getAuthorities()));
    }

    private Publisher publisher;
    private Organization organization;
    private Category category1;
    private Category category2;

    // not saved before test
    private ContentDTO content;

    @Before
    public void initTest()  throws Exception{
        organization = ObjTest.newOrganization("A Desc 1");
        organization.setIdentifiers(Sets.newHashSet("0450822X", "0370028X"));
        organization = organizationRepository.saveAndFlush(organization);

        PermissionOnContext perm = new PermissionOnContext(
            organization.getContextKey(),  PermissionType.CONTRIBUTOR,
            ObjTest.newGlobalEvaluatorOnGroup(OperatorType.OR, userContributorDetails.getUser().getAttributes().get("isMemberOf").get(0)));
        permissionRepository.saveAndFlush(perm);

        Set<AbstractEvaluator> evaluators = new HashSet<>();
        evaluators.add(ObjTest.newMultiValuedUserAttributeEvaluatorForAttr("isMemberOf","esco:Applications:Publication_contenus", StringEvaluationMode.STARTS_WITH));
        perm = new PermissionOnContext(
            organization.getContextKey(),  PermissionType.LOOKOVER,
            ObjTest.newOperatorEvaluator(OperatorType.OR, evaluators));
        permissionRepository.saveAndFlush(perm);

        Reader reader1 = ObjTest.newReader("1");
        reader1 = readerRepository.saveAndFlush(reader1);

        Redactor redactor1 = ObjTest.newRedactor("1");
        redactor1.setOptionalPublishTime(true);
        redactor1 = redactorRepository.saveAndFlush(redactor1);

        publisher = new Publisher(organization, reader1, redactor1, "PUB 1", PermissionClass.CONTEXT, true,true, true);
        publisher = publisherRepository.saveAndFlush(publisher);
        // number of cats in publisher publisher is needed in getItemsFromPublisherTest
        // NB important, à la une isn't persisted, it's hardcoded and should be considered
        category1 = categoryRepository.saveAndFlush(ObjTest.newCategory("Cat1", publisher));
        category2 = categoryRepository.saveAndFlush(ObjTest.newCategory("cat2", publisher));

        Subscriber sub = ObjTest.newSubscriberPerson(organization.getContextKey());
        sub.setSubscribeType(SubscribeType.FORCED);
        sub = subscriberRepository.saveAndFlush(sub);

        News news1 = ObjTest.newNews("news 1", organization, publisher.getContext().getRedactor());
        news1.setStartDate(LocalDate.now().minusDays(1));
        news1.setEndDate(LocalDate.now().plusMonths(1));
        news1.setStatus(ItemStatus.PUBLISHED);
        content = new ContentDTO();
        content.setItem(news1);
        content.setClassifications(Sets.newHashSet(category1.getContextKey(), category2.getContextKey()));
        content.setTargets(Lists.newArrayList(ObjTest.newSubscriberDTOGroup(), ObjTest.newSubscriberDTOPerson()));
    }

    @Test
    @Transactional
    public void testSimpleInsert() throws Exception {
        // Validate the database is empty
        assertThat(itemRepository.findAll()).hasSize(0);

        SecurityContextHolder.getContext().setAuthentication(authUserAdmin);

        // Create the Item
        restContentMockMvc.perform(
            post("/api/contents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(content))).andExpect(status().isCreated());

        // Validate the News in the database
        List<AbstractItem> items = itemRepository.findAll();
        assertThat(items).hasSize(1);
        AbstractItem item = items.iterator().next();
        org.junit.Assert.assertThat(item, instanceOf(News.class));
        News testNews = (News) item;

        assertThat(testNews.getTitle()).isEqualTo(content.getItem().getTitle());
        assertThat(testNews.getSummary()).isEqualTo(content.getItem().getSummary());
        assertThat(testNews.getEnclosure()).isEqualTo(content.getItem().getEnclosure());
        assertThat(testNews.getEndDate()).isEqualTo(content.getItem().getEndDate());
        assertThat(testNews.getStartDate()).isEqualTo(content.getItem().getStartDate());
        assertThat(testNews.getStatus()).isEqualTo(content.getItem().getStatus());
        //assertThat(testNews.getValidatedDate()).isEqualTo(content.getItem().getValidatedDate());
        assertThat(testNews.getValidatedBy().getLogin()).isEqualTo(USER_ADMIN);
        assertThat(testNews.getBody()).isEqualTo(((News)content.getItem()).getBody());
        assertThat(testNews.isRssAllowed()).isEqualTo(content.getItem().isRssAllowed());
        assertThat(testNews.isHighlight()).isEqualTo(content.getItem().isHighlight());
        assertThat(testNews.getRedactor()).isEqualTo(content.getItem().getRedactor());
        assertThat(testNews.getOrganization()).isEqualTo(content.getItem().getOrganization());

        List<ItemClassificationOrder> itemClassificationOrders =
            Lists.newArrayList(itemClassificationOrderRepository.findAll(ItemPredicates.itemsClassOfItem(item.getId())));
        assertThat(itemClassificationOrders).hasSize(2);
        Set<AbstractClassification> classifs = Sets.newHashSet();
        for (ItemClassificationOrder ico: itemClassificationOrders) {
            classifs.add(ico.getItemClassificationId().getAbstractClassification());
        }
        assertThat(classifs.containsAll(content.getClassifications()));
        assertThat(content.getClassifications().containsAll(classifs));
    }

    @Test
    @Transactional
    public void testOptionalDateWithoutEndInsert() throws Exception {
        // Validate the database is empty
        assertThat(itemRepository.findAll()).hasSize(0);

        SecurityContextHolder.getContext().setAuthentication(authUserAdmin);

        content.getItem().setEndDate(null);
        content.getItem().setStartDate(LocalDate.now().minusDays(1));

        // Create the Item
        restContentMockMvc.perform(
            post("/api/contents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(content))).andExpect(status().isCreated());

        // Validate the News in the database
        List<AbstractItem> items = itemRepository.findAll();
        assertThat(items).hasSize(1);
        AbstractItem item = items.iterator().next();
        org.junit.Assert.assertThat(item, instanceOf(News.class));
        News testNews = (News) item;

        assertThat(testNews.getEndDate()).isEqualTo(content.getItem().getEndDate());
        assertThat(testNews.getStartDate()).isEqualTo(content.getItem().getStartDate());
        assertThat(testNews.getStatus()).isEqualTo(ItemStatus.PUBLISHED);
    }

    @Test
    @Transactional
    public void testOptionalDateScheduledInsert() throws Exception {
        // Validate the database is empty
        assertThat(itemRepository.findAll()).hasSize(0);

        SecurityContextHolder.getContext().setAuthentication(authUserAdmin);

        content.getItem().setStartDate(LocalDate.now().plusDays(1));
        content.getItem().setEndDate(null);

        // Create the Item
        restContentMockMvc.perform(
            post("/api/contents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(content))).andExpect(status().isCreated());

        // Validate the News in the database
        List<AbstractItem> items = itemRepository.findAll();
        assertThat(items).hasSize(1);
        AbstractItem item = items.iterator().next();
        org.junit.Assert.assertThat(item, instanceOf(News.class));
        News testNews = (News) item;

        assertThat(testNews.getEndDate()).isEqualTo(content.getItem().getEndDate());
        assertThat(testNews.getStartDate()).isEqualTo(content.getItem().getStartDate());
        assertThat(testNews.getStatus()).isEqualTo(ItemStatus.SCHEDULED);
    }

    @Test
    @Transactional
    public void testOptionalDatePublishedOnEmptyClassifsInsert() throws Exception {
        // Validate the database is empty
        assertThat(itemRepository.findAll()).hasSize(0);

        SecurityContextHolder.getContext().setAuthentication(authUserAdmin);

        content.getItem().setStartDate(LocalDate.now().minusDays(7));
        content.getItem().setEndDate(LocalDate.now().plusDays(7));
        content.getItem().setStatus(ItemStatus.PUBLISHED);
        content.setTargets(new ArrayList<SubscriberFormDTO>());
        content.setClassifications(new HashSet<ContextKey>());

        // Create the Item
        restContentMockMvc.perform(
            post("/api/contents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(content))).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void testOptionalDateDraftOnDateBeforeInsert() throws Exception {
        // Validate the database is empty
        assertThat(itemRepository.findAll()).hasSize(0);

        SecurityContextHolder.getContext().setAuthentication(authUserAdmin);

        content.getItem().setStartDate(LocalDate.now().minusDays(7));
        content.getItem().setEndDate(LocalDate.now().minusDays(1));

        // Create the Item
        restContentMockMvc.perform(
            post("/api/contents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(content))).andExpect(status().isCreated());

        // Validate the News in the database
        List<AbstractItem> items = itemRepository.findAll();
        assertThat(items).hasSize(1);
        AbstractItem item = items.iterator().next();
        org.junit.Assert.assertThat(item, instanceOf(News.class));
        News testNews = (News) item;

        assertThat(testNews.getEndDate()).isEqualTo(content.getItem().getEndDate());
        assertThat(testNews.getStartDate()).isEqualTo(content.getItem().getStartDate());
        assertThat(testNews.getStatus()).isEqualTo(ItemStatus.DRAFT);
    }

    @Test
    @Transactional
    public void testOptionalDateDraftOnNotCompleteInsert() throws Exception {
        // Validate the database is empty
        assertThat(itemRepository.findAll()).hasSize(0);

        SecurityContextHolder.getContext().setAuthentication(authUserAdmin);

        content.getItem().setStartDate(LocalDate.now().minusDays(7));
        content.getItem().setEndDate(LocalDate.now().plusDays(7));
        content.getItem().setStatus(ItemStatus.DRAFT);
        content.setClassifications(null);

        // Create the Item
        restContentMockMvc.perform(
            post("/api/contents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(content))).andExpect(status().isCreated());

        // Validate the News in the database
        List<AbstractItem> items = itemRepository.findAll();
        assertThat(items).hasSize(1);
        AbstractItem item = items.iterator().next();
        org.junit.Assert.assertThat(item, instanceOf(News.class));
        News testNews = (News) item;

        assertThat(testNews.getEndDate()).isEqualTo(content.getItem().getEndDate());
        assertThat(testNews.getStartDate()).isEqualTo(content.getItem().getStartDate());
        assertThat(testNews.getStatus()).isEqualTo(ItemStatus.DRAFT);
    }

    @Test
    @Transactional
    public void testOptionalDateDraftOnEmptyTargetsInsert() throws Exception {
        // Validate the database is empty
        assertThat(itemRepository.findAll()).hasSize(0);

        SecurityContextHolder.getContext().setAuthentication(authUserAdmin);

        content.getItem().setStartDate(LocalDate.now().minusDays(7));
        content.getItem().setEndDate(LocalDate.now().plusDays(7));
        content.getItem().setStatus(ItemStatus.DRAFT);
        content.setTargets(null);

        // Create the Item
        restContentMockMvc.perform(
            post("/api/contents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(content))).andExpect(status().isCreated());

        // Validate the News in the database
        List<AbstractItem> items = itemRepository.findAll();
        assertThat(items).hasSize(1);
        AbstractItem item = items.iterator().next();
        org.junit.Assert.assertThat(item, instanceOf(News.class));
        News testNews = (News) item;

        assertThat(testNews.getEndDate()).isEqualTo(content.getItem().getEndDate());
        assertThat(testNews.getStartDate()).isEqualTo(content.getItem().getStartDate());
        assertThat(testNews.getStatus()).isEqualTo(ItemStatus.DRAFT);
    }

    @Test
    @Transactional
    public void testOptionalDateDraftOnSaveInsert() throws Exception {
        // Validate the database is empty
        assertThat(itemRepository.findAll()).hasSize(0);

        SecurityContextHolder.getContext().setAuthentication(authUserAdmin);

        content.getItem().setStartDate(LocalDate.now().minusDays(7));
        content.getItem().setEndDate(LocalDate.now().plusDays(7));
        content.getItem().setStatus(ItemStatus.DRAFT);

        // Create the Item
        restContentMockMvc.perform(
            post("/api/contents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(content))).andExpect(status().isCreated());

        // Validate the News in the database
        List<AbstractItem> items = itemRepository.findAll();
        assertThat(items).hasSize(1);
        AbstractItem item = items.iterator().next();
        org.junit.Assert.assertThat(item, instanceOf(News.class));
        News testNews = (News) item;

        assertThat(testNews.getEndDate()).isEqualTo(content.getItem().getEndDate());
        assertThat(testNews.getStartDate()).isEqualTo(content.getItem().getStartDate());
        assertThat(testNews.getStatus()).isEqualTo(ItemStatus.DRAFT);
    }

    @Test
    @Transactional
    public void testOptionalDatePendingOnUserInsert() throws Exception {

        // Validate the database is empty
        assertThat(itemRepository.findAll()).hasSize(0);

        SecurityContextHolder.getContext().setAuthentication(authUserContributor);

        // Create the Item
        restContentMockMvc.perform(
            post("/api/contents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(content))).andExpect(status().isCreated());

        // Validate the News in the database
        List<AbstractItem> items = itemRepository.findAll();
        assertThat(items).hasSize(1);
        AbstractItem item = items.iterator().next();
        org.junit.Assert.assertThat(item, instanceOf(News.class));
        News testNews = (News) item;

        assertThat(testNews.getStatus()).isEqualTo(ItemStatus.PENDING);
    }

    @Test
    @Transactional
    public void testOptionalDateFilteredClassifInsert() throws Exception {
        // Validate the database is empty
        assertThat(itemRepository.findAll()).hasSize(0);

        // This user is only editor on one category
        PermissionOnContext perm = new PermissionOnContext(
            category1.getContextKey(),  PermissionType.EDITOR,
            ObjTest.newGlobalEvaluatorOnGroup(OperatorType.OR, userUserSubLevelEditorDetails.getUser().getAttributes().get("isMemberOf").get(0)));
        permissionRepository.saveAndFlush(perm);

        SecurityContextHolder.getContext().setAuthentication(authUserSubLevelEditor);

        content.getItem().setStartDate(LocalDate.now().minusDays(7));
        content.getItem().setEndDate(LocalDate.now().plusDays(7));
        content.getItem().setStatus(ItemStatus.PUBLISHED);

        // Create the Item
        restContentMockMvc.perform(
            post("/api/contents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(content))).andExpect(status().isCreated());

        // Validate the News in the database
        List<AbstractItem> items = itemRepository.findAll();
        assertThat(items).hasSize(1);
        AbstractItem item = items.iterator().next();
        org.junit.Assert.assertThat(item, instanceOf(News.class));
        News testNews = (News) item;

        assertThat(testNews.getEndDate()).isEqualTo(content.getItem().getEndDate());
        assertThat(testNews.getStartDate()).isEqualTo(content.getItem().getStartDate());
        assertThat(testNews.getStatus()).isEqualTo(ItemStatus.PUBLISHED);

        List<ItemClassificationOrder> itemClassificationOrders =
            Lists.newArrayList(itemClassificationOrderRepository.findAll(ItemPredicates.itemsClassOfItem(item.getId())));
        assertThat(itemClassificationOrders).hasSize(1);
        Set<AbstractClassification> classifs = Sets.newHashSet();
        for (ItemClassificationOrder ico: itemClassificationOrders) {
            classifs.add(ico.getItemClassificationId().getAbstractClassification());
        }
        assertThat(classifs.contains(category1));
    }

    @Test
    @Transactional
    public void testOptionalDateNotAuthorizedInsert() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(authUserSubLevelEditor);

        // Create the Item
        restContentMockMvc.perform(
            post("/api/contents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(content))).andExpect(status().isForbidden());

    }

    @Test
    @Transactional
    public void testOptionalDateNotAuthorizedClassifInsert() throws Exception {
        // This user is only editor on one category
        PermissionOnContext perm = new PermissionOnContext(
            category1.getContextKey(),  PermissionType.EDITOR,
            ObjTest.newGlobalEvaluatorOnGroup(OperatorType.OR, userUserSubLevelEditorDetails.getUser().getAttributes().get("isMemberOf").get(0)));
        permissionRepository.saveAndFlush(perm);

        SecurityContextHolder.getContext().setAuthentication(authUserSubLevelEditor);

        content.getClassifications().clear();
        content.getClassifications().add(category2.getContextKey());

        content.getItem().setStartDate(LocalDate.now().minusDays(7));
        content.getItem().setEndDate(LocalDate.now().plusDays(7));
        content.getItem().setStatus(ItemStatus.PUBLISHED);

        // Create the Item
        restContentMockMvc.perform(
            post("/api/contents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(content))).andExpect(status().isForbidden());

    }

}
