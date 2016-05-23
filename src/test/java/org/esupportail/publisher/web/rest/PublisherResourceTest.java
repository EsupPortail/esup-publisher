package org.esupportail.publisher.web.rest;

import com.google.common.collect.Lists;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.repository.*;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.PublisherService;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.ActionDTO;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PublisherResource REST controller.
 *
 * @see PublisherResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class PublisherResourceTest {

    private static final PermissionClass DEFAULT_PERMISSION_CLASS = PermissionClass.CONTEXT;
    private static final PermissionClass UPDATED_PERMISSION_CLASS = PermissionClass.CONTEXT_WITH_SUBJECTS;

    private static final Boolean DEFAULT_USED = false;
    private static final Boolean UPDATED_USED = true;
    private static final Integer DEFAULT_DISPLAY_ORDER = 0;
    private static final Integer UPDATED_DISPLAY_ORDER = 1;

    private static final DisplayOrderType DEFAULT_DEFAULT_DISPLAY_ORDER = DisplayOrderType.LAST_CREATED_MODIFIED_FIRST;
    private static final DisplayOrderType UPDATED_DEFAULT_DISPLAY_ORDER = DisplayOrderType.ONLY_LAST_CREATED_FIRST;

    @Inject
    private OrganizationRepository organizationRepository;
    private Organization organization;

    @Inject
    private IPermissionService permissionService;

    @Inject
    private ReaderRepository readerRepository;
    private Reader reader;

    @Inject
    private RedactorRepository redactorRepository;
    private Redactor redactor;

    @Inject
    private PublisherRepository publisherRepository;

    private MockMvc restPublisherMockMvc;

    private Publisher publisher;
    private OrganizationReaderRedactorKey context;

    @Inject
    private UserRepository userRepo;
    @Inject
    private UserDTOFactory userDTOFactory;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
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

        User userPart = userRepo.findOne(QUser.user.login.like("system"));
        UserDTO userDTOPart = userDTOFactory.from(userPart);
        CustomUserDetails userDetails = new CustomUserDetails(userDTOPart, userPart, Lists.newArrayList(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN)));
        Authentication authentication = new TestingAuthenticationToken(userDetails, "password", Lists.newArrayList(userDetails.getAuthorities()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Before
    public void initTest() {
        final String name = "NAME";
        organization = organizationRepository.saveAndFlush(ObjTest.newOrganization(name));
        reader = readerRepository.saveAndFlush(ObjTest.newReader(name));
        redactor = redactorRepository.saveAndFlush(ObjTest.newRedactor(name));

        context = new OrganizationReaderRedactorKey(organization, reader, redactor);
        publisher = new Publisher(organization, reader, redactor,
            DEFAULT_PERMISSION_CLASS, DEFAULT_USED, DEFAULT_USED);
        publisher.setDefaultDisplayOrder(DEFAULT_DEFAULT_DISPLAY_ORDER);
    }

    @Test
    @Transactional
    public void createPublisher() throws Exception {
        // Validate the database is empty
        assertThat(publisherRepository.findAll()).hasSize(0);

        // Create the Publisher
        restPublisherMockMvc.perform(
            post("/api/publishers").contentType(
                TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(publisher)))
            .andExpect(status().isCreated());

        // Validate the Publisher in the database
        List<Publisher> publishers = publisherRepository.findAll();
        assertThat(publishers).hasSize(1);
        Publisher testPublisher = publishers.iterator().next();
        assertThat(testPublisher.getPermissionType()).isEqualTo(
            DEFAULT_PERMISSION_CLASS);
        assertThat(testPublisher.isUsed()).isEqualTo(DEFAULT_USED);
        assertThat(testPublisher.isHasSubPermsManagement()).isEqualTo(DEFAULT_USED);
        assertThat(testPublisher.getDisplayOrder()).isEqualTo(
            DEFAULT_DISPLAY_ORDER);
        assertThat(testPublisher.getDefaultDisplayOrder()).isEqualTo(
            DEFAULT_DEFAULT_DISPLAY_ORDER);
        ;
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
            .andExpect(
                jsonPath("$.[0].id")
                    .value(publisher.getId().intValue()))
            .andExpect(
                jsonPath("$.[0].permissionType").value(
                    DEFAULT_PERMISSION_CLASS.name()))
            .andExpect(
                jsonPath("$.[0].used").value(
                    DEFAULT_USED.booleanValue()))
            .andExpect(
                jsonPath("$.[0].hasSubPermsManagement").value(
                    DEFAULT_USED.booleanValue()))
            .andExpect(
                jsonPath("$.[0].displayOrder").value(
                    DEFAULT_DISPLAY_ORDER))
            .andExpect(
                jsonPath("$.[0].defaultDisplayOrder").value(
                    DEFAULT_DEFAULT_DISPLAY_ORDER.name()));
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
            .andExpect(
                jsonPath("$.permissionType").value(
                    DEFAULT_PERMISSION_CLASS.name()))
            .andExpect(
                jsonPath("$.used").value(DEFAULT_USED.booleanValue()))
            .andExpect(
                jsonPath("$.hasSubPermsManagement").value(DEFAULT_USED.booleanValue()))
            .andExpect(
                jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(
                jsonPath("$.defaultDisplayOrder").value(
                    DEFAULT_DEFAULT_DISPLAY_ORDER.name()));
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
        publisher.setPermissionType(UPDATED_PERMISSION_CLASS);
        publisher.setUsed(UPDATED_USED);
        publisher.setHasSubPermsManagement(UPDATED_USED);
        publisher.setDisplayOrder(UPDATED_DISPLAY_ORDER);
        publisher.setDefaultDisplayOrder(UPDATED_DEFAULT_DISPLAY_ORDER);
        restPublisherMockMvc.perform(
            put("/api/publishers").contentType(
                TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(publisher)))
            .andExpect(status().isOk());

        // Validate the Publisher in the database
        List<Publisher> publishers = publisherRepository.findAll();
        assertThat(publishers).hasSize(1);
        Publisher testPublisher = publishers.iterator().next();
        assertThat(testPublisher.getPermissionType()).isEqualTo(
            UPDATED_PERMISSION_CLASS);
        assertThat(testPublisher.isUsed()).isEqualTo(UPDATED_USED);
        assertThat(testPublisher.isHasSubPermsManagement()).isEqualTo(UPDATED_USED);
        assertThat(testPublisher.getDisplayOrder()).isEqualTo(
            UPDATED_DISPLAY_ORDER);
        assertThat(testPublisher.getDefaultDisplayOrder()).isEqualTo(
            UPDATED_DEFAULT_DISPLAY_ORDER);

    }

    @Test
    @Transactional
    public void updateUsePublisher() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        ActionDTO action = new ActionDTO();
        action.setObjectId(publisher.getId());
        action.setAttribute("used");
        action.setValue(UPDATED_USED.toString());

        // Update the publisher
        restPublisherMockMvc.perform(
            put("/api/publishers").param("action", "modify").contentType(
                TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(action)))
            .andExpect(status().isOk());

        // Validate the Publisher in the database
        List<Publisher> publishers = publisherRepository.findAll();
        assertThat(publishers).hasSize(1);
        Publisher testPublisher = publishers.iterator().next();
        assertThat(testPublisher.getPermissionType()).isEqualTo(
            DEFAULT_PERMISSION_CLASS);
        assertThat(testPublisher.isUsed()).isEqualTo(UPDATED_USED);
        assertThat(testPublisher.isHasSubPermsManagement()).isEqualTo(DEFAULT_USED);
        assertThat(testPublisher.getDisplayOrder()).isEqualTo(
            DEFAULT_DISPLAY_ORDER);
        assertThat(testPublisher.getDefaultDisplayOrder()).isEqualTo(
            DEFAULT_DEFAULT_DISPLAY_ORDER);
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
        assertThat(publishers).hasSize(0);
    }
}
