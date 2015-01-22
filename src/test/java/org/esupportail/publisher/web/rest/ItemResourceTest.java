package org.esupportail.publisher.web.rest;

import com.google.common.collect.Lists;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.config.Constants;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.repository.*;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.FileService;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.UserDTO;
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
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the NewsResource REST controller.
 *
 * @see org.esupportail.publisher.web.rest.NewsResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ItemResourceTest {

	private static final String DEFAULT_TITLE = "SAMPLE_TEXT";
	private static final String UPDATED_TITLE = "UPDATED_TEXT";

	private static final String DEFAULT_SUMMARY = "SAMPLE_TEXT";
	private static final String UPDATED_SUMMARY = "UPDATED_TEXT";

	private static final String DEFAULT_ENCLOSURE = "http://un.domaine.fr/path/file.jpg";
	private static final String UPDATED_ENCLOSURE = "http://deux.domaine.fr/path/media.png";

	private static final LocalDate DEFAULT_END_DATE = LocalDate.now().plusDays(2);
	private static final LocalDate UPDATED_END_DATE = LocalDate.now().plusMonths(1);

	private static final LocalDate DEFAULT_START_DATE = LocalDate.now();
	private static final LocalDate UPDATED_START_DATE = LocalDate.now().minusDays(1);

	private static final ItemStatus DEFAULT_STATUS = ItemStatus.DRAFT;
	private static final ItemStatus UPDATED_STATUS = ItemStatus.PENDING;

	private static final DateTime DEFAULT_VALIDATION_DATE = ObjTest.d1;
	private static final DateTime UPDATED_VALIDATION_DATE = ObjTest.d1.plusDays(1);

	private static final String DEFAULT_BODY = "SAMPLE_TEXT";
	private static final String UPDATED_BODY = "UPDATED_TEXT";
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

	@Inject
	private ItemRepository<AbstractItem> itemRepository;

	private MockMvc restNewsMockMvc;

	private AbstractItem item;

	@Inject
	private OrganizationRepository organizationRepository;
	private Organization organization;
	@Inject
	private RedactorRepository redactorRepository;
	private Redactor redactor;

    @Inject
    private UserRepository userRepo;
    @Inject
    private UserDTOFactory userDTOFactory;
    private User user1;private User user2;private User user3;

    @Inject
    private IPermissionService permissionService;

	@PostConstruct
	public void setup() {
		MockitoAnnotations.initMocks(this);
		ItemResource itemResource = new ItemResource();
		OrganizationResource organizationResource = new OrganizationResource();
        FileService fileservice = new FileService();
		RedactorResource redactorResource = new RedactorResource();
		ReflectionTestUtils.setField(itemResource, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemResource, "permissionService", permissionService);
		ReflectionTestUtils.setField(itemResource, "fileService", fileservice);
		ReflectionTestUtils.setField(organizationResource, "organizationRepository", organizationRepository);
		ReflectionTestUtils.setField(redactorResource, "redactorRepository", redactorRepository);
		this.restNewsMockMvc = MockMvcBuilders.standaloneSetup(itemResource, organizationResource).build();

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
		redactor = redactorRepository.saveAndFlush(ObjTest.newRedactor(name));
        user1 = userRepo.findOne(ObjTest.subject1);
        user2 = userRepo.findOne(ObjTest.subject2);
        user3 = userRepo.findOne(ObjTest.subject3);



        News news = new News();
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

		item = news;
	}

	@Test
	@Transactional
	public void createItem() throws Exception {
		// Validate the database is empty
		assertThat(itemRepository.findAll()).hasSize(0);

		// Create the Item
		restNewsMockMvc.perform(
				post("/api/items").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                    TestUtil.convertObjectToJsonBytes(item))).andExpect(status().isCreated());

		// Validate the News in the database
		List<AbstractItem> items = itemRepository.findAll();
		assertThat(items).hasSize(1);
		AbstractItem item = items.iterator().next();
		org.junit.Assert.assertThat(item, instanceOf(News.class));
		News testNews = (News) item;
		assertThat(testNews.getTitle()).isEqualTo(DEFAULT_TITLE);
		assertThat(testNews.getSummary()).isEqualTo(DEFAULT_SUMMARY);
		assertThat(testNews.getEnclosure()).isEqualTo(DEFAULT_ENCLOSURE);
		assertThat(testNews.getEndDate()).isEqualTo(DEFAULT_END_DATE);
		assertThat(testNews.getStartDate()).isEqualTo(DEFAULT_START_DATE);
		assertThat(testNews.getStatus()).isEqualTo(DEFAULT_STATUS);
		assertThat(testNews.getValidatedDate()).isEqualTo(DEFAULT_VALIDATION_DATE);
		assertThat(testNews.getValidatedBy().getLogin()).isEqualTo(ObjTest.subject1);
		assertThat(testNews.getBody()).isEqualTo(DEFAULT_BODY);
		assertThat(testNews.getRedactor()).isEqualTo(redactor);
		assertThat(testNews.getOrganization()).isEqualTo(organization);

	}

	@Test
	@Transactional
	public void getAllItems() throws Exception {
		// Initialize the database
		itemRepository.saveAndFlush(item);

		// Get all the Items
		restNewsMockMvc
				.perform(get("/api/items"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.[0].id").value(item.getId().intValue()))
				.andExpect(jsonPath("$.[0].title").value(DEFAULT_TITLE.toString()))
				.andExpect(jsonPath("$.[0].summary").value(DEFAULT_SUMMARY.toString()))
				.andExpect(jsonPath("$.[0].enclosure").value(DEFAULT_ENCLOSURE.toString()))
				.andExpect(jsonPath("$.[0].endDate").value(DEFAULT_END_DATE.toString()))
				.andExpect(jsonPath("$.[0].startDate").value(DEFAULT_START_DATE.toString()))
				.andExpect(jsonPath("$.[0].status").value(DEFAULT_STATUS.getName()))
				.andExpect(jsonPath("$.[0].validatedBy.subject.keyId").value(ObjTest.subject1))
            .andExpect(
                jsonPath("$.[0].validatedDate").value(
								DEFAULT_VALIDATION_DATE.toString(dateTimeFormatter.withZoneUTC())))
                .andExpect(jsonPath("$.[0].body").value(DEFAULT_BODY.toString()))
				.andExpect(jsonPath("$.[0].organization.id").value(organization.getId().intValue()))
				.andExpect(jsonPath("$.[0].redactor.id").value(redactor.getId().intValue()));
	}
    @Test
    @Transactional
    public void getAllItemsOfStatusOfUser() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the Items of Status
        restNewsMockMvc
            .perform(get("/api/items/").requestAttr("item_status", DEFAULT_STATUS.getId()).requestAttr("owned", true))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].createdBy.subject.keyId").value(Constants.SYSTEM_ACCOUNT.toString()))
            .andExpect(jsonPath("$.[0].id").value(item.getId().intValue()))
            .andExpect(jsonPath("$.[0].title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.[0].summary").value(DEFAULT_SUMMARY.toString()))
            .andExpect(jsonPath("$.[0].enclosure").value(DEFAULT_ENCLOSURE.toString()))
            .andExpect(jsonPath("$.[0].endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.[0].startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.[0].status").value(DEFAULT_STATUS.getName()))
            .andExpect(jsonPath("$.[0].validatedBy.subject.keyId").value(ObjTest.subject1))
            .andExpect(
                jsonPath("$.[0].validatedDate").value(
                    DEFAULT_VALIDATION_DATE.toString(dateTimeFormatter.withZoneUTC())))
                .andExpect(jsonPath("$.[0].body").value(DEFAULT_BODY.toString()))
            .andExpect(jsonPath("$.[0].organization.id").value(organization.getId().intValue()))
                .andExpect(jsonPath("$.[0].redactor.id").value(redactor.getId().intValue()));
    }


	@Test
	@Transactional
	public void getItems() throws Exception {
		// Initialize the database
		itemRepository.saveAndFlush(item);

		// Get the item
		restNewsMockMvc
				.perform(get("/api/items/{id}", item.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(item.getId().intValue()))
				.andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
				.andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY.toString()))
				//.andExpect(jsonPath("$.enclosure").value(DEFAULT_ENCLOSURE.toString()))
				.andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
				.andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
				.andExpect(jsonPath("$.status").value(DEFAULT_STATUS.getName()))
				.andExpect(jsonPath("$.validatedBy.subject.keyId").value(ObjTest.subject1))
            .andExpect(
                jsonPath("$.validatedDate").value(
								DEFAULT_VALIDATION_DATE.toString(dateTimeFormatter.withZoneUTC())))
                //.andExpect(jsonPath("$.body").value(DEFAULT_BODY.toString()))
				.andExpect(jsonPath("$.organization.id").value(organization.getId().intValue()))
				.andExpect(jsonPath("$.redactor.id").value(redactor.getId().intValue()));
	}

	@Test
	@Transactional
	public void getNonExistingItem() throws Exception {
		// Get the news
		restNewsMockMvc.perform(get("/api/items/{id}", 1L)).andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void updateItem() throws Exception {
		// Initialize the database
		itemRepository.saveAndFlush(item);

		// Update the news
		item.setTitle(UPDATED_TITLE);
		item.setSummary(UPDATED_SUMMARY);
		item.setEnclosure(UPDATED_ENCLOSURE);
		item.setEndDate(UPDATED_END_DATE);
		item.setStartDate(UPDATED_START_DATE);
		item.setStatus(UPDATED_STATUS);
		item.setValidatedDate(UPDATED_VALIDATION_DATE);
        item.setValidatedBy(user2);
		((News) item).setBody(UPDATED_BODY);
		restNewsMockMvc.perform(
				put("/api/items").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                    TestUtil.convertObjectToJsonBytes(item))).andDo(print()).andExpect(status().isOk());

		// Validate the News in the database
		List<AbstractItem> items = itemRepository.findAll();
		assertThat(items).hasSize(1);
		AbstractItem item = items.iterator().next();
		org.junit.Assert.assertThat(item, instanceOf(News.class));
		News testNews = (News) item;
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
	public void deleteItem() throws Exception {
		// Initialize the database
		itemRepository.saveAndFlush(item);

		// Get the news
		restNewsMockMvc.perform(delete("/api/items/{id}", item.getId()).accept(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());

		// Validate the database is empty
		List<AbstractItem> items = itemRepository.findAll();
		assertThat(items).hasSize(0);
	}
}
