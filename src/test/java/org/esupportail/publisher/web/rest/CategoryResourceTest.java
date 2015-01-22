package org.esupportail.publisher.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.repository.*;
import org.esupportail.publisher.service.bean.UserContextTree;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
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
 * Test class for the CategoryResource REST controller.
 *
 * @see CategoryResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
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

	@Inject
	private CategoryRepository categoryRepository;

	private MockMvc restCategoryMockMvc;

	private Category category;

	@Inject
	private PublisherRepository publisherRepository;

	private Publisher publisher;

	@Inject
	private OrganizationRepository organizationRepository;
	private Organization organization;

	@Inject
	private ReaderRepository readerRepository;
	private Reader reader;

	@Inject
	private RedactorRepository redactorRepository;
	private Redactor redactor;

	@PostConstruct
	public void setup() {
		MockitoAnnotations.initMocks(this);
		CategoryResource categoryResource = new CategoryResource();
		OrganizationResource organizationResource = new OrganizationResource();
        UserContextTree userContextTree = new UserContextTree();
		ReaderResource readerResource = new ReaderResource();
		RedactorResource redactorResource = new RedactorResource();
		PublisherResource publisherResource = new PublisherResource();
		ReflectionTestUtils.setField(categoryResource, "categoryRepository", categoryRepository);
		ReflectionTestUtils.setField(organizationResource, "organizationRepository", organizationRepository);
		ReflectionTestUtils.setField(readerResource, "readerRepository", readerRepository);
		ReflectionTestUtils.setField(redactorResource, "redactorRepository", redactorRepository);
		ReflectionTestUtils.setField(publisherResource, "publisherRepository", publisherRepository);
        ReflectionTestUtils.setField(categoryResource, "userSessionTree", userContextTree);
		this.restCategoryMockMvc = MockMvcBuilders.standaloneSetup(categoryResource).build();
	}

	@Before
	public void initTest() {
		final String name = "NAME";
		organization = organizationRepository.saveAndFlush(ObjTest.newOrganization(name));

		reader = readerRepository.saveAndFlush(ObjTest.newReader(name));

		redactor = redactorRepository.saveAndFlush(ObjTest.newRedactor(name));

		publisher = publisherRepository.saveAndFlush(new Publisher(organization, reader, redactor,
            PermissionClass.CONTEXT, false));

		category = new Category();
		category.setAccessView(DEFAULT_ACCESS_VIEW);
		category.setDefaultDisplayOrder(DEFAULT_DEFAULT_DISPLAY_ORDER);
		category.setName(DEFAULT_NAME);
		category.setDescription(DEFAULT_DESCRIPTION);
		category.setDisplayOrder(DEFAULT_DISPLAY_ORDER);
		category.setIconUrl(DEFAULT_ICON_URL);
		category.setRssAllowed(DEFAULT_RSS_ALLOWED);
		category.setTtl(DEFAULT_TTL);
		category.setPublisher(publisher);
	}

	@Test
	@Transactional
	public void createCategory() throws Exception {
		// Validate the database is empty
		assertThat(categoryRepository.findAll()).hasSize(0);

		// Create the Category
		restCategoryMockMvc.perform(
				post("/api/categorys").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
						TestUtil.convertObjectToJsonBytes(category))).andExpect(status().isCreated());

		// Validate the Category in the database
		List<Category> categorys = categoryRepository.findAll();
		assertThat(categorys).hasSize(1);
		Category testCategory = categorys.iterator().next();
		assertThat(testCategory.getAccessView()).isEqualTo(DEFAULT_ACCESS_VIEW);
		assertThat(testCategory.getDefaultDisplayOrder()).isEqualTo(DEFAULT_DEFAULT_DISPLAY_ORDER);
		assertThat(testCategory.getName()).isEqualTo(DEFAULT_NAME);
		assertThat(testCategory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
		assertThat(testCategory.getDisplayOrder()).isEqualTo(DEFAULT_DISPLAY_ORDER);
		assertThat(testCategory.getIconUrl()).isEqualTo(DEFAULT_ICON_URL);
		assertThat(testCategory.isRssAllowed()).isEqualTo(DEFAULT_RSS_ALLOWED);
		assertThat(testCategory.getTtl()).isEqualTo(DEFAULT_TTL);
		assertThat(testCategory.getPublisher()).isEqualTo(publisher);
		;
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
				.andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME.toString()))
				.andExpect(jsonPath("$.[0].description").value(DEFAULT_DESCRIPTION.toString()))
				.andExpect(jsonPath("$.[0].displayOrder").value(DEFAULT_DISPLAY_ORDER))
				.andExpect(jsonPath("$.[0].iconUrl").value(DEFAULT_ICON_URL.toString()))
				.andExpect(jsonPath("$.[0].rssAllowed").value(DEFAULT_RSS_ALLOWED.booleanValue()))
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
				.andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
				.andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
				.andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
				.andExpect(jsonPath("$.iconUrl").value(DEFAULT_ICON_URL.toString()))
				.andExpect(jsonPath("$.rssAllowed").value(DEFAULT_RSS_ALLOWED.booleanValue()))
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
		category.setTtl(UPDATED_TTL);
		log.debug("Category before update : {}", category);
		restCategoryMockMvc.perform(
				put("/api/categorys").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
						TestUtil.convertObjectToJsonBytes(category))).andExpect(status().isOk());
		log.debug("Category after update : {}", category);

		// Validate the Category in the database
		List<Category> categorys = categoryRepository.findAll();
		assertThat(categorys).hasSize(1);
		Category testCategory = categorys.iterator().next();
		assertThat(testCategory.getAccessView()).isEqualTo(UPDATED_ACCESS_VIEW);
		assertThat(testCategory.getDefaultDisplayOrder()).isEqualTo(UPDATED_DEFAULT_DISPLAY_ORDER);
		assertThat(testCategory.getName()).isEqualTo(UPDATED_NAME);
		assertThat(testCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
		assertThat(testCategory.getDisplayOrder()).isEqualTo(UPDATED_DISPLAY_ORDER);
		assertThat(testCategory.getIconUrl()).isEqualTo(UPDATED_ICON_URL);
		assertThat(testCategory.isRssAllowed()).isEqualTo(UPDATED_RSS_ALLOWED);
		assertThat(testCategory.getTtl()).isEqualTo(UPDATED_TTL);
		assertThat(testCategory.getPublisher()).isEqualTo(publisher);
		;
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
		assertThat(categorys).hasSize(0);
	}
}
