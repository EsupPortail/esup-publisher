package org.esupportail.publisher.web;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Category;
import org.esupportail.publisher.domain.InternalFeed;
import org.esupportail.publisher.domain.ItemClassificationOrder;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.Reader;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.repository.CategoryRepository;
import org.esupportail.publisher.repository.ClassificationRepository;
import org.esupportail.publisher.repository.ItemClassificationOrderRepository;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.ObjTest;
import org.esupportail.publisher.repository.OrganizationRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.ReaderRepository;
import org.esupportail.publisher.repository.RedactorRepository;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.esupportail.publisher.service.factories.impl.PublisherAtomFeedView;
import org.esupportail.publisher.service.factories.impl.PublisherRssFeedView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Transactional
@Slf4j
public class FeedControlerTest {

    private MockMvc mockMvc;

    @Inject
    private ServiceUrlHelper urlHelper;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private ReaderRepository readerRepository;

    @Inject
    private RedactorRepository redactorRepository;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private PublisherRepository publisherRepository;

	@Inject
	private ItemRepository<AbstractItem> itemRepo;

    @Inject
    private ClassificationRepository<AbstractClassification> classificationRepository;

    @Inject
    private ItemClassificationOrderRepository itemClassificationOrderRepository;

    private PublisherRssFeedView publisherRssFeedView;
    private PublisherAtomFeedView publisherAtomFeedView;

    private String url;

    @Before
    public void setup() {
    	FeedController feedController = new FeedController();
    	publisherRssFeedView = new PublisherRssFeedView();
    	publisherAtomFeedView= new PublisherAtomFeedView();

        ReflectionTestUtils.setField(feedController, "organizationRepository", organizationRepository);
        ReflectionTestUtils.setField(feedController, "publisherRepository", publisherRepository);
        ReflectionTestUtils.setField(feedController, "classificationRepository", classificationRepository);
        ReflectionTestUtils.setField(feedController, "itemClassificationOrderRepository", itemClassificationOrderRepository);
        ReflectionTestUtils.setField(publisherRssFeedView, "urlHelper", urlHelper);
        ReflectionTestUtils.setField(publisherAtomFeedView, "urlHelper", urlHelper);
    	this.mockMvc = MockMvcBuilders.standaloneSetup(feedController, publisherRssFeedView).build();

    	Organization organization = organizationRepository.saveAndFlush(ObjTest.newOrganization("1"));
    	Reader reader = readerRepository.saveAndFlush(ObjTest.newReader("2"));
    	Redactor redactor = redactorRepository.saveAndFlush(ObjTest.newRedactor("3"));

    	Publisher pub = ObjTest.newPublisher("4");
    	pub.getContext().setOrganization(organization);
    	pub.getContext().setReader(reader);
    	pub.getContext().setRedactor(redactor);

        Publisher p1 = publisherRepository.saveAndFlush(pub);
        Long idOrganisation = organization.getId();
        Long idPublisher = p1.getId();

        Category category = categoryRepository.saveAndFlush(ObjTest.newCategory("5", p1));

        InternalFeed internalFeed = ObjTest.newInternalFeed("6", p1);
        internalFeed.setParent(category);

        InternalFeed intFeed = classificationRepository.saveAndFlush(internalFeed);
        Long idClassif = intFeed.getId();

        InternalFeed feed1 = classificationRepository.saveAndFlush(ObjTest.newInternalFeed("7", p1, category));
        InternalFeed feed2 = classificationRepository.saveAndFlush(ObjTest.newInternalFeed("8", p1, category));

        News news1 = itemRepo.saveAndFlush(ObjTest.newNewsPublished("9", organization, redactor));
		News news2 = itemRepo.saveAndFlush(ObjTest.newNewsPublished("10", organization, redactor));

		itemClassificationOrderRepository.saveAndFlush(new ItemClassificationOrder(news1, feed1, 25));
		itemClassificationOrderRepository.saveAndFlush(new ItemClassificationOrder(news2, feed2, 0));

        StringBuilder complementUrl = new StringBuilder();
        complementUrl.append(idOrganisation.toString());
        complementUrl.append("?");
        complementUrl.append("pid=").append(idPublisher.toString());
//        complementUrl.append("&");
//        complementUrl.append("cid=").append(idClassif.toString());

        url = complementUrl.toString();
    }

    @Test
    public void getRssFeed() throws Exception {

        MvcResult result = mockMvc
        		.perform(get("/feed/rss/"+url))
        		.andExpect(status().isOk())
        		.andExpect(forwardedUrl("publisherRssFeedView"))
        		.andReturn();

        MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		publisherRssFeedView.render(result.getModelAndView().getModelMap(), request, response);
        assertEquals("application/rss+xml", response.getContentType());
    }

    @Test
    public void getAtomFeed() throws Exception {

        MvcResult result = mockMvc
        		.perform(get("/feed/atom/"+url))
        		.andExpect(status().isOk())
        		.andExpect(forwardedUrl("publisherAtomFeedView"))
        		.andReturn();

        MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		publisherAtomFeedView.render(Objects.requireNonNull(result.getModelAndView()).getModelMap(), request, response);
        assertEquals("application/atom+xml", response.getContentType());

    }

}
