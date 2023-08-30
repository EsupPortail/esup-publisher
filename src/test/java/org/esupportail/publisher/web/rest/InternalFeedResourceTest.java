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

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the InternalFeedResource REST controller.
 *
 * @see InternalFeedResource
 */
@SpringBootTest(classes = Application.class)
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
    private static final Boolean DEFAULT_HIDDEN_IF_EMPTY = true;

	@Inject
	private InternalFeedRepository internalFeedRepository;
	@Inject
	private OrganizationRepository organizationRepository;
	@Inject
	private CategoryRepository categoryRepository;
	@Inject
	private ReaderRepository readerRepository;
	@Inject
	private RedactorRepository redactorRepository;
	@Inject
	private PublisherRepository publisherRepository;

	private MockMvc restInternalFeedMockMvc;

	private Organization organization;
	private Reader reader;
	private Redactor redactor;
	private Publisher publisher;
	private Category category;
	private InternalFeed internalFeed;

	@PostConstruct
	public void setup() {
		//closeable = MockitoAnnotations.openMocks(this);
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

	@BeforeEach
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
        category.setHiddenIfEmpty(DEFAULT_HIDDEN_IF_EMPTY);
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
		assertThat(internalFeedRepository.findAll(), hasSize(0));

		// Create the InternalFeed
		restInternalFeedMockMvc.perform(
				post("/api/internalFeeds").contentType(
						TestUtil.APPLICATION_JSON_UTF8).content(
						TestUtil.convertObjectToJsonBytes(internalFeed)))
				.andExpect(status().isCreated());

		// Validate the InternalFeed in the database
		List<InternalFeed> internalFeeds = internalFeedRepository.findAll();
		assertThat(internalFeeds, hasSize(1));
		InternalFeed testInternalFeed = internalFeeds.iterator().next();
		assertThat(testInternalFeed.getAccessView(), equalTo(DEFAULT_ACCESS_VIEW));
		assertThat(testInternalFeed.getDefaultDisplayOrder(), equalTo(DEFAULT_DEFAULT_DISPLAY_ORDER));
		assertThat(testInternalFeed.getName(), equalTo(DEFAULT_NAME));
		assertThat(testInternalFeed.getDescription(), equalTo(DEFAULT_DESCRIPTION));
		assertThat(testInternalFeed.getDisplayOrder(), equalTo(DEFAULT_DISPLAY_ORDER));
		assertThat(testInternalFeed.getIconUrl(), equalTo(DEFAULT_ICON_URL));
		assertThat(testInternalFeed.getTtl(), equalTo(DEFAULT_TTL));
		assertThat(testInternalFeed.getLang(), equalTo(DEFAULT_LANG));
		assertThat(testInternalFeed.getPublisher(), equalTo(publisher));
		assertThat(testInternalFeed.getParent(), equalTo(category));
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
				.andExpect(jsonPath("$.[0].id").value(internalFeed.getId().intValue()))
				.andExpect(jsonPath("$.[0].accessView").value(DEFAULT_ACCESS_VIEW.name()))
				.andExpect(jsonPath("$.[0].defaultDisplayOrder").value(DEFAULT_DEFAULT_DISPLAY_ORDER.name()))
				.andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME))
				.andExpect(jsonPath("$.[0].description").value(DEFAULT_DESCRIPTION))
				.andExpect(jsonPath("$.[0].displayOrder").value(DEFAULT_DISPLAY_ORDER))
				.andExpect(jsonPath("$.[0].iconUrl").value(DEFAULT_ICON_URL))
				.andExpect(jsonPath("$.[0].ttl").value(DEFAULT_TTL))
				.andExpect(jsonPath("$.[0].lang").value(DEFAULT_LANG))
				.andExpect(jsonPath("$.[0].publisher.id").value(publisher.getId().intValue()))
				.andExpect(jsonPath("$.[0].parent.id").value(category.getId().intValue()));
	}

	@Test
	@Transactional
	public void getInternalFeed() throws Exception {
		// Initialize the database
		internalFeedRepository.saveAndFlush(internalFeed);

		// Get the internalFeed
		restInternalFeedMockMvc
				.perform(get("/api/internalFeeds/{id}",internalFeed.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(internalFeed.getId().intValue()))
				.andExpect(jsonPath("$.accessView").value(DEFAULT_ACCESS_VIEW.name()))
				.andExpect(jsonPath("$.defaultDisplayOrder").value(DEFAULT_DEFAULT_DISPLAY_ORDER.name()))
				.andExpect(jsonPath("$.name").value(DEFAULT_NAME))
				.andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
				.andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
				.andExpect(jsonPath("$.iconUrl").value(DEFAULT_ICON_URL))
				.andExpect(jsonPath("$.ttl").value(DEFAULT_TTL))
				.andExpect(jsonPath("$.lang").value(DEFAULT_LANG))
				.andExpect(jsonPath("$.publisher.id").value(publisher.getId().intValue()))
				.andExpect(jsonPath("$.parent.id").value(category.getId().intValue()));
	}

	@Test
	@Transactional
	public void getNonExistingInternalFeed() throws Exception {
		// Get the internalFeed
		restInternalFeedMockMvc
				.perform(get("/api/internalFeeds/{id}", 1L))
				.andExpect(status().isNotFound());
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
		assertThat(internalFeeds, hasSize(1));
		InternalFeed testInternalFeed = internalFeeds.iterator().next();
		assertThat(testInternalFeed.getAccessView(), equalTo(UPDATED_ACCESS_VIEW));
		assertThat(testInternalFeed.getDefaultDisplayOrder(), equalTo(UPDATED_DEFAULT_DISPLAY_ORDER));
		assertThat(testInternalFeed.getName(), equalTo(UPDATED_NAME));
		assertThat(testInternalFeed.getDescription(), equalTo(UPDATED_DESCRIPTION));
		assertThat(testInternalFeed.getDisplayOrder(), equalTo(UPDATED_DISPLAY_ORDER));
		assertThat(testInternalFeed.getIconUrl(), equalTo(UPDATED_ICON_URL));
		assertThat(testInternalFeed.getTtl(), equalTo(UPDATED_TTL));
		assertThat(testInternalFeed.getLang(), equalTo(UPDATED_LANG));
		assertThat(testInternalFeed.getPublisher(), equalTo(publisher));
		assertThat(testInternalFeed.getParent(), equalTo(category));
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
		assertThat(internalFeeds, hasSize(0));
	}
}
