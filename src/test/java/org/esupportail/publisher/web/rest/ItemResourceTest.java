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
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.config.Constants;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.QUser;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.ObjTest;
import org.esupportail.publisher.repository.OrganizationRepository;
import org.esupportail.publisher.repository.RedactorRepository;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.ContentService;
import org.esupportail.publisher.service.FileService;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
import org.springframework.validation.Validator;

/**
 * Test class for the NewsResource REST controller.
 *
 * @see org.esupportail.publisher.web.rest.NewsResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ItemResourceTest {

    private static final String DEFAULT_TITLE = "SAMPLE_TEXT";
    private static final String UPDATED_TITLE = "UPDATED_TEXT";

    private static final String DEFAULT_SUMMARY = "SAMPLE_TEXT";
    private static final String UPDATED_SUMMARY = "UPDATED_TEXT";

    private static final String DEFAULT_ENCLOSURE = "http://un.domaine.fr/path/file.jpg";
    private static final String UPDATED_ENCLOSURE = "http://deux.domaine.fr/path/media.png";

    private static final LocalDate DEFAULT_END_DATE = LocalDate.now().plusDays(2);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now().plusMonths(1);

    private static final LocalDate DEFAULT_START_DATE = LocalDate.now();
    private static final LocalDate UPDATED_START_DATE = LocalDate.now().minusDays(1);

    private static final ItemStatus DEFAULT_STATUS = ItemStatus.DRAFT;
    private static final ItemStatus UPDATED_STATUS = ItemStatus.PENDING;

    private static final Boolean DEFAULT_RSS_ALLOWED = true;
    private static final Boolean UPDATED_RSS_ALLOWED = false;

    private static final Instant DEFAULT_VALIDATION_DATE = ObjTest.d1;
    private static final Instant UPDATED_VALIDATION_DATE = ObjTest.d1.plus(1, ChronoUnit.DAYS);

    private static final String DEFAULT_BODY = "SAMPLE_TEXT";
    private static final String UPDATED_BODY = "UPDATED_TEXT";

    @Inject
    private ItemRepository<AbstractItem> itemRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private Validator validator;

    @Autowired
    private EntityManager em;

    private MockMvc restNewsMockMvc;

    private AbstractItem item;

    @Inject
    private OrganizationRepository organizationRepository;
    private Organization organization;
    @Inject
    private RedactorRepository redactorRepository;
    private Redactor redactor;
    @Inject
    private ContentService contentService;


    @Inject
    private UserRepository userRepo;
    @Inject
    private UserDTOFactory userDTOFactory;
    private User user1;private User user2;private User user3;

    @Inject
    private IPermissionService permissionService;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ItemResource itemResource = new ItemResource();
        OrganizationResource organizationResource = new OrganizationResource();
        FileService fileservice = new FileService();
        RedactorResource redactorResource = new RedactorResource();
        ReflectionTestUtils.setField(itemResource, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemResource, "permissionService", permissionService);
        ReflectionTestUtils.setField(itemResource, "fileService", fileservice);
        ReflectionTestUtils.setField(organizationResource, "organizationRepository", organizationRepository);
        ReflectionTestUtils.setField(itemResource, "contentService", contentService);
        ReflectionTestUtils.setField(redactorResource, "redactorRepository", redactorRepository);
        this.restNewsMockMvc = MockMvcBuilders.standaloneSetup(itemResource, organizationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            //.setControllerAdvice(exceptionTranslator)
            .setConversionService(TestUtil.createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator)
            .build();

        Optional<User> optionalUser = userRepo.findOne(QUser.user.login.like("system"));
        User userPart = optionalUser.orElse(null);
        UserDTO userDTOPart = userDTOFactory.from(userPart);
        CustomUserDetails userDetails = new CustomUserDetails(userDTOPart, userPart, Lists.newArrayList(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN)));
        Authentication authentication = new TestingAuthenticationToken(userDetails, "password", Lists.newArrayList(userDetails.getAuthorities()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Before
    public void initTest() {
        final String name = "NAME";
        organization = organizationRepository.saveAndFlush(ObjTest.newOrganization(name));
        redactor = redactorRepository.saveAndFlush(ObjTest.newRedactor(name));
        user1 = userRepo.findById(ObjTest.subject1).get();
        user2 = userRepo.findById(ObjTest.subject2).get();
        user3 = userRepo.findById(ObjTest.subject3).get();



        News news = new News();
        news.setTitle(DEFAULT_TITLE);
        news.setSummary(DEFAULT_SUMMARY);
        news.setEnclosure(DEFAULT_ENCLOSURE);
        news.setEndDate(DEFAULT_END_DATE);
        news.setStartDate(DEFAULT_START_DATE);
        news.setStatus(DEFAULT_STATUS);
        news.setValidatedDate(DEFAULT_VALIDATION_DATE);
        news.setValidatedBy(user1);
        news.setBody(DEFAULT_BODY);
        news.setRssAllowed(DEFAULT_RSS_ALLOWED);
        news.setOrganization(organization);
        news.setRedactor(redactor);

        item = news;
    }

    @Test
    @Transactional
    public void createItem() throws Exception {
        // Validate the database is empty
        assertThat(itemRepository.findAll()).hasSize(0);

        // Create the Item
        restNewsMockMvc.perform(
            post("/api/items").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(item))).andExpect(status().isCreated());

        // Validate the News in the database
        List<AbstractItem> items = itemRepository.findAll();
        assertThat(items).hasSize(1);
        AbstractItem item = items.iterator().next();
        org.junit.Assert.assertThat(item, instanceOf(News.class));
        News testNews = (News) item;
        assertThat(testNews.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testNews.getSummary()).isEqualTo(DEFAULT_SUMMARY);
        assertThat(testNews.getEnclosure()).isEqualTo(DEFAULT_ENCLOSURE);
        assertThat(testNews.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testNews.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testNews.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testNews.getValidatedDate()).isEqualTo(DEFAULT_VALIDATION_DATE);
        assertThat(testNews.getValidatedBy().getLogin()).isEqualTo(ObjTest.subject1);
        assertThat(testNews.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testNews.isRssAllowed()).isEqualTo(DEFAULT_RSS_ALLOWED);
        assertThat(testNews.getRedactor()).isEqualTo(redactor);
        assertThat(testNews.getOrganization()).isEqualTo(organization);

    }

    @Test
    @Transactional
    public void getAllItems() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the Items
        restNewsMockMvc
            .perform(get("/api/items")).andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(item.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY)))
            .andExpect(jsonPath("$.[*].enclosure").value(hasItem(DEFAULT_ENCLOSURE)))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.getName())))
            .andExpect(jsonPath("$.[*].validatedBy.subject.keyId").value(hasItem(ObjTest.subject1)))
            .andExpect(jsonPath("$.[*].validatedDate").value(
                hasItem(DEFAULT_VALIDATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].rssAllowed").value(hasItem(DEFAULT_RSS_ALLOWED)))
            .andExpect(jsonPath("$.[*].organization.id").value(hasItem(organization.getId().intValue())))
            .andExpect(jsonPath("$.[*].redactor.id").value(hasItem(redactor.getId().intValue())));
    }
    @Test
    @Transactional
    public void getAllItemsOfStatusOfUser() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the Items of Status
        restNewsMockMvc
            .perform(get("/api/items/").requestAttr("item_status", DEFAULT_STATUS.getId()).requestAttr("owned", true))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[*].createdBy.subject.keyId").value(hasItem(Constants.SYSTEM_ACCOUNT)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(item.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY)))
            .andExpect(jsonPath("$.[*].enclosure").value(hasItem(DEFAULT_ENCLOSURE)))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.getName())))
            .andExpect(jsonPath("$.[*].validatedBy.subject.keyId").value(hasItem(ObjTest.subject1)))
            .andExpect(jsonPath("$.[*].validatedDate").value(
                hasItem(DEFAULT_VALIDATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].rssAllowed").value(DEFAULT_RSS_ALLOWED))
            .andExpect(jsonPath("$.[*].organization.id").value(hasItem(organization.getId().intValue())))
            .andExpect(jsonPath("$.[*].redactor.id").value(hasItem(redactor.getId().intValue())));
    }


    @Test
    @Transactional
    public void getItems() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get the item
        restNewsMockMvc
            .perform(get("/api/items/{id}", item.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(item.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY))
            .andExpect(jsonPath("$.enclosure").value(DEFAULT_ENCLOSURE))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.getName()))
            .andExpect(jsonPath("$.validatedBy.subject.keyId").value(ObjTest.subject1))
            .andExpect(jsonPath("$.validatedDate").value(
                    DEFAULT_VALIDATION_DATE.toString()))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY))
            .andExpect(jsonPath("$.rssAllowed").value(DEFAULT_RSS_ALLOWED))
            .andExpect(jsonPath("$.organization.id").value(organization.getId().intValue()))
            .andExpect(jsonPath("$.redactor.id").value(redactor.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingItem() throws Exception {
        // Get the news
        restNewsMockMvc.perform(get("/api/items/{id}", 1L)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateItem() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        int databaseSizeBeforeUpdate = itemRepository.findAll().size();

        AbstractItem updatedItem = itemRepository.findById(item.getId()).get();
        em.detach(updatedItem);
        // Update the news
        updatedItem.setTitle(UPDATED_TITLE);
        updatedItem.setSummary(UPDATED_SUMMARY);
        updatedItem.setEnclosure(UPDATED_ENCLOSURE);
        updatedItem.setEndDate(UPDATED_END_DATE);
        updatedItem.setStartDate(UPDATED_START_DATE);
        updatedItem.setStatus(UPDATED_STATUS);
        updatedItem.setRssAllowed(UPDATED_RSS_ALLOWED);
        updatedItem.setValidatedDate(UPDATED_VALIDATION_DATE);
        updatedItem.setValidatedBy(user2);
        ((News) updatedItem).setBody(UPDATED_BODY);
        restNewsMockMvc.perform(
            put("/api/items").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(updatedItem))).andDo(print()).andExpect(status().isOk());

        // Validate the News in the database
        List<AbstractItem> items = itemRepository.findAll();
        assertThat(items).hasSize(databaseSizeBeforeUpdate);
        AbstractItem item = items.get((items.size() - 1));
        org.junit.Assert.assertThat(item, instanceOf(News.class));
        News testNews = (News) item;
        assertThat(testNews.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testNews.getSummary()).isEqualTo(UPDATED_SUMMARY);
        assertThat(testNews.getEnclosure()).isEqualTo(UPDATED_ENCLOSURE);
        assertThat(testNews.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testNews.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testNews.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testNews.getValidatedDate()).isEqualTo(UPDATED_VALIDATION_DATE);
        assertThat(testNews.getValidatedBy().getLogin()).isEqualTo(user2.getLogin());
        assertThat(testNews.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testNews.isRssAllowed()).isEqualTo(UPDATED_RSS_ALLOWED);
        assertThat(testNews.getRedactor()).isEqualTo(redactor);
        assertThat(testNews.getOrganization()).isEqualTo(organization);
    }

    @Test
    @Transactional
    public void deleteItem() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        int databaseSizeBeforeDelete = itemRepository.findAll().size();

        // Get the news
        restNewsMockMvc.perform(delete("/api/items/{id}", item.getId()).accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<AbstractItem> items = itemRepository.findAll();
        assertThat(items).hasSize(databaseSizeBeforeDelete - 1);
    }
}
