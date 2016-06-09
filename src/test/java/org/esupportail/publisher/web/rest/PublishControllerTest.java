package org.esupportail.publisher.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.domain.enums.SubscribeType;
import org.esupportail.publisher.repository.*;
import org.esupportail.publisher.service.SubscriberService;
import org.esupportail.publisher.service.factories.CategoryProfileFactory;
import org.esupportail.publisher.service.factories.FlashInfoVOFactory;
import org.esupportail.publisher.service.factories.ItemVOFactory;
import org.esupportail.publisher.service.factories.RubriqueVOFactory;
import org.esupportail.publisher.web.PublishController;
import org.joda.time.LocalDate;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by jgribonvald on 08/06/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@Slf4j
@Transactional
public class PublishControllerTest {

    private MockMvc restPublishControllerMockMvc;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private PublisherRepository publisherRepository;

    @Inject
    private ClassificationRepository<AbstractClassification> classificationRepository;

    @Inject
    private CategoryRepository categoryRepository;
//
//    @Inject
//    private FeedRepository<AbstractFeed> feedRepository;

    @Inject
    private ReaderRepository readerRepository;

    @Inject
    private RedactorRepository redactorRepository;

    @Inject
    private ItemRepository<AbstractItem> itemRepository;

    @Inject
    private FlashRepository flashRepository;

    @Inject
    private ItemClassificationOrderRepository itemClassificationOrderRepository;

    @Inject
    private FlashInfoVOFactory flashInfoVOFactory;

    @Inject
    private RubriqueVOFactory rubriqueVOFactory;

    @Inject
    private ItemVOFactory itemVOFactory;

    @Inject
    private CategoryProfileFactory categoryProfileFactory;

    @Inject
    private SubscriberService subscriberService;

    @Inject
    private SubscriberRepository subscriberRepository;


    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PublishController publishController = new PublishController();

        ReflectionTestUtils.setField(publishController, "organizationRepository", organizationRepository);
        ReflectionTestUtils.setField(publishController, "publisherRepository", publisherRepository);
        ReflectionTestUtils.setField(publishController, "flashRepository", flashRepository);
        ReflectionTestUtils.setField(publishController, "categoryRepository", categoryRepository);
        ReflectionTestUtils.setField(publishController, "classificationRepository", classificationRepository);
        ReflectionTestUtils.setField(publishController, "itemClassificationOrderRepository", itemClassificationOrderRepository);
        ReflectionTestUtils.setField(publishController, "rubriqueVOFactory", rubriqueVOFactory);
        ReflectionTestUtils.setField(publishController, "itemVOFactory", itemVOFactory);
        ReflectionTestUtils.setField(publishController, "categoryProfileFactory", categoryProfileFactory);
        ReflectionTestUtils.setField(publishController, "flashInfoVOFactory", flashInfoVOFactory);
        ReflectionTestUtils.setField(publishController, "subscriberService", subscriberService);


        this.restPublishControllerMockMvc = MockMvcBuilders.standaloneSetup(publishController).build();
    }

    private Publisher newWay;
    private Publisher flashInfo;
    private Organization organization;

    @Before
    public void initTest() {
        organization = new Organization();
        organization.setDescription("A Desc");
        organization.setDisplayOrder(200);
        organization.setName("A TESTER" );
        organization.setDisplayName("À tester");

        organization = organizationRepository.saveAndFlush(organization);
        Reader reader1 = ObjTest.newReader("1");
        reader1 = readerRepository.saveAndFlush(reader1);
        Reader reader2 = ObjTest.newReader("2");
        reader2 = readerRepository.saveAndFlush(reader2);
        Redactor redactor1 = ObjTest.newRedactor("1");
        redactor1 = redactorRepository.saveAndFlush(redactor1);
        Redactor redactor2 = ObjTest.newRedactor("2");
        redactor2 = redactorRepository.saveAndFlush(redactor2);
        newWay = new Publisher(organization, reader1, redactor1, PermissionClass.CONTEXT, true,true);
        newWay = publisherRepository.saveAndFlush(newWay);
        flashInfo = new Publisher(organization, reader2, redactor2, PermissionClass.CONTEXT, true,false);
        flashInfo = publisherRepository.saveAndFlush(flashInfo);
        // number of cats in publisher newWay is needed in getItemsFromPublisherTest
        Category cat1 = ObjTest.newCategory("Cat1", newWay);
        cat1 = categoryRepository.saveAndFlush(cat1);
        Category cat2 = ObjTest.newCategory("cat2", newWay);
        cat2 = categoryRepository.saveAndFlush(cat2);
        Category cat3 = ObjTest.newCategory("cat3", flashInfo);
        cat3 = categoryRepository.saveAndFlush(cat3);
        News news1 = ObjTest.newNews("news 1", organization, newWay.getContext().getRedactor());
        news1.setEndDate(LocalDate.now().plusMonths(1));
        news1.setStatus(ItemStatus.PUBLISHED);
        news1 = itemRepository.saveAndFlush(news1);
        News news2 = ObjTest.newNews("news 2", organization, newWay.getContext().getRedactor());
        news2.setEndDate(LocalDate.now().plusMonths(1));
        news2.setStatus(ItemStatus.PUBLISHED);
        news2 = itemRepository.saveAndFlush(news2);
        News news3 = ObjTest.newNews("news 3", organization, newWay.getContext().getRedactor());
        news3.setStatus(ItemStatus.PUBLISHED);
        news3.setEndDate(LocalDate.now().plusMonths(1));
        news3 = itemRepository.saveAndFlush(news3);
        News news4 = ObjTest.newNews("news 4", organization, newWay.getContext().getRedactor());
        news4.setStatus(ItemStatus.ARCHIVED);
        news4.setEndDate(LocalDate.now().minusDays(1));
        news4 = itemRepository.saveAndFlush(news4);
        Flash flash = ObjTest.newFlash("news 1", organization, flashInfo.getContext().getRedactor());
        flash.setStatus(ItemStatus.PUBLISHED);
        flash.setEndDate(LocalDate.now().plusMonths(1));
        flash = itemRepository.saveAndFlush(flash);

        itemClassificationOrderRepository.saveAndFlush(new ItemClassificationOrder(news1, cat1, 0));
        itemClassificationOrderRepository.saveAndFlush(new ItemClassificationOrder(news2, cat1, 1));
        itemClassificationOrderRepository.saveAndFlush(new ItemClassificationOrder(news3, cat2, 2));
        itemClassificationOrderRepository.saveAndFlush(new ItemClassificationOrder(news4, cat2, 3));
        itemClassificationOrderRepository.saveAndFlush(new ItemClassificationOrder(flash, cat3, 0));

        Subscriber sub = ObjTest.newSubscriberPerson(organization.getContextKey());
        sub.setSubscribeType(SubscribeType.FORCED);
        Subscriber sub2 = ObjTest.newSubscriberGroup(news1.getContextKey());
        Subscriber sub3 = ObjTest.newSubscriberGroup(news2.getContextKey());
        Subscriber sub4 = ObjTest.newSubscriberPerson(news3.getContextKey());
        Subscriber sub5 = ObjTest.newSubscriberPerson(news4.getContextKey());

        sub = subscriberRepository.saveAndFlush(sub);
        sub2 = subscriberRepository.saveAndFlush(sub2);
        sub3 = subscriberRepository.saveAndFlush(sub3);
        sub4 = subscriberRepository.saveAndFlush(sub4);
        sub5 = subscriberRepository.saveAndFlush(sub5);


    }

    @Test
    public void getAllPlublisherContextTest() throws Exception {
        restPublishControllerMockMvc.perform(get("/published/contexts/{reader_id}/{redactor_id}", newWay.getContext().getReader().getId(), newWay.getContext().getRedactor().getId())
            .accept(MediaType.APPLICATION_XML)).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_XML))
            .andExpect(xpath("/categoryProfilesUrl/categoryProfile/visibility/obliged").exists())
            .andExpect(xpath("/categoryProfilesUrl/categoryProfile/visibility/allowed").exists())
            .andExpect(xpath("/categoryProfilesUrl/categoryProfile/visibility/autoSubscribed").exists())
            .andExpect(xpath("/categoryProfilesUrl/categoryProfile/visibility/obliged/regular").exists());
            //.andExpect(xpath("/categoryProfilesUrl//categoryProfile/visibility//").exists());
    }

    @Test
    public void getItemsFromPublisherTest() throws Exception {
        restPublishControllerMockMvc.perform(get("/published/items/{publisher_id}", newWay.getId())
            .accept(MediaType.APPLICATION_XML)).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_XML))
            .andExpect(xpath("/actualites/rubriques/*").nodeCount(2))
            .andExpect(xpath("/actualites/items/*").nodeCount(3))
            .andExpect(xpath("/actualites/items/item/article/pubDate").exists())
            .andExpect(xpath("/actualites/items/item/rubriques/uuid").exists())
            .andExpect(xpath("/actualites/items/item/visibility").exists())
            .andExpect(xpath("/actualites/items/item/visibility/obliged").exists())
            .andExpect(xpath("/actualites/items/item/visibility/obliged/regular").exists())
            .andExpect(xpath("/actualites/items/item/visibility/allowed").exists())
            .andExpect(xpath("/actualites/items/item/visibility/autoSubscribed").exists())
        ;
    }

}
