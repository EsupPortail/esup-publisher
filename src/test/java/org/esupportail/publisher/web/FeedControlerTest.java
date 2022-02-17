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
package org.esupportail.publisher.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.esupportail.publisher.domain.enums.DisplayOrderType;
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

import com.rometools.rome.io.impl.DateParser;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

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

    private Publisher publisher;
    private News news1;
    private News news2;

    private String url;

    @Before
    public void setup() throws InterruptedException {
        System.setProperty("file.encoding","UTF-8");
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

        publisher = ObjTest.newPublisher("4");
        publisher.getContext().setOrganization(organization);
        publisher.getContext().setReader(reader);
        publisher.getContext().setRedactor(redactor);
        publisher.setDefaultDisplayOrder(DisplayOrderType.START_DATE);

        Publisher p1 = publisherRepository.saveAndFlush(publisher);
        Long idOrganisation = organization.getId();
        Long idPublisher = p1.getId();

        Category category = categoryRepository.saveAndFlush(ObjTest.newCategory("5", p1));

        InternalFeed internalFeed = ObjTest.newInternalFeed("6", p1);
        internalFeed.setParent(category);

        InternalFeed intFeed = classificationRepository.saveAndFlush(internalFeed);
        Long idClassif = intFeed.getId();

        InternalFeed feed1 = classificationRepository.saveAndFlush(ObjTest.newInternalFeed("7", p1, category));
        InternalFeed feed2 = classificationRepository.saveAndFlush(ObjTest.newInternalFeed("8", p1, category));

        news2 = ObjTest.newNewsPublished("à tester 10", organization, redactor);
        news2.setStartDate(news2.getStartDate().minusDays(2));
        news2 = itemRepo.saveAndFlush(news2);
        Thread.sleep(1000);
        news1 = ObjTest.newNewsPublished("à voir 9", organization, redactor);
        news1.setStartDate(news1.getStartDate().minusDays(1));
        news1 = itemRepo.saveAndFlush(news1);

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
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
        		.andExpect(forwardedUrl("publisherRssFeedView"))
        		.andReturn();

        MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		publisherRssFeedView.render(result.getModelAndView().getModelMap(), request, response);
        assertEquals("application/rss+xml", response.getContentType());
        log.debug("RSS feed result: {}", response.getContentAsString(StandardCharsets.UTF_8));
        log.debug("Forwarded URL: '{}'", result.getResponse().getForwardedUrl());

        // testing xpath result
        InputSource source = new InputSource(new StringReader(response.getContentAsString(StandardCharsets.UTF_8)));
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);

        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/title", startsWith("Contenus publiés par l'établissement")));
        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/link", not(emptyOrNullString())));
        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/description",
                equalTo("Contenus limités au contexte de publication : " + publisher.getDisplayName())));
        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/pubDate", not(emptyOrNullString())));
        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/docs", equalTo(publisherRssFeedView.getDocRssUrl())));
        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/generator", equalTo(publisherRssFeedView.getChannelGenerator())));
        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/item[1]"));
        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/item[2]"));
        assertThat(document.getDocumentElement(), not(hasXPath("/rss/channel/item[3]")));
        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/item[1]/title", equalTo(news1.getTitle())));
        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/item[1]/link", not(emptyOrNullString())));
        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/item[1]/description", equalTo(news1.getSummary())));
        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/item[1]/source", equalTo(publisherRssFeedView.getChannelGenerator())));
        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/item[1]/enclosure/@url", not(emptyOrNullString())));
        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/item[1]/category", not(emptyOrNullString())));
        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/item[1]/pubDate",
                equalTo(DateParser.formatRFC822(Date.from(news1.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()), Locale.US))));
        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/item[1]/author", equalTo(news1.getCreatedBy().getDisplayName())));
        assertThat(document.getDocumentElement(), hasXPath("/rss/channel/item[1]/guid", not(emptyOrNullString())));
    }

    @Test
    public void getAtomFeed() throws Exception {

        MvcResult result = mockMvc
        		.perform(get("/feed/atom/"+url))
                .andDo(MockMvcResultHandlers.print())
        		.andExpect(status().isOk())
        		.andExpect(forwardedUrl("publisherAtomFeedView"))
        		.andReturn();

        MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		publisherAtomFeedView.render(Objects.requireNonNull(result.getModelAndView()).getModelMap(), request, response);
        assertEquals("application/atom+xml", response.getContentType());
        log.debug("RSS feed result: {}", response.getContentAsString(StandardCharsets.UTF_8));
        log.debug("Forwarded URL: '{}'", result.getResponse().getForwardedUrl());

        // testing xpath result
        InputSource source = new InputSource(new StringReader(response.getContentAsString(StandardCharsets.UTF_8)));
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);

        assertThat(document.getDocumentElement(), hasXPath("/feed/title", startsWith("Contenus publiés par l'établissement")));
        assertThat(document.getDocumentElement(), hasXPath("/feed/link/@href", not(emptyOrNullString())));
        assertThat(document.getDocumentElement(), hasXPath("/feed/subtitle",
                equalTo("Contenus limités au contexte de publication : " + publisher.getDisplayName())));
        assertThat(document.getDocumentElement(), hasXPath("/feed/generator", equalTo(publisherRssFeedView.getChannelGenerator())));
        assertThat(document.getDocumentElement(), hasXPath("/feed/updated", not(emptyOrNullString())));
        assertThat(document.getDocumentElement(), hasXPath("/feed/entry[1]"));
        assertThat(document.getDocumentElement(), hasXPath("/feed/entry[2]"));
        assertThat(document.getDocumentElement(), not(hasXPath("/feed/entry[3]")));
        assertThat(document.getDocumentElement(), hasXPath("/feed/entry[1]/title", equalTo(news1.getTitle())));
        assertThat(document.getDocumentElement(), hasXPath("/feed/entry[1]/link[@rel=\"alternate\"]/@href", not(emptyOrNullString())));
        assertThat(document.getDocumentElement(), hasXPath("/feed/entry[1]/link[@rel=\"enclosure\"]/@href", not(emptyOrNullString())));
        assertThat(document.getDocumentElement(), hasXPath("/feed/entry[1]/category/@label", not(emptyOrNullString())));
        assertThat(document.getDocumentElement(), hasXPath("/feed/entry[1]/author/name", equalTo(news1.getCreatedBy().getDisplayName())));
        assertThat(document.getDocumentElement(), hasXPath("/feed/entry[1]/author/email", equalTo(news1.getCreatedBy().getEmail())));
        assertThat(document.getDocumentElement(), hasXPath("/feed/entry[1]/id", equalTo(news1.getId().toString())));
        assertThat(document.getDocumentElement(), hasXPath("/feed/entry[1]/updated",
                equalTo(DateParser.formatW3CDateTime(Date.from(news1.getLastModifiedDate()), Locale.US))));
        assertThat(document.getDocumentElement(), hasXPath("/feed/entry[1]/published",
                equalTo(DateParser.formatW3CDateTime(Date.from(news1.getValidatedDate()), Locale.US))));
        assertThat(document.getDocumentElement(), hasXPath("/feed/entry[1]/summary", equalTo(news1.getSummary())));
    }

}