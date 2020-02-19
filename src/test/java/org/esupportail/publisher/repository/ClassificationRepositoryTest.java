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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.Category;
import org.esupportail.publisher.domain.ExternalFeed;
import org.esupportail.publisher.domain.InternalFeed;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.Reader;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.repository.predicates.ClassificationPredicates;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author GIP RECIA - Julien Gribonvald 3 oct. 2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Rollback
@Transactional
@Slf4j
public class ClassificationRepositoryTest {

	@Inject
	private ClassificationRepository<AbstractClassification> repository;

    @Inject
    private CategoryRepository catRepo;

	@Inject
	private OrganizationRepository orgRepo;
	@Inject
	private PublisherRepository publisherRepo;
	@Inject
	private ReaderRepository readerRepo;
	@Inject
	private RedactorRepository redactorRepo;

	// @Inject
	// private UserRepository userRepo;

	final static String INDICE_1 = "ind1";
	final static String INDICE_2 = "ind2";
	final static String INDICE_3 = "ind3";
	final static String INDICE_4 = "ind4";
	final static String URL = "https://lycees.netocentre.fr";
	final static String UPDATED_URL = "https://update.netocentre.fr";

	private Organization org1;
	private Organization org2;
	private Reader reader1;
	private Reader reader2;
	private Redactor redactor1;
	private Redactor redactor2;
	private Publisher pub1;
	private Publisher pub2;
    private Category cat1;

	@Before
	public void setUp() throws Exception {
		log.info("starting up {}", this.getClass().getName());

		// userRepo.save(new User(ObjTest.subject1, "test"));
		// userRepo.save(new User(ObjTest.subject2, "test"));

		org1 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_1));
		org2 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_2));

		reader1 = readerRepo.saveAndFlush(ObjTest.newReader(INDICE_1));
		reader2 = readerRepo.saveAndFlush(ObjTest.newReader(INDICE_2));

		redactor1 = redactorRepo.saveAndFlush(ObjTest.newRedactor(INDICE_1));
		redactor2 = redactorRepo.saveAndFlush(ObjTest.newRedactor(INDICE_2));

		pub1 = new Publisher(org1, reader2, redactor1, "PUB " + INDICE_1,
            PermissionClass.CONTEXT, false, true, true);
		pub1 = publisherRepo.saveAndFlush(pub1);
		pub2 = new Publisher(org2, reader1, redactor2,"PUB " + INDICE_2,
            PermissionClass.CONTEXT, false, true, true);
		pub2 = publisherRepo.saveAndFlush(pub2);

		cat1 = new Category(true, "CAT " + INDICE_1, "ICON_URL"
				+ INDICE_1, "fr_fr", 3600, 200, AccessType.PUBLIC, "A DESC"
				+ INDICE_1, DisplayOrderType.NAME, "#F44336", pub1);
		cat1 = catRepo.saveAndFlush(cat1);

		InternalFeed c2 = new InternalFeed(true, "CAT " + INDICE_2, "ICON_URL"
				+ INDICE_2, "fr_fr", 3600, 200, AccessType.AUTHENTICATED,
				"A DESC" + INDICE_2, DisplayOrderType.START_DATE, "#F44336", pub2, cat1);
		repository.saveAndFlush(c2);

		ExternalFeed c3 = new ExternalFeed(true, "CAT " + INDICE_3, "ICON_URL"
				+ INDICE_3, "fr_fr", 3600, 200, AccessType.AUTHENTICATED,
				"A DESC" + INDICE_3, DisplayOrderType.START_DATE, "#F44336", pub2, cat1, URL);
		repository.saveAndFlush(c3);

	}

	// @Test
	// public void testDTO() {
	// List<AbstractClassification> classList = repository.findAll();
	// log.debug("models before : {}", classList);
	// AbstractClassification m = classList.get(0);
	// log.debug("model before all : {}", m);
	// AbstractClassification c = classList.get(0);
	// m.setIconUrl(UPDATED_URL);
	// assertThat(m, not(samePropertyValuesAs(c)));
	// log.debug("model before saved modif : {}", m);
	// m = repository.save(m);
	// log.debug("model after saved modif : {}", m);
	// AbstractClassification c2 = repository.findOne(m.getId());
	// assertThat(c2, samePropertyValuesAs(m));
	// assertThat(c2, not(samePropertyValuesAs(c)));
	// }

	@Test(expected = javax.validation.ConstraintViolationException.class)
	public void testBadURL() {
		ExternalFeed c3 = new ExternalFeed(true, "CAT " + INDICE_4, "ICON_URL"
				+ INDICE_4, "fr_fr", 3600, 200, AccessType.AUTHENTICATED,
				"A DESC" + INDICE_4, DisplayOrderType.START_DATE, "#F44336", pub2,
				(Category) repository.getOne(cat1.getId()), "RSS_URL");
		repository.saveAndFlush(c3);
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void testDuplicateCategoryName() {
		Category c1 = new Category(true, "CAT " + INDICE_1, "ICON_URL"
				+ INDICE_1, "fr_fr", 3600, 200, AccessType.PUBLIC, "A DESC"
				+ INDICE_1, DisplayOrderType.NAME, "#F44336", pub1);
		repository.saveAndFlush(c1);
	}

	@Test
	public void testInsert() {
		Category c1 = new Category(true, "CAT " + INDICE_4, "ICON_URL"
				+ INDICE_4, "fr_fr", 3600, 200, AccessType.PUBLIC, "A DESC"
				+ INDICE_4, DisplayOrderType.NAME, "#F44336", pub1);
		repository.saveAndFlush(c1);

		log.info("Before insert : {}", c1);
		repository.save(c1);
		assertNotNull(c1.getId());
		log.info("After insert : {}", c1);
		Optional<AbstractClassification> optionalClassif = repository.findById(c1.getId());
		Category c2 = (Category) optionalClassif.orElse(null);
		log.info("After select {} ", c2);
		assertNotNull(c2);
		assertEquals(c1, c2);

		c2.setName("UPDATED CAT");
		c2.setDefaultDisplayOrder(DisplayOrderType.ONLY_LAST_CREATED_FIRST);
		c2 = repository.save(c2);
		log.info("After update : {}", c2);
		assertTrue(repository.existsById(c2.getId()));
		c2 = (Category) repository.getOne(c2.getId());
		assertNotNull(c2);
		log.info("After select : {}", c2);
        assertEquals(4, repository.count());

		List<AbstractClassification> result = repository.findAll();
		assertThat(result.size(), is(4));
		assertThat(result, hasItem(c2));
		result = Lists.newArrayList(repository.findAll(ClassificationPredicates
				.CategoryClassificationOfPublisher(pub1.getId())));
		assertThat(result.size(), is(2));
		assertThat(result, hasItem(c2));

		repository.deleteById(c2.getId());
		log.debug("nb returned : {}", repository.findAll().size());
		assertThat(repository.findAll().size(), is(3));
		assertFalse(repository.existsById(c2.getId()));
	}

	@Test
    public void testOrderLastModifiedCreated() throws InterruptedException {
        Thread.sleep(1000);
        Category cat = ObjTest.newCategory(INDICE_4, pub1);
        cat = repository.saveAndFlush(cat);
        Thread.sleep(1000);
        Category cat2 = ObjTest.newCategory("A name", pub1);
        cat2 = repository.saveAndFlush(cat2);

        Category[] tab = { cat2, cat, cat1 };

        Arrays.stream(tab).forEach(c -> log.debug("Category order {}, {}, {}", c.getName(), c.getCreatedDate(), c.getLastModifiedDate()));

        List<Category> result = Lists.newArrayList(catRepo.findAll(ClassificationPredicates
            .CategoryOfPublisher(pub1.getId()), ClassificationPredicates.categoryOrderByDisplayOrderType(DisplayOrderType.LAST_CREATED_MODIFIED_FIRST)));

        result.forEach(c -> log.debug("Obtained Category order {}, {}, {}", c.getName(), c.getCreatedDate(), c.getLastModifiedDate()));

        assertThat(result, IsIterableContainingInOrder.contains(tab));

        cat.setColor("blue");
        cat = repository.saveAndFlush(cat);

        Category[] tab2 = { cat, cat2, cat1 };

        result = Lists.newArrayList(catRepo.findAll(ClassificationPredicates
            .CategoryOfPublisher(pub1.getId()), ClassificationPredicates.categoryOrderByDisplayOrderType(DisplayOrderType.LAST_CREATED_MODIFIED_FIRST)));

        assertThat(result, IsIterableContainingInOrder.contains(tab2));
    }

    @Test
    public void testOrderName() {
        Category cat = ObjTest.newCategory(INDICE_4, pub1);
        cat = repository.saveAndFlush(cat);
        Category cat2 = ObjTest.newCategory("A name", pub1);
        cat2 = repository.saveAndFlush(cat2);

        Category[] tab = { cat2, cat1, cat };

        List<Category> result = Lists.newArrayList(catRepo.findAll(ClassificationPredicates
            .CategoryOfPublisher(pub1.getId()), ClassificationPredicates.categoryOrderByDisplayOrderType(DisplayOrderType.NAME)));

        assertThat(result, IsIterableContainingInOrder.contains(tab));
    }

    @Test
    public void testOrderLastCreatedFirst() {
        Category cat = ObjTest.newCategory(INDICE_4, pub1);
        cat = repository.saveAndFlush(cat);
        Category cat2 = ObjTest.newCategory("A name", pub1);
        cat2 = repository.saveAndFlush(cat2);

        Category[] tab = { cat1, cat, cat2 };

        List<Category> result = Lists.newArrayList(catRepo.findAll(ClassificationPredicates
            .CategoryOfPublisher(pub1.getId()), ClassificationPredicates.categoryOrderByDisplayOrderType(DisplayOrderType.ONLY_LAST_CREATED_FIRST)));

        assertThat(result, IsIterableContainingInOrder.contains(tab));
    }

    @Test
    public void testOrderStartDate() {
	    // there is no startdate ! it's falling back to createdDate
        Category cat = ObjTest.newCategory(INDICE_4, pub1);
        cat = repository.saveAndFlush(cat);
        Category cat2 = ObjTest.newCategory("A name", pub1);
        cat2 = repository.saveAndFlush(cat2);

        Category[] tab = { cat1, cat, cat2 };

        List<Category> result = Lists.newArrayList(catRepo.findAll(ClassificationPredicates
            .CategoryOfPublisher(pub1.getId()), ClassificationPredicates.categoryOrderByDisplayOrderType(DisplayOrderType.START_DATE)));

        assertThat(result, IsIterableContainingInOrder.contains(tab));
    }

    @Test
    public void testOrderCustom() {
        Category cat = ObjTest.newCategory(INDICE_4, pub1);
        cat.setDisplayOrder(cat1.getDisplayOrder() + 5);
        cat = repository.saveAndFlush(cat);
        Category cat2 = ObjTest.newCategory("A name", pub1);
        cat2.setDisplayOrder(cat1.getDisplayOrder() - 5 );
        cat2 = repository.saveAndFlush(cat2);

        Category[] tab = { cat, cat1, cat2 };

        List<Category> result = Lists.newArrayList(catRepo.findAll(ClassificationPredicates
            .CategoryOfPublisher(pub1.getId()), ClassificationPredicates.categoryOrderByDisplayOrderType(DisplayOrderType.CUSTOM)));

        assertThat(result, IsIterableContainingInOrder.contains(tab));
    }

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findAll()}.
	 */
	@Test
	public void testFindAll() {
		assertThat(repository.findAll().size(), is(3));
	}

    /**
     * Test method for eager path of Category with publisher
     */
    @Test
    public void testFindAllOfPublisher() {
        assertTrue(repository.count(ClassificationPredicates.CategoryClassificationOfPublisher(pub1.getId())) == 1);
    }

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#existsById(Object)}
	 * .
	 */
	@Test
	public void testExists() {
		assertTrue(repository.existsById(repository.findAll().get(0).getId()));

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#count()}.
	 */
	@Test
	public void testCount() {
		assertTrue(repository.count() == 3);
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#delete(java.lang.Object)}
	 * .
	 */
	@Test
	public void testDelete() {
		Category c1 = new Category(true, "CAT " + INDICE_4, "ICON_URL"
				+ INDICE_4, "fr_fr", 3600, 200, AccessType.PUBLIC, "A DESC"
				+ INDICE_4, DisplayOrderType.NAME, "#F44336", pub1);
		repository.saveAndFlush(c1);
		long count = repository.count();
		repository.delete(c1);
		assertTrue(repository.count() == count - 1);
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

}
