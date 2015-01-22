package org.esupportail.publisher.web.rest;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.Filter;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.enums.FilterType;
import org.esupportail.publisher.repository.FilterRepository;
import org.esupportail.publisher.repository.ObjTest;
import org.esupportail.publisher.repository.OrganizationRepository;
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
 * Test class for the FilterResource REST controller.
 *
 * @see FilterResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class FilterResourceTest {

	private static final String DEFAULT_PATTERN = "SAMPLE_TEXT";
	private static final String UPDATED_PATTERN = "UPDATED_TEXT";

	private static final FilterType DEFAULT_TYPE = FilterType.LDAP;
	private static final FilterType UPDATED_TYPE = FilterType.LDAP;

	// private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
	// private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

	@Inject
	private FilterRepository filterRepository;

	private MockMvc restFilterMockMvc;

	private Filter filter;

	@Inject
	private OrganizationRepository organizationRepository;
	private Organization organization;

	@PostConstruct
	public void setup() {
		MockitoAnnotations.initMocks(this);
		FilterResource filterResource = new FilterResource();
		OrganizationResource organizationResource = new OrganizationResource();
		ReflectionTestUtils.setField(filterResource, "filterRepository",
				filterRepository);
		ReflectionTestUtils.setField(organizationResource,
				"organizationRepository", organizationRepository);
		this.restFilterMockMvc = MockMvcBuilders
				.standaloneSetup(filterResource).build();
	}

	@Before
	public void initTest() {
		organization = organizationRepository.saveAndFlush(ObjTest
				.newOrganization(DEFAULT_PATTERN));
		filter = new Filter();
		filter.setPattern(DEFAULT_PATTERN);
		filter.setType(DEFAULT_TYPE);
		filter.setOrganization(organization);
	}

	@Test
	@Transactional
	public void createFilter() throws Exception {
		// Validate the database is empty
		assertThat(filterRepository.findAll()).hasSize(0);

		// Create the Filter
		restFilterMockMvc.perform(
				post("/api/filters").contentType(
						TestUtil.APPLICATION_JSON_UTF8).content(
						TestUtil.convertObjectToJsonBytes(filter))).andExpect(
				status().isCreated());

		// Validate the Filter in the database
		List<Filter> filters = filterRepository.findAll();
		assertThat(filters).hasSize(1);
		Filter testFilter = filters.iterator().next();
		assertThat(testFilter.getPattern()).isEqualTo(DEFAULT_PATTERN);
		assertThat(testFilter.getType()).isEqualTo(DEFAULT_TYPE);
		assertThat(testFilter.getOrganization()).isEqualTo(organization);
		;
	}

	@Test
	@Transactional
	public void getAllFilters() throws Exception {
		// Initialize the database
		filterRepository.saveAndFlush(filter);

		// Get all the filters
		restFilterMockMvc
				.perform(get("/api/filters"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(
						jsonPath("$.[0].id").value(filter.getId().intValue()))
				.andExpect(
						jsonPath("$.[0].pattern").value(
								DEFAULT_PATTERN.toString()))
				.andExpect(
						jsonPath("$.[0].type").value(DEFAULT_TYPE.toString()))
				.andExpect(
						jsonPath("$.[0].organization.id").value(
								organization.getId().intValue()));
		// .andExpect(
		// jsonPath("$.[0].description").value(
		// DEFAULT_DESCRIPTION.toString()));
	}

	@Test
	@Transactional
	public void getFilter() throws Exception {
		// Initialize the database
		filterRepository.saveAndFlush(filter);

		// Get the filter
		restFilterMockMvc
				.perform(get("/api/filters/{id}", filter.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(filter.getId().intValue()))
				.andExpect(
						jsonPath("$.pattern").value(DEFAULT_PATTERN.toString()))
				.andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
				.andExpect(
						jsonPath("$.organization.id").value(
								organization.getId().intValue()));
		// .andExpect(
		// jsonPath("$.description").value(
		// DEFAULT_DESCRIPTION.toString()));
	}

	@Test
	@Transactional
	public void getNonExistingFilter() throws Exception {
		// Get the filter
		restFilterMockMvc.perform(get("/api/filters/{id}", 1L)).andExpect(
				status().isNotFound());
	}

	@Test
	@Transactional
	public void updateFilter() throws Exception {
		// Initialize the database
		filterRepository.saveAndFlush(filter);

		// Update the filter
		filter.setPattern(UPDATED_PATTERN);
		filter.setType(UPDATED_TYPE);
		// filter.setDescription(UPDATED_DESCRIPTION);
		restFilterMockMvc.perform(
				put("/api/filters").contentType(
						TestUtil.APPLICATION_JSON_UTF8).content(
						TestUtil.convertObjectToJsonBytes(filter))).andExpect(
				status().isOk());

		// Validate the Filter in the database
		List<Filter> filters = filterRepository.findAll();
		assertThat(filters).hasSize(1);
		Filter testFilter = filters.iterator().next();
		assertThat(testFilter.getPattern()).isEqualTo(UPDATED_PATTERN);
		assertThat(testFilter.getType()).isEqualTo(UPDATED_TYPE);
		assertThat(testFilter.getOrganization()).isEqualTo(organization);
		;
	}

	@Test
	@Transactional
	public void deleteFilter() throws Exception {
		// Initialize the database
		filterRepository.saveAndFlush(filter);

		// Get the filter
		restFilterMockMvc.perform(
				delete("/api/filters/{id}", filter.getId()).accept(
						TestUtil.APPLICATION_JSON_UTF8)).andExpect(
				status().isOk());

		// Validate the database is empty
		List<Filter> filters = filterRepository.findAll();
		assertThat(filters).hasSize(0);
	}
}
