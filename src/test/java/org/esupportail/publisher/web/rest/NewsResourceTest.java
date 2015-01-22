package org.esupportail.publisher.web.rest;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.repository.*;
import org.esupportail.publisher.service.FileService;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
 * Test class for the NewsResource REST controller.
 *
 * @see NewsResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class NewsResourceTest {

	private static final String DEFAULT_TITLE = "SAMPLE_TEXT";
	private static final String UPDATED_TITLE = "UPDATED_TEXT";

	private static final String DEFAULT_SUMMARY = "SAMPLE_TEXT";
	private static final String UPDATED_SUMMARY = "UPDATED_TEXT";

    private static final String DEFAULT_ENCLOSURE = "http://un.domaine.fr/path/file.jpg";
    private static final String UPDATED_ENCLOSURE = "http://deux.domaine.fr/path/media.png";

	private static final LocalDate DEFAULT_END_DATE = LocalDate.now().plusDays(
			2);
	private static final LocalDate UPDATED_END_DATE = LocalDate.now()
			.plusMonths(1);

	private static final LocalDate DEFAULT_START_DATE = LocalDate.now();
	private static final LocalDate UPDATED_START_DATE = LocalDate.now()
			.minusDays(1);

	private static final ItemStatus DEFAULT_STATUS = ItemStatus.DRAFT;
	private static final ItemStatus UPDATED_STATUS = ItemStatus.PENDING;

	private static final DateTime DEFAULT_VALIDATION_DATE = ObjTest.d1;
	private static final DateTime UPDATED_VALIDATION_DATE = ObjTest.d1
			.plusDays(1);

	private static final String DEFAULT_BODY = "SAMPLE_TEXT";
	private static final String UPDATED_BODY = "UPDATED_TEXT";
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

	@Inject
	private NewsRepository newsRepository;

	private MockMvc restNewsMockMvc;

	private News news;

	@Inject
	private OrganizationRepository organizationRepository;
	private Organization organization;
	@Inject
	private RedactorRepository redactorRepository;
	private Redactor redactor;
    @Inject
    private UserRepository userRepo;
    private User user1;private User user2;private User user3;


	@PostConstruct
	public void setup() {
		MockitoAnnotations.initMocks(this);
		NewsResource newsResource = new NewsResource();
		OrganizationResource organizationResource = new OrganizationResource();
		RedactorResource redactorResource = new RedactorResource();
        FileService fileservice = new FileService();
		ReflectionTestUtils.setField(newsResource, "newsRepository",
				newsRepository);
        ReflectionTestUtils.setField(newsResource, "fileService", fileservice);
		ReflectionTestUtils.setField(organizationResource,
				"organizationRepository", organizationRepository);
		ReflectionTestUtils.setField(redactorResource, "redactorRepository",
				redactorRepository);
		this.restNewsMockMvc = MockMvcBuilders.standaloneSetup(newsResource,
				organizationResource).build();
	}

	@Before
	public void initTest() {
		final String name = "NAME";
		organization = organizationRepository.saveAndFlush(ObjTest
				.newOrganization(name));
		redactor = redactorRepository.saveAndFlush(ObjTest.newRedactor(name));
        user1 = userRepo.findOne(ObjTest.subject1);
        user2 = userRepo.findOne(ObjTest.subject2);
        user3 = userRepo.findOne(ObjTest.subject3);

		news = new News();
		news.setTitle(DEFAULT_TITLE);
		news.setSummary(DEFAULT_SUMMARY);
		news.setEnclosure(DEFAULT_ENCLOSURE);
		news.setEndDate(DEFAULT_END_DATE);
		news.setStartDate(DEFAULT_START_DATE);
		news.setStatus(DEFAULT_STATUS);
		news.setValidatedDate(DEFAULT_VALIDATION_DATE);
		news.setValidatedBy(user1);
		news.setBody(DEFAULT_BODY);
		news.setOrganization(organization);
		news.setRedactor(redactor);
	}

	@Test
	@Transactional
	public void createNews() throws Exception {
		// Validate the database is empty
		assertThat(newsRepository.findAll()).hasSize(0);

		// Create the News
		restNewsMockMvc.perform(
				post("/api/newss").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(TestUtil.convertObjectToJsonBytes(news)))
				.andExpect(status().isCreated());

		// Validate the News in the database
		List<News> newss = newsRepository.findAll();
		assertThat(newss).hasSize(1);
		News testNews = newss.iterator().next();
		assertThat(testNews.getTitle()).isEqualTo(DEFAULT_TITLE);
		assertThat(testNews.getSummary()).isEqualTo(DEFAULT_SUMMARY);
		assertThat(testNews.getEnclosure()).isEqualTo(DEFAULT_ENCLOSURE);
		assertThat(testNews.getEndDate()).isEqualTo(DEFAULT_END_DATE);
		assertThat(testNews.getStartDate()).isEqualTo(DEFAULT_START_DATE);
		assertThat(testNews.getStatus()).isEqualTo(DEFAULT_STATUS);
		assertThat(testNews.getValidatedDate()).isEqualTo(
				DEFAULT_VALIDATION_DATE);
		assertThat(testNews.getValidatedBy().getLogin()).isEqualTo(
				user1.getLogin());
		assertThat(testNews.getBody()).isEqualTo(DEFAULT_BODY);
		assertThat(testNews.getRedactor()).isEqualTo(redactor);
		assertThat(testNews.getOrganization()).isEqualTo(organization);
		;
	}

	@Test
	@Transactional
	public void getAllNewss() throws Exception {
		// Initialize the database
		newsRepository.saveAndFlush(news);

		// Get all the newss
		restNewsMockMvc
				.perform(get("/api/newss"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.[0].id").value(news.getId().intValue()))
				.andExpect(
						jsonPath("$.[0].title").value(DEFAULT_TITLE.toString()))
				.andExpect(
						jsonPath("$.[0].summary").value(
								DEFAULT_SUMMARY.toString()))
				.andExpect(
						jsonPath("$.[0].enclosure").value(
								DEFAULT_ENCLOSURE.toString()))
				.andExpect(
						jsonPath("$.[0].endDate").value(
								DEFAULT_END_DATE.toString()))
				.andExpect(
						jsonPath("$.[0].startDate").value(
								DEFAULT_START_DATE.toString()))
				.andExpect(
						jsonPath("$.[0].status").value(DEFAULT_STATUS.name()))
				.andExpect(
						jsonPath("$.[0].validatedBy.login").value(user1.getLogin()))
				.andExpect(
						jsonPath("$.[0].validatedDate").value(
								DEFAULT_VALIDATION_DATE
										.toString(dateTimeFormatter
												.withZoneUTC())))
				.andExpect(
						jsonPath("$.[0].body").value(DEFAULT_BODY.toString()))
				.andExpect(
						jsonPath("$.[0].organization.id").value(
								organization.getId().intValue()))
				.andExpect(
						jsonPath("$.[0].redactor.id").value(
								redactor.getId().intValue()));
	}

	@Test
	@Transactional
	public void getNews() throws Exception {
		// Initialize the database
		newsRepository.saveAndFlush(news);

		// Get the news
		restNewsMockMvc
				.perform(get("/api/newss/{id}", news.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(news.getId().intValue()))
				.andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
				.andExpect(
						jsonPath("$.summary").value(DEFAULT_SUMMARY.toString()))
				.andExpect(
						jsonPath("$.enclosure").value(
								DEFAULT_ENCLOSURE.toString()))
				.andExpect(
						jsonPath("$.endDate")
								.value(DEFAULT_END_DATE.toString()))
				.andExpect(
						jsonPath("$.startDate").value(
								DEFAULT_START_DATE.toString()))
				.andExpect(jsonPath("$.status").value(DEFAULT_STATUS.name()))
				.andExpect(
						jsonPath("$.validatedBy.login").value(user1.getLogin()))
				.andExpect(
						jsonPath("$.validatedDate").value(
								DEFAULT_VALIDATION_DATE
										.toString(dateTimeFormatter
												.withZoneUTC())))
				.andExpect(jsonPath("$.body").value(DEFAULT_BODY.toString()))
				.andExpect(
						jsonPath("$.organization.id").value(
								organization.getId().intValue()))
				.andExpect(
						jsonPath("$.redactor.id").value(
								redactor.getId().intValue()));
	}

	@Test
	@Transactional
	public void getNonExistingNews() throws Exception {
		// Get the news
		restNewsMockMvc.perform(get("/api/newss/{id}", 1L)).andExpect(
				status().isNotFound());
	}

	@Test
	@Transactional
	public void updateNews() throws Exception {
		// Initialize the database
		newsRepository.saveAndFlush(news);

		// Update the news
		news.setTitle(UPDATED_TITLE);
		news.setSummary(UPDATED_SUMMARY);
		news.setEnclosure(UPDATED_ENCLOSURE);
		news.setEndDate(UPDATED_END_DATE);
		news.setStartDate(UPDATED_START_DATE);
		news.setStatus(UPDATED_STATUS);
		news.setValidatedDate(UPDATED_VALIDATION_DATE);
        news.setValidatedBy(user2);
		news.setBody(UPDATED_BODY);
		restNewsMockMvc.perform(
				put("/api/newss").contentType(TestUtil.APPLICATION_JSON_UTF8)
						.content(TestUtil.convertObjectToJsonBytes(news)))
				.andExpect(status().isOk());

		// Validate the News in the database
		List<News> newss = newsRepository.findAll();
		assertThat(newss).hasSize(1);
		News testNews = newss.iterator().next();
		assertThat(testNews.getTitle()).isEqualTo(UPDATED_TITLE);
		assertThat(testNews.getSummary()).isEqualTo(UPDATED_SUMMARY);
		assertThat(testNews.getEnclosure()).isEqualTo(UPDATED_ENCLOSURE);
		assertThat(testNews.getEndDate()).isEqualTo(UPDATED_END_DATE);
		assertThat(testNews.getStartDate()).isEqualTo(UPDATED_START_DATE);
		assertThat(testNews.getStatus()).isEqualTo(UPDATED_STATUS);
		assertThat(testNews.getValidatedDate()).isEqualTo(UPDATED_VALIDATION_DATE);
		assertThat(testNews.getValidatedBy().getLogin()).isEqualTo(user2.getLogin());
		assertThat(testNews.getBody()).isEqualTo(UPDATED_BODY);
		assertThat(testNews.getRedactor()).isEqualTo(redactor);
		assertThat(testNews.getOrganization()).isEqualTo(organization);
		;
	}

	@Test
	@Transactional
	public void deleteNews() throws Exception {
		// Initialize the database
		newsRepository.saveAndFlush(news);

		// Get the news
		restNewsMockMvc.perform(
				delete("/api/newss/{id}", news.getId()).accept(
						TestUtil.APPLICATION_JSON_UTF8)).andExpect(
				status().isOk());

		// Validate the database is empty
		List<News> newss = newsRepository.findAll();
		assertThat(newss).hasSize(0);
	}
}
