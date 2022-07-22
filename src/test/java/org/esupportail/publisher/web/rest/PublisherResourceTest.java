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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.OrganizationReaderRedactorKey;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.QUser;
import org.esupportail.publisher.domain.Reader;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.repository.ObjTest;
import org.esupportail.publisher.repository.OrganizationRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.ReaderRepository;
import org.esupportail.publisher.repository.RedactorRepository;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.PublisherService;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.ActionDTO;
import org.esupportail.publisher.web.rest.dto.UserDTO;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test class for the PublisherResource REST controller.
 *
 * @see PublisherResource
 */
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class PublisherResourceTest {

    private static final PermissionClass DEFAULT_PERMISSION_CLASS = PermissionClass.CONTEXT;
    private static final PermissionClass UPDATED_PERMISSION_CLASS = PermissionClass.CONTEXT_WITH_SUBJECTS;
    private static final Boolean DEFAULT_BOOLEAN = false;
    private static final Boolean UPDATED_BOOLEAN = true;
    private static final Integer DEFAULT_DISPLAY_ORDER = 0;
    private static final Integer UPDATED_DISPLAY_ORDER = 1;
    private static final DisplayOrderType DEFAULT_DEFAULT_DISPLAY_ORDER = DisplayOrderType.LAST_CREATED_MODIFIED_FIRST;
    private static final DisplayOrderType UPDATED_DEFAULT_DISPLAY_ORDER = DisplayOrderType.ONLY_LAST_CREATED_FIRST;
    private static final String DEFAULT_PUBLISHER_NAME = "My Publisher";
    private static final String UPDATED_PUBLISHER_NAME = "Your Publisher";

    @Inject
    private OrganizationRepository organizationRepository;
    @Inject
    private IPermissionService permissionService;
    @Inject
    private ReaderRepository readerRepository;
    @Inject
    private RedactorRepository redactorRepository;
    @Inject
    private PublisherRepository publisherRepository;
    @Inject
    private UserRepository userRepo;
    @Inject
    private UserDTOFactory userDTOFactory;

    private MockMvc restPublisherMockMvc;

    private Organization organization;
    private OrganizationReaderRedactorKey context;
    private Redactor redactor;
    private Reader reader;
    private Publisher publisher;


    @PostConstruct
    public void setup() {
        //closeable = MockitoAnnotations.openMocks(this);
        OrganizationResource organizationResource = new OrganizationResource();
        ReaderResource readerResource = new ReaderResource();
        RedactorResource redactorResource = new RedactorResource();
        PublisherResource publisherResource = new PublisherResource();
        PublisherService publisherService = new PublisherService();
        ReflectionTestUtils.setField(organizationResource, "organizationRepository", organizationRepository);
        ReflectionTestUtils.setField(readerResource, "readerRepository", readerRepository);
        ReflectionTestUtils.setField(redactorResource, "redactorRepository", redactorRepository);
        ReflectionTestUtils.setField(publisherResource, "publisherRepository", publisherRepository);
        ReflectionTestUtils.setField(publisherResource, "publisherService", publisherService);
        ReflectionTestUtils.setField(publisherResource, "permissionService", permissionService);
        ReflectionTestUtils.setField(publisherService, "publisherRepository", publisherRepository);
        this.restPublisherMockMvc = MockMvcBuilders.standaloneSetup(
            publisherResource, publisherService).build();

        Optional<User> optionalUser = userRepo.findOne(QUser.user.login.like("system"));
        User userPart = optionalUser.orElse(null);
        UserDTO userDTOPart = userDTOFactory.from(userPart);
        CustomUserDetails userDetails = new CustomUserDetails(userDTOPart, userPart, Lists.newArrayList(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN)));
        Authentication authentication = new TestingAuthenticationToken(userDetails, "password", Lists.newArrayList(userDetails.getAuthorities()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @BeforeEach
    public void initTest() {
        final String name = "NAME";
        organization = organizationRepository.saveAndFlush(ObjTest.newOrganization(name));
        reader = readerRepository.saveAndFlush(ObjTest.newReader(name));
        redactor = redactorRepository.saveAndFlush(ObjTest.newRedactor(name));

        context = new OrganizationReaderRedactorKey(organization, reader, redactor);
        publisher = new Publisher(organization, reader, redactor, DEFAULT_PUBLISHER_NAME,
            DEFAULT_PERMISSION_CLASS, DEFAULT_BOOLEAN, DEFAULT_BOOLEAN, DEFAULT_BOOLEAN);
        publisher.setDefaultDisplayOrder(DEFAULT_DEFAULT_DISPLAY_ORDER);
    }

    @Test
    @Transactional
    public void createPublisher() throws Exception {
        // Validate the database is empty
        assertThat(publisherRepository.findAll(), hasSize(0));

        // Create the Publisher
        restPublisherMockMvc.perform(
            post("/api/publishers").contentType(
                TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(publisher)))
            .andExpect(status().isCreated());

        // Validate the Publisher in the database
        List<Publisher> publishers = publisherRepository.findAll();
        assertThat(publishers, hasSize(1));
        Publisher testPublisher = publishers.iterator().next();
        assertThat(testPublisher.getDisplayName(), equalTo(DEFAULT_PUBLISHER_NAME));
        assertThat(testPublisher.getPermissionType(), equalTo(DEFAULT_PERMISSION_CLASS));
        assertThat(testPublisher.isUsed(), equalTo(DEFAULT_BOOLEAN));
        assertThat(testPublisher.isHasSubPermsManagement(), equalTo(DEFAULT_BOOLEAN));
        assertThat(testPublisher.isDoHighlight(), equalTo(DEFAULT_BOOLEAN));
        assertThat(testPublisher.getDisplayOrder(), equalTo(DEFAULT_DISPLAY_ORDER));
        assertThat(testPublisher.getDefaultDisplayOrder(), equalTo(DEFAULT_DEFAULT_DISPLAY_ORDER));
    }

    @Test
    @Transactional
    public void getAllPublishers() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publishers
        restPublisherMockMvc
            .perform(get("/api/publishers"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[0].id").value(publisher.getId().intValue()))
            .andExpect(jsonPath("$.[0].displayName").value(DEFAULT_PUBLISHER_NAME))
            .andExpect(jsonPath("$.[0].permissionType").value(DEFAULT_PERMISSION_CLASS.name()))
            .andExpect(jsonPath("$.[0].used").value(DEFAULT_BOOLEAN))
            .andExpect(jsonPath("$.[0].hasSubPermsManagement").value(DEFAULT_BOOLEAN))
            .andExpect(jsonPath("$.[0].doHighlight").value(DEFAULT_BOOLEAN))
            .andExpect(jsonPath("$.[0].displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.[0].defaultDisplayOrder").value(DEFAULT_DEFAULT_DISPLAY_ORDER.name()));
    }

    @Test
    @Transactional
    public void getPublisher() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get the publisher
        restPublisherMockMvc
            .perform(get("/api/publishers/{id}", publisher.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(publisher.getId().intValue()))
            .andExpect(jsonPath("$.displayName").value(DEFAULT_PUBLISHER_NAME))
            .andExpect(jsonPath("$.permissionType").value(DEFAULT_PERMISSION_CLASS.name()))
            .andExpect(jsonPath("$.used").value(DEFAULT_BOOLEAN))
            .andExpect(jsonPath("$.hasSubPermsManagement").value(DEFAULT_BOOLEAN))
            .andExpect(jsonPath("$.doHighlight").value(DEFAULT_BOOLEAN))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.defaultDisplayOrder").value(DEFAULT_DEFAULT_DISPLAY_ORDER.name()));
    }

    @Test
    @Transactional
    public void getNonExistingPublisher() throws Exception {
        // Get the publisher
        restPublisherMockMvc.perform(get("/api/publishers/{id}", 1L))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePublisher() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Update the publisher
        publisher.setDisplayName(UPDATED_PUBLISHER_NAME);
        publisher.setPermissionType(UPDATED_PERMISSION_CLASS);
        publisher.setUsed(UPDATED_BOOLEAN);
        publisher.setHasSubPermsManagement(UPDATED_BOOLEAN);
        publisher.setDoHighlight(UPDATED_BOOLEAN);
        publisher.setDisplayOrder(UPDATED_DISPLAY_ORDER);
        publisher.setDefaultDisplayOrder(UPDATED_DEFAULT_DISPLAY_ORDER);
        restPublisherMockMvc.perform(
            put("/api/publishers").contentType(
                TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(publisher)))
            .andExpect(status().isOk());

        // Validate the Publisher in the database
        List<Publisher> publishers = publisherRepository.findAll();
        assertThat(publishers, hasSize(1));
        Publisher testPublisher = publishers.iterator().next();
        assertThat(testPublisher.getDisplayName(), equalTo(UPDATED_PUBLISHER_NAME));
        assertThat(testPublisher.getPermissionType(), equalTo(UPDATED_PERMISSION_CLASS));
        assertThat(testPublisher.isUsed(), equalTo(UPDATED_BOOLEAN));
        assertThat(testPublisher.isHasSubPermsManagement(), equalTo(UPDATED_BOOLEAN));
        assertThat(testPublisher.isDoHighlight(), equalTo(UPDATED_BOOLEAN));
        assertThat(testPublisher.getDisplayOrder(), equalTo(UPDATED_DISPLAY_ORDER));
        assertThat(testPublisher.getDefaultDisplayOrder(), equalTo(UPDATED_DEFAULT_DISPLAY_ORDER));

    }

    @Test
    @Transactional
    public void updateUsePublisher() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        ActionDTO action = new ActionDTO();
        action.setObjectId(publisher.getId());
        action.setAttribute("used");
        action.setValue(UPDATED_BOOLEAN.toString());

        // Update the publisher
        restPublisherMockMvc.perform(
            put("/api/publishers").param("action", "modify").contentType(
                TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(action)))
            .andExpect(status().isOk());

        // Validate the Publisher in the database
        List<Publisher> publishers = publisherRepository.findAll();
        assertThat(publishers, hasSize(1));
        Publisher testPublisher = publishers.iterator().next();
        assertThat(testPublisher.getDisplayName(), equalTo(DEFAULT_PUBLISHER_NAME));
        assertThat(testPublisher.getPermissionType(), equalTo(DEFAULT_PERMISSION_CLASS));
        assertThat(testPublisher.isUsed(), equalTo(UPDATED_BOOLEAN));
        assertThat(testPublisher.isHasSubPermsManagement(), equalTo(DEFAULT_BOOLEAN));
        assertThat(testPublisher.isDoHighlight(), equalTo(DEFAULT_BOOLEAN));
        assertThat(testPublisher.getDisplayOrder(), equalTo(DEFAULT_DISPLAY_ORDER));
        assertThat(testPublisher.getDefaultDisplayOrder(), equalTo(DEFAULT_DEFAULT_DISPLAY_ORDER));
    }

    @Test
    @Transactional
    public void deletePublisher() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get the publisher
        restPublisherMockMvc.perform(
            delete("/api/publishers/{id}", publisher.getId()).accept(
                TestUtil.APPLICATION_JSON_UTF8)).andExpect(
            status().isOk());

        // Validate the database is empty
        List<Publisher> publishers = publisherRepository.findAll();
        assertThat(publishers, hasSize(0));
    }
}