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
package org.esupportail.publisher.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Category;
import org.esupportail.publisher.domain.InternalFeed;
import org.esupportail.publisher.domain.ItemClassificationKey;
import org.esupportail.publisher.domain.ItemClassificationOrder;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.Reader;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.repository.predicates.ItemPredicates;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Rollback
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

	@BeforeEach
	public void setUp() throws Exception {
		log.info("starting up {}", this.getClass().getName());

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
		log.info("Before insert : {}", ico3);
		repository.saveAndFlush(ico3);
		assertThat(ico3.getDisplayOrder(), notNullValue());
		assertThat(ico3.getItemClassificationId(), notNullValue());
		assertThat(ico3.getItemClassificationId().getAbstractClassification(), notNullValue());
		assertThat(ico3.getItemClassificationId().getAbstractItem(), notNullValue());
		log.info("After insert : {}", ico3);
		Optional<ItemClassificationOrder> optionalItemClassif = repository.findById(ico3.getItemClassificationId());
		ItemClassificationOrder ico4 = optionalItemClassif.orElse(null);
		log.info("After select : {}", ico4);
		assertThat(ico4, notNullValue());
		assertThat(ico3, equalTo(ico4));

		ico4.setDisplayOrder(0);
		ico4 = repository.saveAndFlush(ico4);
		log.info("After update : {}", ico4);

		ico4 = new ItemClassificationOrder(news2, feed1, 5);
		log.info("Before insert : {}", ico4);
		repository.save(ico4);
		log.info("After insert : {}", ico4);
		assertThat(ico4.getItemClassificationId(), notNullValue());
		assertThat(repository.existsById(ico4.getItemClassificationId()), is(true));
		ico3 = repository.getReferenceById(ico4.getItemClassificationId());
		assertThat(ico3, notNullValue());
		log.info("After select : {}", ico3);
        assertThat(repository.count(), equalTo(4L));

		List<ItemClassificationOrder> results = repository.findAll();
		assertThat(results.size(), is(4));
		assertThat(results, hasItem(ico4));

		repository.deleteById(ico4.getItemClassificationId());
        assertThat(repository.findAll().size(), equalTo(3));
		assertThat(repository.existsById(ico4.getItemClassificationId()), is(false));

		Optional<ItemClassificationOrder> optionalItem = repository.findById(new ItemClassificationKey());
		ico4 = optionalItem.orElse(null);
		assertThat(ico4, is(nullValue()));

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
	 * {@link org.springframework.data.jpa.repository.JpaRepository#saveAll(java.lang.Iterable)}
	 * .
	 */
	@Test
	public void testSaveIterableOfS() {
		repository.saveAll(Arrays.asList(new ItemClassificationOrder(news1, feed2,
				28), new ItemClassificationOrder(news2, feed1, 1)));
		assertThat(repository.findAll().size(), is(4));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#existsById(Object)}
	 * .
	 */
	@Test
	public void testExists() {
		assertThat(repository.existsById(repository.findAll().get(0)
				.getItemClassificationId()), is(true));

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#count()}.
	 */
	@Test
	public void testCount() {
        assertThat(repository.count(), equalTo(2L));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#delete(java.lang.Object)}
	 * .
	 */
	@Test
	public void testDelete() {
		repository.delete(repository.findAll().get(0));
        assertThat(repository.count(), equalTo(1L));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#deleteAll()}.
	 */
	@Test
	public void testDeleteAll() {
		repository.deleteAll();
        assertThat(repository.count(), equalTo(0L));
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
			log.info("ICO : {}, {}, {}, {}, {}, {}",
					ico.getItemClassificationId().getAbstractItem()
							.getCreatedDate(), ico.getItemClassificationId()
							.getAbstractItem().getLastModifiedDate(),
					ico.getDisplayOrder(), ico.getItemClassificationId()
							.getAbstractItem().getStartDate(), ico
							.getItemClassificationId().getAbstractItem()
							.getEndDate(), ico.getItemClassificationId()
                    .getAbstractItem().getTitle());
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
			log.info("ICO : {}, {}, {}, {}, {}, {}", ico
                    .getItemClassificationId().getAbstractItem()
                    .getTitle(), ico.getDisplayOrder(),
					ico.getItemClassificationId().getAbstractItem()
							.getStartDate(), ico.getItemClassificationId()
							.getAbstractItem().getEndDate(), ico
							.getItemClassificationId().getAbstractItem()
							.getCreatedDate(), ico.getItemClassificationId()
							.getAbstractItem().getLastModifiedDate());
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
			log.info("ICO : {}, {}, {}, {}, {}, {}",
					ico.getItemClassificationId().getAbstractItem()
							.getCreatedDate(), ico.getItemClassificationId()
							.getAbstractItem().getLastModifiedDate(),
					ico.getDisplayOrder(), ico.getItemClassificationId()
							.getAbstractItem().getStartDate(), ico
							.getItemClassificationId().getAbstractItem()
							.getEndDate(),ico
                    .getItemClassificationId().getAbstractItem().getTitle());
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

		news1.setStartDate(ObjTest.instantToLocalDate(ObjTest.d4));
		news1.setEndDate(ObjTest.instantToLocalDate(ObjTest.d5));
		news1 = itemRepo.saveAndFlush(news1);
		news2.setStartDate(ObjTest.instantToLocalDate(ObjTest.d2));
		news2.setEndDate(ObjTest.instantToLocalDate(ObjTest.d3));
		news2 = itemRepo.saveAndFlush(news2);
		news3.setStartDate(ObjTest.instantToLocalDate(ObjTest.d1));
		news3.setEndDate(ObjTest.instantToLocalDate(ObjTest.d2));
		news3 = itemRepo.saveAndFlush(news3);
		news4.setStartDate(ObjTest.instantToLocalDate(ObjTest.d5));
		news4.setEndDate(ObjTest.instantToLocalDate(ObjTest.d6));
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