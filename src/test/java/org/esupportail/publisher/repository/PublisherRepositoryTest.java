package org.esupportail.publisher.repository;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.Reader;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.repository.predicates.PublisherPredicates;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
@Slf4j
public class PublisherRepositoryTest {

	@Inject
	private PublisherRepository repository;

	@Inject
	private OrganizationRepository orgRepo;
	@Inject
	private ReaderRepository readerRepo;
	@Inject
	private RedactorRepository redactorRepo;

	final static String INDICE_1 = "ind1";
	final static String INDICE_2 = "ind2";
	final static String INDICE_3 = "ind3";
	final static String INDICE_4 = "ind4";

	private Organization org1;
	private Organization org2;
	private Reader reader1;
	private Reader reader2;
	private Redactor redactor1;
	private Redactor redactor2;

	@Before
	public void setUp() throws Exception {
		log.info("starting up " + this.getClass().getName());

		org1 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_1));
		org2 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_2));

		reader1 = readerRepo.saveAndFlush(ObjTest.newReader(INDICE_1));
		reader2 = readerRepo.saveAndFlush(ObjTest.newReader(INDICE_2));

		redactor1 = redactorRepo.saveAndFlush(ObjTest.newRedactor(INDICE_1));
		redactor2 = redactorRepo.saveAndFlush(ObjTest.newRedactor(INDICE_2));

		Publisher e1 = new Publisher(org1, reader2, redactor1,
            PermissionClass.CONTEXT, false);
		repository.saveAndFlush(e1);
		Publisher e2 = new Publisher(org2, reader1, redactor2,
            PermissionClass.CONTEXT, false);
		repository.saveAndFlush(e2);
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void testUnique() {
		Publisher e = new Publisher(org1, reader2, redactor1,
            PermissionClass.SUBJECT, true);
		repository.saveAndFlush(e);
		assertEquals(e,
				repository.findOne(PublisherPredicates.AllOfOrganization(org1)));
	}

	@Test
	public void testInsert() {
		Publisher e = new Publisher(org1, reader1, redactor1,
            PermissionClass.CONTEXT, true);

		log.info("Before insert : " + e.toString());
		repository.save(e);
		assertNotNull(e.getId());
		log.info("After insert : " + e.toString());

		Publisher e2 = repository.findOne(e.getId());
		log.info("After select : " + e2.toString());
		assertNotNull(e2);
		assertEquals(e, e2);

		e = new Publisher(org2, reader2, redactor2,
            PermissionClass.CONTEXT_WITH_SUBJECTS, true);
		repository.save(e);
		log.info("After update : " + e.toString());
		assertNotNull(e.getId());
		assertTrue(repository.exists(e.getId()));
		e = repository.getOne(e.getId());
		assertNotNull(e);
		log.info("After select : " + e.toString());
		assertTrue(repository.count() == 4);

		List<Publisher> result = repository.findAll();
		assertThat(result.size(), is(4));
		assertThat(result, hasItem(e));
		result = Lists.newArrayList(repository.findAll(PublisherPredicates
				.AllOfOrganization(org2)));
		assertThat(result.size(), is(2));
		assertThat(result, hasItem(e));

		repository.delete(e.getId());
		log.debug("nb returned : {}", repository.findAll().size());
		assertThat(repository.findAll().size(), is(3));
		assertFalse(repository.exists(e.getId()));
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
		assertTrue(repository.count() == 2);
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#delete(java.lang.Object)}
	 * .
	 */
	@Test
	public void testDelete() {
		long count = repository.count();
		repository.delete(repository.findAll().get(0));
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
