package org.esupportail.publisher.repository;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
@Slf4j
public class ItemClassificationOrderRepositoryTest {

	@Inject
	private ItemClassificationOrderRepository repository;

	@Inject
	private ClassificationRepository<AbstractClassification> classifRepo;
	@Inject
	private ItemRepository<AbstractItem> itemRepo;
	@Inject
	private OrganizationRepository orgRepo;
	@Inject
	private PublisherRepository publisherRepo;
	@Inject
	private ReaderRepository readerRepo;
	@Inject
	private RedactorRepository redactorRepo;

	final static String INDICE_1 = "ind1";
	final static String INDICE_2 = "ind2";
	final static String INDICE_3 = "ind3";
	final static String INDICE_4 = "ind4";
	final static String URL = "https://lycees.netocentre.fr";

	private Organization org1;
	private Organization org2;
	@SuppressWarnings("unused")
	private Reader reader1;
	@SuppressWarnings("unused")
	private Reader reader2;
	private Redactor redactor1;
	private Redactor redactor2;
	private Publisher pub1;
	private Publisher pub2;
	private Category cat1;
	private Category cat2;
	private InternalFeed feed1;
	private InternalFeed feed2;
	private News news1;
	private News news2;
	@SuppressWarnings("unused")
	private ItemClassificationOrder ico1;
	@SuppressWarnings("unused")
	private ItemClassificationOrder ico2;

	@Before
	public void setUp() throws Exception {
		log.info("starting up " + this.getClass().getName());

		cat1 = ObjTest.newCategory(INDICE_1);
		pub1 = cat1.getPublisher();
		org1 = orgRepo.saveAndFlush(pub1.getContext().getOrganization());
		reader1 = readerRepo.saveAndFlush(pub1.getContext().getReader());
		redactor1 = redactorRepo.saveAndFlush(pub1.getContext().getRedactor());
		pub1 = publisherRepo.saveAndFlush(pub1);
		cat1 = classifRepo.saveAndFlush(cat1);

		cat2 = ObjTest.newCategory(INDICE_2);
		pub2 = cat2.getPublisher();
		org2 = orgRepo.saveAndFlush(pub2.getContext().getOrganization());
		reader2 = readerRepo.saveAndFlush(pub2.getContext().getReader());
		redactor2 = redactorRepo.saveAndFlush(pub2.getContext().getRedactor());
		pub2 = publisherRepo.saveAndFlush(pub2);
		cat2 = classifRepo.saveAndFlush(cat2);

		feed1 = classifRepo.saveAndFlush(ObjTest.newInternalFeed(INDICE_1,
				pub1, cat1));
		feed2 = classifRepo.saveAndFlush(ObjTest.newInternalFeed(INDICE_2,
				pub2, cat2));

		news1 = itemRepo.saveAndFlush(ObjTest
				.newNews(INDICE_1, org1, redactor1));
		Thread.sleep(2000);
		news2 = itemRepo.saveAndFlush(ObjTest
				.newNews(INDICE_2, org2, redactor2));

		ico1 = repository.saveAndFlush(new ItemClassificationOrder(news1,
				feed1, 25));
		ico2 = repository.saveAndFlush(new ItemClassificationOrder(news2,
				feed2, 0));
	}

	@Test
	public void testInsert() {
		ItemClassificationOrder ico3 = new ItemClassificationOrder(news1,
				feed2, 17);
		log.info("Before insert : " + ico3.toString());
		repository.saveAndFlush(ico3);
		assertNotNull(ico3.getDisplayOrder());
		assertNotNull(ico3.getItemClassificationId());
		assertNotNull(ico3.getItemClassificationId()
				.getAbstractClassification());
		assertNotNull(ico3.getItemClassificationId().getAbstractItem());
		log.info("After insert : " + ico3.toString());

		ItemClassificationOrder ico4 = repository.findOne(ico3
				.getItemClassificationId());
		log.info("After select : " + ico4.toString());
		assertNotNull(ico4);
		assertEquals(ico3, ico4);

		ico4.setDisplayOrder(0);
		ico4 = repository.saveAndFlush(ico4);
		log.info("After update : " + ico4.toString());

		ico4 = new ItemClassificationOrder(news2, feed1, 5);
		log.info("Before insert : " + ico4.toString());
		repository.save(ico4);
		log.info("After insert : " + ico4.toString());
		assertNotNull(ico4.getItemClassificationId());
		assertTrue(repository.exists(ico4.getItemClassificationId()));
		ico3 = repository.getOne(ico4.getItemClassificationId());
		assertNotNull(ico3);
		log.info("After select : " + ico3.toString());
		assertTrue(repository.count() == 4);

		List<ItemClassificationOrder> results = repository.findAll();
		assertThat(results.size(), is(4));
		assertThat(results, hasItem(ico4));

		repository.delete(ico4.getItemClassificationId());
		assertTrue(repository.findAll().size() == 3);
		assertFalse(repository.exists(ico4.getItemClassificationId()));

		ico4 = repository.findOne(new ItemClassificationKey());
		assertNull(ico4);

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findAll()}.
	 */
	@Test
	public void testFindAll() {
		assertThat(repository.findAll().size(), is(2));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#save(java.lang.Iterable)}
	 * .
	 */
	@Test
	public void testSaveIterableOfS() {
		repository.save(Arrays.asList(new ItemClassificationOrder(news1, feed2,
				28), new ItemClassificationOrder(news2, feed1, 1)));
		assertThat(repository.findAll().size(), is(4));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)}
	 * .
	 */
	@Test
	public void testExists() {
		assertTrue(repository.exists(repository.findAll().get(0)
				.getItemClassificationId()));

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#count()}.
	 */
	@Test
	public void testCount() {
		assertTrue(repository.count() == 2);
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#delete(java.lang.Object)}
	 * .
	 */
	@Test
	public void testDelete() {
		repository.delete(repository.findAll().get(0));
		assertTrue(repository.count() == 1);
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#deleteAll()}.
	 */
	@Test
	public void testDeleteAll() {
		repository.deleteAll();
		assertTrue(repository.count() == 0);
	}

	@Test
	public void testCustomOrderFeed() {
		InternalFeed feed3 = ObjTest.newInternalFeed(INDICE_3, pub1, cat1);
		feed3.setDefaultDisplayOrder(DisplayOrderType.CUSTOM);
		classifRepo.saveAndFlush(feed3);
		News news3 = itemRepo.saveAndFlush(ObjTest.newNews(INDICE_3, org1,
				redactor2));
		News news4 = itemRepo.saveAndFlush(ObjTest.newNews(INDICE_4, org1,
				redactor2));
		ItemClassificationOrder icoa = repository
				.saveAndFlush(new ItemClassificationOrder(news1, feed3, 1));
		ItemClassificationOrder icob = repository
				.saveAndFlush(new ItemClassificationOrder(news2, feed3, 25));
		ItemClassificationOrder icoc = repository
				.saveAndFlush(new ItemClassificationOrder(news3, feed3, 10));
		ItemClassificationOrder icod = repository
				.saveAndFlush(new ItemClassificationOrder(news4, feed3, 28));

		List<ItemClassificationOrder> results = Lists.newArrayList(repository
				.findAll(ItemPredicates.itemsClassOfClassification(feed3),
						ItemPredicates.orderByClassifDefinition(feed3
								.getDefaultDisplayOrder())));
		log.info("display Order Type : {}", feed3.getDefaultDisplayOrder());
		for (ItemClassificationOrder ico : results) {
			log.info("ICO : {}, {}, {}, {}, {}, {}", ico.getDisplayOrder(),
					ico.getItemClassificationId().getAbstractItem()
							.getStartDate(), ico.getItemClassificationId()
							.getAbstractItem().getEndDate(), ico
							.getItemClassificationId().getAbstractItem()
							.getCreatedDate(), ico.getItemClassificationId()
							.getAbstractItem().getLastModifiedDate(), ico
							.getItemClassificationId().getAbstractItem()
							.getTitle());
		}
		ItemClassificationOrder[] tab = { icod, icob, icoc, icoa };

		assertThat(results, IsIterableContainingInOrder.contains(tab));
	}

	@Test
	public void testCreatedModifiedFirstOrderFeed() throws InterruptedException {
		InternalFeed feed3 = ObjTest.newInternalFeed(INDICE_3, pub1, cat1);
		feed3.setDefaultDisplayOrder(DisplayOrderType.LAST_CREATED_MODIFIED_FIRST);
		classifRepo.saveAndFlush(feed3);
		News news3 = ObjTest.newNews(INDICE_3, org1, redactor2);
		News news4 = ObjTest.newNews(INDICE_4, org1, redactor2);

		Thread.sleep(2000);
		news3 = itemRepo.saveAndFlush(news3);
		Thread.sleep(2000);
		news4 = itemRepo.saveAndFlush(news4);
		Thread.sleep(2000);
		news1.setSummary(news1.getSummary() + "updated");
		news1 = itemRepo.saveAndFlush(news1);
		Thread.sleep(2000);
		news2.setSummary(news2.getSummary() + "updated");
		news2 = itemRepo.saveAndFlush(news2);

		ItemClassificationOrder icoa = repository
				.saveAndFlush(new ItemClassificationOrder(news1, feed3, 1));
		ItemClassificationOrder icob = repository
				.saveAndFlush(new ItemClassificationOrder(news2, feed3, 25));
		ItemClassificationOrder icoc = repository
				.saveAndFlush(new ItemClassificationOrder(news3, feed3, 10));
		ItemClassificationOrder icod = repository
				.saveAndFlush(new ItemClassificationOrder(news4, feed3, 1));

		List<ItemClassificationOrder> results = Lists.newArrayList(repository
				.findAll(ItemPredicates.itemsClassOfClassification(feed3),
						ItemPredicates.orderByClassifDefinition(feed3
								.getDefaultDisplayOrder())));
		log.info("display Order Type : {}", feed3.getDefaultDisplayOrder());
		for (ItemClassificationOrder ico : results) {
			log.info("ICO : {}, {}, {}, {}, {}, {}", ico
					.getItemClassificationId().getAbstractItem().getTitle(),
					ico.getItemClassificationId().getAbstractItem()
							.getCreatedDate(), ico.getItemClassificationId()
							.getAbstractItem().getLastModifiedDate(),
					ico.getDisplayOrder(), ico.getItemClassificationId()
							.getAbstractItem().getStartDate(), ico
							.getItemClassificationId().getAbstractItem()
							.getEndDate());
		}
		ItemClassificationOrder[] tab = { icob, icoa, icod, icoc };

		assertThat(results, IsIterableContainingInOrder.contains(tab));
	}

	@Test
	public void testNameOrderFeed() {
		InternalFeed feed3 = ObjTest.newInternalFeed(INDICE_3, pub1, cat1);
		feed3.setDefaultDisplayOrder(DisplayOrderType.NAME);
		classifRepo.saveAndFlush(feed3);
		News news3 = itemRepo.saveAndFlush(ObjTest.newNews(INDICE_3, org1,
				redactor2));
		News news4 = itemRepo.saveAndFlush(ObjTest.newNews(INDICE_4, org1,
				redactor2));
		ItemClassificationOrder icoa = repository
				.saveAndFlush(new ItemClassificationOrder(news1, feed3, 1));
		ItemClassificationOrder icob = repository
				.saveAndFlush(new ItemClassificationOrder(news2, feed3, 25));
		ItemClassificationOrder icoc = repository
				.saveAndFlush(new ItemClassificationOrder(news3, feed3, 10));
		ItemClassificationOrder icod = repository
				.saveAndFlush(new ItemClassificationOrder(news4, feed3, 10));

		List<ItemClassificationOrder> results = Lists.newArrayList(repository
				.findAll(ItemPredicates.itemsClassOfClassification(feed3),
						ItemPredicates.orderByClassifDefinition(feed3
								.getDefaultDisplayOrder())));
		log.info("display Order Type : {}", feed3.getDefaultDisplayOrder());
		for (ItemClassificationOrder ico : results) {
			log.info("ICO : {}, {}, {}, {}, {}, {}", ico.getDisplayOrder(),
					ico.getItemClassificationId().getAbstractItem()
							.getStartDate(), ico.getItemClassificationId()
							.getAbstractItem().getEndDate(), ico
							.getItemClassificationId().getAbstractItem()
							.getCreatedDate(), ico.getItemClassificationId()
							.getAbstractItem().getLastModifiedDate(), ico
							.getItemClassificationId().getAbstractItem()
							.getTitle());
		}
		ItemClassificationOrder[] tab = { icoa, icob, icoc, icod };

		assertThat(results, IsIterableContainingInOrder.contains(tab));
	}

	@Test
	public void testCreationOrderFeed() throws InterruptedException {
		InternalFeed feed3 = ObjTest.newInternalFeed(INDICE_3, pub1, cat1);
		feed3.setDefaultDisplayOrder(DisplayOrderType.ONLY_LAST_CREATED_FIRST);
		classifRepo.saveAndFlush(feed3);
		News news3 = ObjTest.newNews(INDICE_3, org1, redactor2);
		News news4 = ObjTest.newNews(INDICE_4, org1, redactor2);

		Thread.sleep(4000);
		news3 = itemRepo.saveAndFlush(news3);
		Thread.sleep(2000);
		news4 = itemRepo.saveAndFlush(news4);
		Thread.sleep(2000);
		news1 = itemRepo.saveAndFlush(news1);
		Thread.sleep(2000);
		news2 = itemRepo.saveAndFlush(news2);

		ItemClassificationOrder icoa = repository
				.saveAndFlush(new ItemClassificationOrder(news1, feed3, 1));
		ItemClassificationOrder icob = repository
				.saveAndFlush(new ItemClassificationOrder(news2, feed3, 25));
		ItemClassificationOrder icoc = repository
				.saveAndFlush(new ItemClassificationOrder(news3, feed3, 10));
		ItemClassificationOrder icod = repository
				.saveAndFlush(new ItemClassificationOrder(news4, feed3, 1));

		List<ItemClassificationOrder> results = Lists.newArrayList(repository
				.findAll(ItemPredicates.itemsClassOfClassification(feed3),
						ItemPredicates.orderByClassifDefinition(feed3
								.getDefaultDisplayOrder())));
		log.info("display Order Type : {}", feed3.getDefaultDisplayOrder());
		for (ItemClassificationOrder ico : results) {
			log.info("ICO : {}, {}, {}, {}, {}, {}", ico
					.getItemClassificationId().getAbstractItem().getTitle(),
					ico.getItemClassificationId().getAbstractItem()
							.getCreatedDate(), ico.getItemClassificationId()
							.getAbstractItem().getLastModifiedDate(),
					ico.getDisplayOrder(), ico.getItemClassificationId()
							.getAbstractItem().getStartDate(), ico
							.getItemClassificationId().getAbstractItem()
							.getEndDate());
		}
		ItemClassificationOrder[] tab = { icod, icoc, icob, icoa };

		assertThat(results, IsIterableContainingInOrder.contains(tab));
	}

	@Test
	public void testStartFirstOrderFeed() {
		InternalFeed feed3 = ObjTest.newInternalFeed(INDICE_3, pub1, cat1);
		feed3.setDefaultDisplayOrder(DisplayOrderType.START_DATE);
		classifRepo.saveAndFlush(feed3);
		News news3 = ObjTest.newNews(INDICE_3, org1, redactor2);
		News news4 = ObjTest.newNews(INDICE_4, org1, redactor2);

		news1.setStartDate(ObjTest.d4.toLocalDate());
		news1.setEndDate(ObjTest.d5.toLocalDate());
		news1 = itemRepo.saveAndFlush(news1);
		news2.setStartDate(ObjTest.d2.toLocalDate());
		news2.setEndDate(ObjTest.d3.toLocalDate());
		news2 = itemRepo.saveAndFlush(news2);
		news3.setStartDate(ObjTest.d1.toLocalDate());
		news3.setEndDate(ObjTest.d2.toLocalDate());
		news3 = itemRepo.saveAndFlush(news3);
		news4.setStartDate(ObjTest.d5.toLocalDate());
		news4.setEndDate(ObjTest.d6.toLocalDate());
		news4 = itemRepo.saveAndFlush(news4);

		ItemClassificationOrder icoa = repository
				.saveAndFlush(new ItemClassificationOrder(news1, feed3, 1));
		ItemClassificationOrder icob = repository
				.saveAndFlush(new ItemClassificationOrder(news2, feed3, 25));
		ItemClassificationOrder icoc = repository
				.saveAndFlush(new ItemClassificationOrder(news3, feed3, 10));
		ItemClassificationOrder icod = repository
				.saveAndFlush(new ItemClassificationOrder(news4, feed3, 1));

		List<ItemClassificationOrder> results = Lists.newArrayList(repository
				.findAll(ItemPredicates.itemsClassOfClassification(feed3),
						ItemPredicates.orderByClassifDefinition(feed3
								.getDefaultDisplayOrder())));
		log.info("display Order Type : {}", feed3.getDefaultDisplayOrder());
		for (ItemClassificationOrder ico : results) {
			log.info("ICO : {}, {}, {}, {}, {}, {}", ico
					.getItemClassificationId().getAbstractItem().getTitle(),
					ico.getItemClassificationId().getAbstractItem()
							.getStartDate(), ico.getItemClassificationId()
							.getAbstractItem().getEndDate(), ico
							.getItemClassificationId().getAbstractItem()
							.getCreatedDate(), ico.getItemClassificationId()
							.getAbstractItem().getLastModifiedDate(),
					ico.getDisplayOrder());
		}
		ItemClassificationOrder[] tab = { icod, icoa, icob, icoc };

		assertThat(results, IsIterableContainingInOrder.contains(tab));
	}

}
