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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.repository.*;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.UserContextLoaderService;
import org.esupportail.publisher.security.UserContextLoaderServiceImpl;
import org.esupportail.publisher.service.bean.UserContextTree;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CategoryResource REST controller.
 *
 * @see CategoryResource
 */
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Slf4j
public class CategoryResourceTest {

	private static final AccessType DEFAULT_ACCESS_VIEW = AccessType.AUTHENTICATED;
	private static final AccessType UPDATED_ACCESS_VIEW = AccessType.AUTHORIZED;
	private static final DisplayOrderType DEFAULT_DEFAULT_DISPLAY_ORDER = DisplayOrderType.CUSTOM;
	private static final DisplayOrderType UPDATED_DEFAULT_DISPLAY_ORDER = DisplayOrderType.NAME;
	private static final String DEFAULT_NAME = "SAMPLE_TEXT";
	private static final String UPDATED_NAME = "UPDATED_TEXT";
	private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
	private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";
	private static final Integer DEFAULT_DISPLAY_ORDER = 0;
	private static final Integer UPDATED_DISPLAY_ORDER = 1;
	private static final String DEFAULT_ICON_URL = "http://default.app.fr/icon.ico";
	private static final String UPDATED_ICON_URL = "http://updated.app.fr/icon.ico";
	private static final Boolean DEFAULT_RSS_ALLOWED = false;
	private static final Boolean UPDATED_RSS_ALLOWED = true;
	private static final Integer DEFAULT_TTL = 1000;
	private static final Integer UPDATED_TTL = 10000;

    private static final Boolean DEFAULT_HIDDEN_IF_EMPTY = false;
    private static final Boolean UPDATED_HIDDEN_IF_EMPTY = true;
    private static final String USER_ADMIN = "admin";
    private static final String USER = "user";

	@Inject
	private CategoryRepository categoryRepository;
	@Inject
	private PublisherRepository publisherRepository;
	@Inject
	private OrganizationRepository organizationRepository;
	@Inject
	private ReaderRepository readerRepository;
	@Inject
	private RedactorRepository redactorRepository;
	@Inject
    private UserRepository userRepo;

	private MockMvc restCategoryMockMvc;

	private Organization organization;
	private Redactor redactor;
	private Reader reader;
	private Publisher publisher;
	private Category category;
	private Authentication authUserAdmin;
	private CustomUserDetails userAdminDetails;

	@PostConstruct
	public void setup() {
		//closeable = MockitoAnnotations.openMocks(this);
		CategoryResource categoryResource = new CategoryResource();
		OrganizationResource organizationResource = new OrganizationResource();
        UserContextLoaderService userSessionTreeLoader = new UserContextLoaderServiceImpl();
        UserContextTree userSessionTree = new UserContextTree();
		ReaderResource readerResource = new ReaderResource();
		RedactorResource redactorResource = new RedactorResource();
		PublisherResource publisherResource = new PublisherResource();
        ReflectionTestUtils.setField(userSessionTreeLoader, "userSessionTree", userSessionTree);
		ReflectionTestUtils.setField(categoryResource, "categoryRepository", categoryRepository);
		ReflectionTestUtils.setField(organizationResource, "organizationRepository", organizationRepository);
		ReflectionTestUtils.setField(readerResource, "readerRepository", readerRepository);
		ReflectionTestUtils.setField(redactorResource, "redactorRepository", redactorRepository);
		ReflectionTestUtils.setField(publisherResource, "publisherRepository", publisherRepository);
        ReflectionTestUtils.setField(categoryResource, "userSessionTreeLoader", userSessionTreeLoader);
		this.restCategoryMockMvc = MockMvcBuilders.standaloneSetup(categoryResource).build();

		Optional<User> optionalUser = userRepo.findOne(QUser.user.login.like(USER_ADMIN));
        User userPart1 = optionalUser.orElse(null);
		assertThat(userPart1, notNullValue());
        Map<String, List<String>> userAttrs1 = Maps.newHashMap();
        userAttrs1.put("uid", Lists.newArrayList(USER_ADMIN));
        userAttrs1.put("ENTPersonJointure", Lists.newArrayList("ENT$INCONNU"));
        userAttrs1.put("mail", Lists.newArrayList(userPart1.getDisplayName()));
        userAttrs1.put("displayName",Lists.newArrayList("Admin User"));
        userAttrs1.put("isMemberOf", Lists.newArrayList("esco:Applications:Publication_contenus:ADMIN:Mon_ETAB1","esco:Etablissements:Mon_ETAB1:Profs"));
        UserDTO userDTOPart1 = new UserDTO(USER_ADMIN, userPart1.getDisplayName(), true, true, userPart1.getEmail(), userAttrs1);
        userAdminDetails = new CustomUserDetails(userDTOPart1, userPart1, Lists.newArrayList(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN)));
        authUserAdmin = new TestingAuthenticationToken(userAdminDetails, "password", Lists.newArrayList(userAdminDetails.getAuthorities()));

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authUserAdmin);
	}

	@BeforeEach
	public void initTest() {
		final String name = "NAME";
		organization = organizationRepository.saveAndFlush(ObjTest.newOrganization(name));

		reader = readerRepository.saveAndFlush(ObjTest.newReader(name));

		redactor = redactorRepository.saveAndFlush(ObjTest.newRedactor(name));

		publisher = publisherRepository.saveAndFlush(new Publisher(organization, reader, redactor,
            "PUBLISHER", PermissionClass.CONTEXT, false, true, true));

		category = new Category();
		category.setAccessView(DEFAULT_ACCESS_VIEW);
		category.setDefaultDisplayOrder(DEFAULT_DEFAULT_DISPLAY_ORDER);
		category.setName(DEFAULT_NAME);
		category.setDescription(DEFAULT_DESCRIPTION);
		category.setDisplayOrder(DEFAULT_DISPLAY_ORDER);
		category.setIconUrl(DEFAULT_ICON_URL);
		category.setRssAllowed(DEFAULT_RSS_ALLOWED);
		category.setTtl(DEFAULT_TTL);
        category.setHiddenIfEmpty(DEFAULT_HIDDEN_IF_EMPTY);
		category.setPublisher(publisher);
	}

	@Test
	@Transactional
	public void createCategory() throws Exception {
		// Validate the database is empty
		assertThat(categoryRepository.findAll(), hasSize(0));

		// Create the Category
		restCategoryMockMvc.perform(
				post("/api/categorys").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
						TestUtil.convertObjectToJsonBytes(category))).andExpect(status().isCreated());

		// Validate the Category in the database
		List<Category> categorys = categoryRepository.findAll();
		assertThat(categorys, hasSize(1));
		Category testCategory = categorys.iterator().next();
		assertThat(testCategory.getAccessView(), equalTo(DEFAULT_ACCESS_VIEW));
		assertThat(testCategory.getDefaultDisplayOrder(), equalTo(DEFAULT_DEFAULT_DISPLAY_ORDER));
		assertThat(testCategory.getName(), equalTo(DEFAULT_NAME));
		assertThat(testCategory.getDescription(), equalTo(DEFAULT_DESCRIPTION));
		assertThat(testCategory.getDisplayOrder(), equalTo(DEFAULT_DISPLAY_ORDER));
		assertThat(testCategory.getIconUrl(), equalTo(DEFAULT_ICON_URL));
		assertThat(testCategory.isRssAllowed(), equalTo(DEFAULT_RSS_ALLOWED));
		assertThat(testCategory.isHiddenIfEmpty(), equalTo(DEFAULT_HIDDEN_IF_EMPTY));
		assertThat(testCategory.getTtl(), equalTo(DEFAULT_TTL));
		assertThat(testCategory.getPublisher(), equalTo(publisher));
	}

	@Test
	@Transactional
	public void getAllCategorys() throws Exception {
		// Initialize the database
		categoryRepository.saveAndFlush(category);

		// Get all the categorys
		restCategoryMockMvc.perform(get("/api/categorys")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.[0].id").value(category.getId().intValue()))
				.andExpect(jsonPath("$.[0].accessView").value(DEFAULT_ACCESS_VIEW.getName()))
				.andExpect(jsonPath("$.[0].defaultDisplayOrder").value(DEFAULT_DEFAULT_DISPLAY_ORDER.getName()))
				.andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME))
				.andExpect(jsonPath("$.[0].description").value(DEFAULT_DESCRIPTION))
				.andExpect(jsonPath("$.[0].displayOrder").value(DEFAULT_DISPLAY_ORDER))
				.andExpect(jsonPath("$.[0].iconUrl").value(DEFAULT_ICON_URL))
				.andExpect(jsonPath("$.[0].rssAllowed").value(DEFAULT_RSS_ALLOWED))
				.andExpect(jsonPath("$.[0].hiddenIfEmpty").value(DEFAULT_HIDDEN_IF_EMPTY))
				.andExpect(jsonPath("$.[0].ttl").value(DEFAULT_TTL))
				.andExpect(jsonPath("$.[0].publisher.id").value(publisher.getId().intValue()));
	}

	@Test
	@Transactional
	public void getCategory() throws Exception {
		// Initialize the database
		categoryRepository.saveAndFlush(category);

		// Get the category
		restCategoryMockMvc.perform(get("/api/categorys/{id}", category.getId())).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(category.getId().intValue()))
				.andExpect(jsonPath("$.accessView").value(DEFAULT_ACCESS_VIEW.getName()))
				.andExpect(jsonPath("$.defaultDisplayOrder").value(DEFAULT_DEFAULT_DISPLAY_ORDER.getName()))
				.andExpect(jsonPath("$.name").value(DEFAULT_NAME))
				.andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
				.andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
				.andExpect(jsonPath("$.iconUrl").value(DEFAULT_ICON_URL))
				.andExpect(jsonPath("$.rssAllowed").value(DEFAULT_RSS_ALLOWED))
				.andExpect(jsonPath("$.hiddenIfEmpty").value(DEFAULT_HIDDEN_IF_EMPTY))
				.andExpect(jsonPath("$.ttl").value(DEFAULT_TTL))
				.andExpect(jsonPath("$.publisher.id").value(publisher.getId().intValue()));
	}

	@Test
	@Transactional
	public void getNonExistingCategory() throws Exception {
		// Get the category
		restCategoryMockMvc.perform(get("/api/categorys/{id}", 1L)).andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void updateCategory() throws Exception {
		// Initialize the database
		category = categoryRepository.saveAndFlush(category);
		log.debug("Category before change : {}", category);
		// Update the category
		category.setAccessView(UPDATED_ACCESS_VIEW);
		category.setDefaultDisplayOrder(UPDATED_DEFAULT_DISPLAY_ORDER);
		category.setName(UPDATED_NAME);
		category.setDescription(UPDATED_DESCRIPTION);
		category.setDisplayOrder(UPDATED_DISPLAY_ORDER);
		category.setIconUrl(UPDATED_ICON_URL);
		category.setRssAllowed(UPDATED_RSS_ALLOWED);
        category.setHiddenIfEmpty(UPDATED_HIDDEN_IF_EMPTY);
		category.setTtl(UPDATED_TTL);
		log.debug("Category before update : {}", category);
		restCategoryMockMvc.perform(
				put("/api/categorys").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
						TestUtil.convertObjectToJsonBytes(category))).andExpect(status().isOk());
		log.debug("Category after update : {}", category);

		// Validate the Category in the database
		List<Category> categorys = categoryRepository.findAll();
		assertThat(categorys, hasSize(1));
		Category testCategory = categorys.iterator().next();
		assertThat(testCategory.getAccessView(), equalTo(UPDATED_ACCESS_VIEW));
		assertThat(testCategory.getDefaultDisplayOrder(), equalTo(UPDATED_DEFAULT_DISPLAY_ORDER));
		assertThat(testCategory.getName(), equalTo(UPDATED_NAME));
		assertThat(testCategory.getDescription(), equalTo(UPDATED_DESCRIPTION));
		assertThat(testCategory.getDisplayOrder(), equalTo(UPDATED_DISPLAY_ORDER));
		assertThat(testCategory.getIconUrl(), equalTo(UPDATED_ICON_URL));
		assertThat(testCategory.isRssAllowed(), equalTo(UPDATED_RSS_ALLOWED));
		assertThat(testCategory.isHiddenIfEmpty(), equalTo(UPDATED_HIDDEN_IF_EMPTY));
		assertThat(testCategory.getTtl(), equalTo(UPDATED_TTL));
		assertThat(testCategory.getPublisher(), equalTo(publisher));
	}

	@Test
	@Transactional
	public void deleteCategory() throws Exception {
		// Initialize the database
		categoryRepository.saveAndFlush(category);

		// Get the category
		restCategoryMockMvc.perform(
				delete("/api/categorys/{id}", category.getId()).accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(
				status().isOk());

		// Validate the database is empty
		List<Category> categorys = categoryRepository.findAll();
		assertThat(categorys, hasSize(0));
	}
}
