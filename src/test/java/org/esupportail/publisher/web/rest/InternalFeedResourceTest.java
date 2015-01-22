package org.esupportail.publisher.web.rest;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.repository.*;
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
 * Test class for the InternalFeedResource REST controller.
 *
 * @see InternalFeedResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class InternalFeedResourceTest {

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

	private static final Integer DEFAULT_TTL = 1000;
	private static final Integer UPDATED_TTL = 10000;

	private static final String DEFAULT_LANG = "fr";
	private static final String UPDATED_LANG = "fr_fr";

	private static final Boolean DEFAULT_RSS_ALLOWED = true;

	@Inject
	private InternalFeedRepository internalFeedRepository;

	private MockMvc restInternalFeedMockMvc;

	private InternalFeed internalFeed;

	@Inject
	private OrganizationRepository organizationRepository;
	private Organization organization;

	@Inject
	private CategoryRepository categoryRepository;
	private Category category;

	@Inject
	private ReaderRepository readerRepository;
	private Reader reader;

	@Inject
	private RedactorRepository redactorRepository;
	private Redactor redactor;

	@Inject
	private PublisherRepository publisherRepository;
	private Publisher publisher;

	@PostConstruct
	public void setup() {
		MockitoAnnotations.initMocks(this);
		InternalFeedResource internalFeedResource = new InternalFeedResource();
		OrganizationResource organizationResource = new OrganizationResource();
		CategoryResource categoryResource = new CategoryResource();
		ReaderResource readerResource = new ReaderResource();
		RedactorResource redactorResource = new RedactorResource();
		PublisherResource publisherResource = new PublisherResource();
		ReflectionTestUtils.setField(internalFeedResource,
				"internalFeedRepository", internalFeedRepository);
		ReflectionTestUtils.setField(categoryResource, "categoryRepository",
				categoryRepository);
		ReflectionTestUtils.setField(organizationResource,
				"organizationRepository", organizationRepository);
		ReflectionTestUtils.setField(readerResource, "readerRepository",
				readerRepository);
		ReflectionTestUtils.setField(redactorResource, "redactorRepository",
				redactorRepository);
		ReflectionTestUtils.setField(publisherResource, "publisherRepository",
				publisherRepository);
		this.restInternalFeedMockMvc = MockMvcBuilders.standaloneSetup(
				internalFeedResource).build();
	}

	@Before
	public void initTest() {
		final String name = "NAME";
		organization = organizationRepository.saveAndFlush(ObjTest
				.newOrganization(name));

		reader = readerRepository.saveAndFlush(ObjTest.newReader(name));

		redactor = redactorRepository.saveAndFlush(ObjTest.newRedactor(name));

		publisher = publisherRepository.saveAndFlush(new Publisher(
				organization, reader, redactor, PermissionClass.CONTEXT,
				false));

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
		category = categoryRepository.saveAndFlush(category);

		internalFeed = new InternalFeed();
		internalFeed.setAccessView(DEFAULT_ACCESS_VIEW);
		internalFeed.setDefaultDisplayOrder(DEFAULT_DEFAULT_DISPLAY_ORDER);
		internalFeed.setName(DEFAULT_NAME);
		internalFeed.setDescription(DEFAULT_DESCRIPTION);
		internalFeed.setDisplayOrder(DEFAULT_DISPLAY_ORDER);
		internalFeed.setIconUrl(DEFAULT_ICON_URL);
		internalFeed.setTtl(DEFAULT_TTL);
		internalFeed.setLang(DEFAULT_LANG);
		internalFeed.setPublisher(publisher);
		internalFeed.setParent(category);
	}

	@Test
	@Transactional
	public void createInternalFeed() throws Exception {
		// Validate the database is empty
		assertThat(internalFeedRepository.findAll()).hasSize(0);

		// Create the InternalFeed
		restInternalFeedMockMvc.perform(
				post("/api/internalFeeds").contentType(
						TestUtil.APPLICATION_JSON_UTF8).content(
						TestUtil.convertObjectToJsonBytes(internalFeed)))
				.andExpect(status().isCreated());

		// Validate the InternalFeed in the database
		List<InternalFeed> internalFeeds = internalFeedRepository.findAll();
		assertThat(internalFeeds).hasSize(1);
		InternalFeed testInternalFeed = internalFeeds.iterator().next();
		assertThat(testInternalFeed.getAccessView()).isEqualTo(
				DEFAULT_ACCESS_VIEW);
		assertThat(testInternalFeed.getDefaultDisplayOrder()).isEqualTo(
				DEFAULT_DEFAULT_DISPLAY_ORDER);
		assertThat(testInternalFeed.getName()).isEqualTo(DEFAULT_NAME);
		assertThat(testInternalFeed.getDescription()).isEqualTo(
				DEFAULT_DESCRIPTION);
		assertThat(testInternalFeed.getDisplayOrder()).isEqualTo(
				DEFAULT_DISPLAY_ORDER);
		assertThat(testInternalFeed.getIconUrl()).isEqualTo(DEFAULT_ICON_URL);
		assertThat(testInternalFeed.getTtl()).isEqualTo(DEFAULT_TTL);
		assertThat(testInternalFeed.getLang()).isEqualTo(DEFAULT_LANG);
		assertThat(testInternalFeed.getPublisher()).isEqualTo(publisher);
		assertThat(testInternalFeed.getParent()).isEqualTo(category);
		;
	}

	@Test
	@Transactional
	public void getAllInternalFeeds() throws Exception {
		// Initialize the database
		internalFeedRepository.saveAndFlush(internalFeed);

		// Get all the internalFeeds
		restInternalFeedMockMvc
				.perform(get("/api/internalFeeds"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(
						jsonPath("$.[0].id").value(
								internalFeed.getId().intValue()))
				.andExpect(
						jsonPath("$.[0].accessView").value(
								DEFAULT_ACCESS_VIEW.name()))
				.andExpect(
						jsonPath("$.[0].defaultDisplayOrder").value(
								DEFAULT_DEFAULT_DISPLAY_ORDER.name()))
				.andExpect(
						jsonPath("$.[0].name").value(DEFAULT_NAME.toString()))
				.andExpect(
						jsonPath("$.[0].description").value(
								DEFAULT_DESCRIPTION.toString()))
				.andExpect(
						jsonPath("$.[0].displayOrder").value(
								DEFAULT_DISPLAY_ORDER))
				.andExpect(
						jsonPath("$.[0].iconUrl").value(
								DEFAULT_ICON_URL.toString()))
				.andExpect(jsonPath("$.[0].ttl").value(DEFAULT_TTL))
				.andExpect(
						jsonPath("$.[0].lang").value(DEFAULT_LANG.toString()))
				.andExpect(
						jsonPath("$.[0].publisher.id").value(
								publisher.getId().intValue()))
				.andExpect(
						jsonPath("$.[0].parent.id").value(
								category.getId().intValue()));
	}

	@Test
	@Transactional
	public void getInternalFeed() throws Exception {
		// Initialize the database
		internalFeedRepository.saveAndFlush(internalFeed);

		// Get the internalFeed
		restInternalFeedMockMvc
				.perform(
						get("/api/internalFeeds/{id}",
								internalFeed.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(
						jsonPath("$.id").value(internalFeed.getId().intValue()))
				.andExpect(
						jsonPath("$.accessView").value(
								DEFAULT_ACCESS_VIEW.name()))
				.andExpect(
						jsonPath("$.defaultDisplayOrder").value(
								DEFAULT_DEFAULT_DISPLAY_ORDER.name()))
				.andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
				.andExpect(
						jsonPath("$.description").value(
								DEFAULT_DESCRIPTION.toString()))
				.andExpect(
						jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
				.andExpect(
						jsonPath("$.iconUrl")
								.value(DEFAULT_ICON_URL.toString()))
				.andExpect(jsonPath("$.ttl").value(DEFAULT_TTL))
				.andExpect(jsonPath("$.lang").value(DEFAULT_LANG.toString()))
				.andExpect(
						jsonPath("$.publisher.id").value(
								publisher.getId().intValue()))
				.andExpect(
						jsonPath("$.parent.id").value(
								category.getId().intValue()));
	}

	@Test
	@Transactional
	public void getNonExistingInternalFeed() throws Exception {
		// Get the internalFeed
		restInternalFeedMockMvc
				.perform(get("/api/internalFeeds/{id}", 1L)).andExpect(
						status().isNotFound());
	}

	@Test
	@Transactional
	public void updateInternalFeed() throws Exception {
		// Initialize the database
		internalFeedRepository.saveAndFlush(internalFeed);

		// Update the internalFeed
		internalFeed.setAccessView(UPDATED_ACCESS_VIEW);
		internalFeed.setDefaultDisplayOrder(UPDATED_DEFAULT_DISPLAY_ORDER);
		internalFeed.setName(UPDATED_NAME);
		internalFeed.setDescription(UPDATED_DESCRIPTION);
		internalFeed.setDisplayOrder(UPDATED_DISPLAY_ORDER);
		internalFeed.setIconUrl(UPDATED_ICON_URL);
		internalFeed.setTtl(UPDATED_TTL);
		internalFeed.setLang(UPDATED_LANG);
		restInternalFeedMockMvc.perform(
				put("/api/internalFeeds").contentType(
						TestUtil.APPLICATION_JSON_UTF8).content(
						TestUtil.convertObjectToJsonBytes(internalFeed)))
				.andExpect(status().isOk());

		// Validate the InternalFeed in the database
		List<InternalFeed> internalFeeds = internalFeedRepository.findAll();
		assertThat(internalFeeds).hasSize(1);
		InternalFeed testInternalFeed = internalFeeds.iterator().next();
		assertThat(testInternalFeed.getAccessView()).isEqualTo(
				UPDATED_ACCESS_VIEW);
		assertThat(testInternalFeed.getDefaultDisplayOrder()).isEqualTo(
				UPDATED_DEFAULT_DISPLAY_ORDER);
		assertThat(testInternalFeed.getName()).isEqualTo(UPDATED_NAME);
		assertThat(testInternalFeed.getDescription()).isEqualTo(
				UPDATED_DESCRIPTION);
		assertThat(testInternalFeed.getDisplayOrder()).isEqualTo(
				UPDATED_DISPLAY_ORDER);
		assertThat(testInternalFeed.getIconUrl()).isEqualTo(UPDATED_ICON_URL);
		assertThat(testInternalFeed.getTtl()).isEqualTo(UPDATED_TTL);
		assertThat(testInternalFeed.getLang()).isEqualTo(UPDATED_LANG);
		assertThat(testInternalFeed.getPublisher()).isEqualTo(publisher);
		assertThat(testInternalFeed.getParent()).isEqualTo(category);
		;
	}

	@Test
	@Transactional
	public void deleteInternalFeed() throws Exception {
		// Initialize the database
		internalFeedRepository.saveAndFlush(internalFeed);

		// Get the internalFeed
		restInternalFeedMockMvc.perform(
				delete("/api/internalFeeds/{id}", internalFeed.getId())
						.accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(
				status().isOk());

		// Validate the database is empty
		List<InternalFeed> internalFeeds = internalFeedRepository.findAll();
		assertThat(internalFeeds).hasSize(0);
	}
}
