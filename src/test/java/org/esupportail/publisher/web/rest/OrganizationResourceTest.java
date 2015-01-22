package org.esupportail.publisher.web.rest;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.QUser;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.repository.ObjTest;
import org.esupportail.publisher.repository.OrganizationRepository;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.OrganizationService;
import org.esupportail.publisher.service.bean.UserContextTree;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the OrganizationResource REST controller.
 *
 * @see OrganizationResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@Slf4j
public class OrganizationResourceTest {

	private static final String DEFAULT_NAME = "SAMPLE_TEXT";
	private static final String UPDATED_NAME = "UPDATED_TEXT";

	private static final String DEFAULT_DISPLAY_NAME = "SAMPLE_TEXT";
	private static final String UPDATED_DISPLAY_NAME = "UPDATED_TEXT";

	private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
	private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

	private static final Integer DEFAULT_DISPLAY_ORDER = 0;
	private static final Integer UPDATED_DISPLAY_ORDER = 2;

	private static final Boolean DEFAULT_ALLOW_NOTIFICATIONS = false;
	private static final Boolean UPDATED_ALLOW_NOTIFICATIONS = true;

	@Inject
	private OrganizationRepository organizationRepository;

    @Inject
    private IPermissionService permissionService;

	private MockMvc restOrganizationMockMvc;

	private Organization organization;

    @Inject
    private UserRepository userRepo;
    @Inject
    private UserDTOFactory userDTOFactory;

	@PostConstruct
	public void setup() {
		MockitoAnnotations.initMocks(this);
		OrganizationResource organizationResource = new OrganizationResource();
		UserContextTree userContextTree = new UserContextTree();
		OrganizationService organizationService = new OrganizationService();
		ReflectionTestUtils.setField(organizationResource,
				"organizationRepository", organizationRepository);
		ReflectionTestUtils.setField(organizationResource, "userSessionTree",
				userContextTree);
		ReflectionTestUtils.setField(organizationResource,
				"organizationService", organizationService);
		ReflectionTestUtils.setField(organizationService,
				"organizationRepository", organizationRepository);
        ReflectionTestUtils.setField(organizationResource,
            "permissionService", permissionService);
		this.restOrganizationMockMvc = MockMvcBuilders.standaloneSetup(
				organizationResource).build();

        User userPart = userRepo.findOne(QUser.user.login.like("system"));
        UserDTO userDTOPart = userDTOFactory.from(userPart);
        CustomUserDetails userDetails = new CustomUserDetails(userDTOPart, userPart, Lists.newArrayList(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN)));
        Authentication authentication = new TestingAuthenticationToken(userDetails, "password", Lists.newArrayList(userDetails.getAuthorities()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Before
	public void initTest() {
		organization = new Organization();
		organization.setName(DEFAULT_NAME);
		organization.setDisplayName(DEFAULT_DISPLAY_NAME);
		organization.setDescription(DEFAULT_DESCRIPTION);
		organization.setAllowNotifications(DEFAULT_ALLOW_NOTIFICATIONS);
	}

	@Test
	@Transactional
	public void createOrganization() throws Exception {
		// Validate the database is empty
		assertThat(organizationRepository.findAll()).hasSize(0);

		// Create the Organization
		restOrganizationMockMvc
				.perform(
						post("/api/organizations")
								.contentType(TestUtil.APPLICATION_JSON_UTF8)
								.content(
										TestUtil.convertObjectToJsonBytes(organization)))
				.andDo(print()).andExpect(status().isCreated());

		// Validate the Organization in the database
		List<Organization> organizations = organizationRepository.findAll();
		assertThat(organizations).hasSize(1);
		Organization testOrganization = organizations.iterator().next();
		assertThat(testOrganization.getName()).isEqualTo(DEFAULT_NAME);
		assertThat(testOrganization.getDisplayName()).isEqualTo(
				DEFAULT_DISPLAY_NAME);
		assertThat(testOrganization.getDescription()).isEqualTo(
				DEFAULT_DESCRIPTION);
		assertThat(testOrganization.getDisplayOrder()).isEqualTo(
				DEFAULT_DISPLAY_ORDER);
		assertThat(testOrganization.isAllowNotifications()).isEqualTo(
				DEFAULT_ALLOW_NOTIFICATIONS);
		;
	}

	@Test
	@Transactional
	public void getAllOrganizations() throws Exception {
		// Initialize the database
		organizationRepository.saveAndFlush(organization);

		// Get all the organizations
		restOrganizationMockMvc
				.perform(get("/api/organizations"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(
						jsonPath("$.[0].id").value(
								organization.getId().intValue()))
				.andExpect(
						jsonPath("$.[0].name").value(DEFAULT_NAME.toString()))
				.andExpect(
						jsonPath("$.[0].displayName").value(
								DEFAULT_DISPLAY_NAME.toString()))
				.andExpect(
						jsonPath("$.[0].description").value(
								DEFAULT_DESCRIPTION.toString()))
				.andExpect(
						jsonPath("$.[0].displayOrder").value(
								DEFAULT_DISPLAY_ORDER))
				.andExpect(
						jsonPath("$.[0].allowNotifications").value(
								DEFAULT_ALLOW_NOTIFICATIONS.booleanValue()));
	}

	@Test
	@Transactional
	public void getOrganization() throws Exception {
		// Initialize the database
		organizationRepository.saveAndFlush(organization);

		// Get the organization
		restOrganizationMockMvc
				.perform(
						get("/api/organizations/{id}",
								organization.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(
						jsonPath("$.id").value(organization.getId().intValue()))
				.andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
				.andExpect(
						jsonPath("$.displayName").value(
								DEFAULT_DISPLAY_NAME.toString()))
				.andExpect(
						jsonPath("$.description").value(
								DEFAULT_DESCRIPTION.toString()))
				.andExpect(
						jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
				.andExpect(
						jsonPath("$.allowNotifications").value(
								DEFAULT_ALLOW_NOTIFICATIONS.booleanValue()));
	}

	@Test
	@Transactional
	public void moveUpOrganization() throws Exception {
		// Initialize the database
		// Initialize the database
		organization = organizationRepository.saveAndFlush(organization);
		Organization o1 = ObjTest.newOrganization("1");
		o1.setDisplayOrder(1);
		o1 = organizationRepository.saveAndFlush(o1);
		Organization o2 = ObjTest.newOrganization("2");
		o2.setDisplayOrder(2);
		o2 = organizationRepository.saveAndFlush(o2);
		Organization o3 = ObjTest.newOrganization("3");
		o3.setDisplayOrder(3);
		o3 = organizationRepository.saveAndFlush(o3);

		// Create the Organization
		/*
		 * restOrganizationMockMvc .perform(
		 * post("/api/organizations/{id}",
		 * organization.getId()).contentType(
		 * TestUtil.APPLICATION_JSON_UTF8).content(
		 * TestUtil.convertObjectToJsonBytes(new MoveDTO(
		 * UPDATED_DISPLAY_ORDER)))) .andDo(print()).andExpect(status().isOk());
		 */
		// Create the Organization
		restOrganizationMockMvc
				.perform(
						put("/api/organizations/{id}",
								organization.getId()).contentType(
								TestUtil.APPLICATION_JSON_UTF8).param("pos",
								Integer.toString(UPDATED_DISPLAY_ORDER)))
				.andDo(print()).andExpect(status().isOk());

		// Validate the orders in the database
		for (Organization org : organizationRepository.findAll()) {
			log.debug(org.toString());
		}
		assertThat(
				organizationRepository.getOne(organization.getId())
						.getDisplayOrder()).isEqualTo(UPDATED_DISPLAY_ORDER);
		assertThat(organizationRepository.getOne(o1.getId()).getDisplayOrder())
				.isEqualTo(0);
		assertThat(organizationRepository.getOne(o2.getId()).getDisplayOrder())
				.isEqualTo(1);
		assertThat(organizationRepository.getOne(o3.getId()).getDisplayOrder())
				.isEqualTo(3);
	}

	@Test
	@Transactional
	public void getNonExistingOrganization() throws Exception {
		// Get the organization
		restOrganizationMockMvc
				.perform(get("/api/organizations/{id}", 1L)).andExpect(
						status().isNotFound());
	}

	@Test
	@Transactional
	public void updateOrganization() throws Exception {
		// Initialize the database
		organizationRepository.saveAndFlush(organization);

		// Update the organization
		organization.setName(UPDATED_NAME);
		organization.setDisplayName(UPDATED_DISPLAY_NAME);
		organization.setDescription(UPDATED_DESCRIPTION);
		organization.setDisplayOrder(UPDATED_DISPLAY_ORDER);
		organization.setAllowNotifications(UPDATED_ALLOW_NOTIFICATIONS);
		restOrganizationMockMvc.perform(
				put("/api/organizations").with(new RequestPostProcessor() {
                    @Override
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        request.setRemoteUser("admin");
                        return request;
                    }
                }).contentType(
						TestUtil.APPLICATION_JSON_UTF8).content(
						TestUtil.convertObjectToJsonBytes(organization)))
				.andExpect(status().isOk());

		// Validate the Organization in the database
		List<Organization> organizations = organizationRepository.findAll();
		assertThat(organizations).hasSize(1);
		Organization testOrganization = organizations.iterator().next();
		assertThat(testOrganization.getName()).isEqualTo(UPDATED_NAME);
		assertThat(testOrganization.getDisplayName()).isEqualTo(
				UPDATED_DISPLAY_NAME);
		assertThat(testOrganization.getDescription()).isEqualTo(
				UPDATED_DESCRIPTION);
		assertThat(testOrganization.getDisplayOrder()).isEqualTo(
				UPDATED_DISPLAY_ORDER);
		assertThat(testOrganization.isAllowNotifications()).isEqualTo(
				UPDATED_ALLOW_NOTIFICATIONS);
	}

	@Test
	@Transactional
	public void deleteOrganization() throws Exception {
		// Initialize the database
		organizationRepository.saveAndFlush(organization);

		// Get the organization
		restOrganizationMockMvc.perform(
				delete("/api/organizations/{id}", organization.getId())
						.accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(
				status().isOk());

		// Validate the database is empty
		List<Organization> organizations = organizationRepository.findAll();
		assertThat(organizations).hasSize(0);
	}
}
