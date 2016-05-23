package org.esupportail.publisher.repository;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.repository.predicates.ClassificationPredicates;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author GIP RECIA - Julien Gribonvald 3 oct. 2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
@Slf4j
public class ClassificationRepositoryTest {

	@Inject
	private ClassificationRepository<AbstractClassification> repository;

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

	@Before
	public void setUp() throws Exception {
		log.info("starting up " + this.getClass().getName());

		// userRepo.save(new User(ObjTest.subject1, "test"));
		// userRepo.save(new User(ObjTest.subject2, "test"));

		org1 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_1));
		org2 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_2));

		reader1 = readerRepo.saveAndFlush(ObjTest.newReader(INDICE_1));
		reader2 = readerRepo.saveAndFlush(ObjTest.newReader(INDICE_2));

		redactor1 = redactorRepo.saveAndFlush(ObjTest.newRedactor(INDICE_1));
		redactor2 = redactorRepo.saveAndFlush(ObjTest.newRedactor(INDICE_2));

		pub1 = new Publisher(org1, reader2, redactor1,
            PermissionClass.CONTEXT, false, true);
		pub1 = publisherRepo.saveAndFlush(pub1);
		pub2 = new Publisher(org2, reader1, redactor2,
            PermissionClass.CONTEXT, false, true);
		pub2 = publisherRepo.saveAndFlush(pub2);

		Category c1 = new Category(true, "CAT " + INDICE_1, "ICON_URL"
				+ INDICE_1, "fr_fr", 3600, 200, AccessType.PUBLIC, "A DESC"
				+ INDICE_1, DisplayOrderType.NAME, pub1);
		repository.saveAndFlush(c1);

		InternalFeed c2 = new InternalFeed(true, "CAT " + INDICE_2, "ICON_URL"
				+ INDICE_2, "fr_fr", 3600, 200, AccessType.AUTHENTICATED,
				"A DESC" + INDICE_2, DisplayOrderType.START_DATE, pub2, c1);
		repository.saveAndFlush(c2);

		ExternalFeed c3 = new ExternalFeed(true, "CAT " + INDICE_3, "ICON_URL"
				+ INDICE_3, "fr_fr", 3600, 200, AccessType.AUTHENTICATED,
				"A DESC" + INDICE_3, DisplayOrderType.START_DATE, pub2, c1, URL);
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
				"A DESC" + INDICE_4, DisplayOrderType.START_DATE, pub2,
				(Category) repository.findOne(ClassificationPredicates
						.CategoryClassification()), "RSS_URL");
		repository.saveAndFlush(c3);
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void testDuplicateCategoryName() {
		Category c1 = new Category(true, "CAT " + INDICE_1, "ICON_URL"
				+ INDICE_1, "fr_fr", 3600, 200, AccessType.PUBLIC, "A DESC"
				+ INDICE_1, DisplayOrderType.NAME, pub1);
		repository.saveAndFlush(c1);
	}

	@Test
	public void testInsert() {
		Category c1 = new Category(true, "CAT " + INDICE_4, "ICON_URL"
				+ INDICE_4, "fr_fr", 3600, 200, AccessType.PUBLIC, "A DESC"
				+ INDICE_4, DisplayOrderType.NAME, pub1);
		repository.saveAndFlush(c1);

		log.info("Before insert : " + c1.toString());
		repository.save(c1);
		assertNotNull(c1.getId());
		log.info("After insert : " + c1.toString());

		Category c2 = (Category) repository.findOne(c1.getId());
		log.info("After select : " + c2.toString());
		assertNotNull(c2);
		assertEquals(c1, c2);

		c2.setName("UPDATED CAT");
		c2.setDefaultDisplayOrder(DisplayOrderType.ONLY_LAST_CREATED_FIRST);
		c2 = repository.save(c2);
		log.info("After update : " + c2.toString());
		assertTrue(repository.exists(c2.getId()));
		c2 = (Category) repository.getOne(c2.getId());
		assertNotNull(c2);
		log.info("After select : " + c2.toString());
		assertTrue(repository.count() == 4);

		List<AbstractClassification> result = repository.findAll();
		assertThat(result.size(), is(4));
		assertThat(result, hasItem(c2));
		result = Lists.newArrayList(repository.findAll(ClassificationPredicates
				.CategoryClassificationOfPublisher(pub1.getId())));
		assertThat(result.size(), is(2));
		assertThat(result, hasItem(c2));

		repository.delete(c2.getId());
		log.debug("nb returned : {}", repository.findAll().size());
		assertThat(repository.findAll().size(), is(3));
		assertFalse(repository.exists(c2.getId()));
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
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)}
	 * .
	 */
	@Test
	public void testExists() {
		assertTrue(repository.exists(repository.findAll().get(0).getId()));

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
				+ INDICE_4, DisplayOrderType.NAME, pub1);
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
