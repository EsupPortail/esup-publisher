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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.Category;
import org.esupportail.publisher.domain.ExternalFeed;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.Reader;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.repository.CategoryRepository;
import org.esupportail.publisher.repository.ExternalFeedRepository;
import org.esupportail.publisher.repository.ObjTest;
import org.esupportail.publisher.repository.OrganizationRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.ReaderRepository;
import org.esupportail.publisher.repository.RedactorRepository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test class for the ExternalFeedResource REST controller.
 *
 * @see ExternalFeedResource
 */
@ExtendWith(SpringExtension.class)//@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ExternalFeedResourceTest {

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

	private static final String DEFAULT_RSS_URL = "http://default.app.fr/rss";
	private static final String UPDATED_RSS_URL = "http://updated.app.fr/rss";

	private static final Boolean DEFAULT_RSS_ALLOWED = true;

	@Inject
	private ExternalFeedRepository externalFeedRepository;

	private MockMvc restExternalFeedMockMvc;

	private ExternalFeed externalFeed;

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

		ExternalFeedResource externalFeedResource = new ExternalFeedResource();
		OrganizationResource organizationResource = new OrganizationResource();
		CategoryResource categoryResource = new CategoryResource();
		ReaderResource readerResource = new ReaderResource();
		RedactorResource redactorResource = new RedactorResource();
		PublisherResource publisherResource = new PublisherResource();
		ReflectionTestUtils.setField(externalFeedResource,
				"externalFeedRepository", externalFeedRepository);
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
		this.restExternalFeedMockMvc = MockMvcBuilders.standaloneSetup(
				externalFeedResource).build();
	}

	@BeforeAll
	public void initTest() {
		final String name = "NAME";
		organization = organizationRepository.saveAndFlush(ObjTest
				.newOrganization(name));

		reader = readerRepository.saveAndFlush(ObjTest.newReader(name));

		redactor = redactorRepository.saveAndFlush(ObjTest.newRedactor(name));

		publisher = publisherRepository.saveAndFlush(new Publisher(
				organization, reader, redactor, "PUBLISHER",
            PermissionClass.CONTEXT, false, true, true));

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

		externalFeed = new ExternalFeed();
		externalFeed.setAccessView(DEFAULT_ACCESS_VIEW);
		externalFeed.setDefaultDisplayOrder(DEFAULT_DEFAULT_DISPLAY_ORDER);
		externalFeed.setName(DEFAULT_NAME);
		externalFeed.setDescription(DEFAULT_DESCRIPTION);
		externalFeed.setDisplayOrder(DEFAULT_DISPLAY_ORDER);
		externalFeed.setIconUrl(DEFAULT_ICON_URL);
		externalFeed.setTtl(DEFAULT_TTL);
		externalFeed.setLang(DEFAULT_LANG);
		externalFeed.setRssUrl(DEFAULT_RSS_URL);
		externalFeed.setPublisher(publisher);
		externalFeed.setParent(category);
	}

	@Test
	@Transactional
	public void createExternalFeed() throws Exception {
		// Validate the database is empty
		assertThat(externalFeedRepository.findAll(), hasSize(0));

		// Create the ExternalFeed
		restExternalFeedMockMvc.perform(
				post("/api/externalFeeds").contentType(
						TestUtil.APPLICATION_JSON_UTF8).content(
						TestUtil.convertObjectToJsonBytes(externalFeed)))
				.andExpect(status().isCreated());

		// Validate the ExternalFeed in the database
		List<ExternalFeed> externalFeeds = externalFeedRepository.findAll();
		assertThat(externalFeeds, hasSize(1));
		ExternalFeed testExternalFeed = externalFeeds.iterator().next();
		assertThat(testExternalFeed.getAccessView(), equalTo(DEFAULT_ACCESS_VIEW));
		assertThat(testExternalFeed.getDefaultDisplayOrder(), equalTo(DEFAULT_DEFAULT_DISPLAY_ORDER));
		assertThat(testExternalFeed.getName(), equalTo(DEFAULT_NAME));
		assertThat(testExternalFeed.getDescription(), equalTo(DEFAULT_DESCRIPTION));
		assertThat(testExternalFeed.getDisplayOrder(), equalTo(DEFAULT_DISPLAY_ORDER));
		assertThat(testExternalFeed.getIconUrl(), equalTo(DEFAULT_ICON_URL));
		assertThat(testExternalFeed.getTtl(), equalTo(DEFAULT_TTL));
		assertThat(testExternalFeed.getLang(), equalTo(DEFAULT_LANG));
		assertThat(testExternalFeed.getRssUrl(), equalTo(DEFAULT_RSS_URL));
		assertThat(testExternalFeed.getPublisher(), equalTo(publisher));
		assertThat(testExternalFeed.getParent(), equalTo(category));
	}

	@Test
	@Transactional
	public void getAllExternalFeeds() throws Exception {
		// Initialize the database
		externalFeedRepository.saveAndFlush(externalFeed);

		// Get all the externalFeeds
		restExternalFeedMockMvc
				.perform(get("/api/externalFeeds"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.[0].id").value(externalFeed.getId().intValue()))
				.andExpect(jsonPath("$.[0].accessView").value(DEFAULT_ACCESS_VIEW.name()))
				.andExpect(jsonPath("$.[0].defaultDisplayOrder").value(DEFAULT_DEFAULT_DISPLAY_ORDER.name()))
				.andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME))
				.andExpect(jsonPath("$.[0].description").value(DEFAULT_DESCRIPTION))
				.andExpect(jsonPath("$.[0].displayOrder").value(DEFAULT_DISPLAY_ORDER))
				.andExpect(jsonPath("$.[0].iconUrl").value(DEFAULT_ICON_URL))
				.andExpect(jsonPath("$.[0].ttl").value(DEFAULT_TTL))
				.andExpect(jsonPath("$.[0].lang").value(DEFAULT_LANG))
				.andExpect(jsonPath("$.[0].rssUrl").value(DEFAULT_RSS_URL))
				.andExpect(jsonPath("$.[0].publisher.id").value(publisher.getId().intValue()))
				.andExpect(jsonPath("$.[0].parent.id").value(category.getId().intValue()));
	}

	@Test
	@Transactional
	public void getExternalFeed() throws Exception {
		// Initialize the database
		externalFeedRepository.saveAndFlush(externalFeed);

		// Get the externalFeed
		restExternalFeedMockMvc
				.perform(
						get("/api/externalFeeds/{id}",
								externalFeed.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(externalFeed.getId().intValue()))
				.andExpect(jsonPath("$.accessView").value(DEFAULT_ACCESS_VIEW.name()))
				.andExpect(jsonPath("$.defaultDisplayOrder").value(DEFAULT_DEFAULT_DISPLAY_ORDER.name()))
				.andExpect(jsonPath("$.name").value(DEFAULT_NAME))
				.andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
				.andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
				.andExpect(jsonPath("$.iconUrl").value(DEFAULT_ICON_URL))
				.andExpect(jsonPath("$.ttl").value(DEFAULT_TTL))
				.andExpect(jsonPath("$.lang").value(DEFAULT_LANG))
				.andExpect(jsonPath("$.rssUrl").value(DEFAULT_RSS_URL))
				.andExpect(jsonPath("$.publisher.id").value(publisher.getId().intValue()))
				.andExpect(jsonPath("$.parent.id").value(category.getId().intValue()));
	}

	@Test
	@Transactional
	public void getNonExistingExternalFeed() throws Exception {
		// Get the externalFeed
		restExternalFeedMockMvc
				.perform(get("/api/externalFeeds/{id}", 1L)).andExpect(
						status().isNotFound());
	}

	@Test
	@Transactional
	public void updateExternalFeed() throws Exception {
		// Initialize the database
		externalFeedRepository.saveAndFlush(externalFeed);

		// Update the externalFeed
		externalFeed.setAccessView(UPDATED_ACCESS_VIEW);
		externalFeed.setDefaultDisplayOrder(UPDATED_DEFAULT_DISPLAY_ORDER);
		externalFeed.setName(UPDATED_NAME);
		externalFeed.setDescription(UPDATED_DESCRIPTION);
		externalFeed.setDisplayOrder(UPDATED_DISPLAY_ORDER);
		externalFeed.setIconUrl(UPDATED_ICON_URL);
		externalFeed.setTtl(UPDATED_TTL);
		externalFeed.setLang(UPDATED_LANG);
		externalFeed.setRssUrl(UPDATED_RSS_URL);
		restExternalFeedMockMvc.perform(
				put("/api/externalFeeds").contentType(
						TestUtil.APPLICATION_JSON_UTF8).content(
						TestUtil.convertObjectToJsonBytes(externalFeed)))
				.andExpect(status().isOk());

		// Validate the ExternalFeed in the database
		List<ExternalFeed> externalFeeds = externalFeedRepository.findAll();
		assertThat(externalFeeds, hasSize(1));
		ExternalFeed testExternalFeed = externalFeeds.iterator().next();
		assertThat(testExternalFeed.getAccessView(), equalTo(UPDATED_ACCESS_VIEW));
		assertThat(testExternalFeed.getDefaultDisplayOrder(), equalTo(UPDATED_DEFAULT_DISPLAY_ORDER));
		assertThat(testExternalFeed.getName(), equalTo(UPDATED_NAME));
		assertThat(testExternalFeed.getDescription(), equalTo(UPDATED_DESCRIPTION));
		assertThat(testExternalFeed.getDisplayOrder(), equalTo(UPDATED_DISPLAY_ORDER));
		assertThat(testExternalFeed.getIconUrl(), equalTo(UPDATED_ICON_URL));
		assertThat(testExternalFeed.getTtl(), equalTo(UPDATED_TTL));
		assertThat(testExternalFeed.getLang(), equalTo(UPDATED_LANG));
		assertThat(testExternalFeed.getRssUrl(), equalTo(UPDATED_RSS_URL));
		assertThat(testExternalFeed.getPublisher(), equalTo(publisher));
		assertThat(testExternalFeed.getParent(), equalTo(category));
	}

	@Test
	@Transactional
	public void deleteExternalFeed() throws Exception {
		// Initialize the database
		externalFeedRepository.saveAndFlush(externalFeed);

		// Get the externalFeed
		restExternalFeedMockMvc.perform(
				delete("/api/externalFeeds/{id}", externalFeed.getId())
						.accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(
				status().isOk());

		// Validate the database is empty
		List<ExternalFeed> externalFeeds = externalFeedRepository.findAll();
		assertThat(externalFeeds, hasSize(0));
	}
}