package org.esupportail.publisher.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.Filter;
import org.esupportail.publisher.domain.Organization;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
@Slf4j
public class FilterRepositoryTest {

	@Inject
	private FilterRepository repository;

	@Inject
	private OrganizationRepository orgRepo;

	final static String FILTER_INDICE_1 = "filter1";
	final static String FILTER_LDAP_PATTERN = "(ESCOUAI=0450822X)";
	final static String FILTER_GROUP_PATTERN = "esco:Etablissements:FICTIF_0450822X";

	@Before
	public void setUp() throws Exception {
		log.info("starting up " + this.getClass().getName());

		Organization o = orgRepo.saveAndFlush(ObjTest
				.newOrganization(FILTER_INDICE_1));

		Filter f = ObjTest.newFilterLDAP(FILTER_LDAP_PATTERN, o);
		log.info("Before insert : " + f.toString());
		repository.saveAndFlush(f);
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void testInsertDuplicate() {
		Organization o = orgRepo.findAll().get(0);

		Filter f = ObjTest.newFilterLDAP(FILTER_LDAP_PATTERN, o);
		log.info("Before insert : " + f.toString());
		repository.saveAndFlush(f);

	}

	@Test()
	public void testInsert() {
		Organization o = orgRepo.findAll().get(0);

		Filter f = ObjTest.newFilterGroup(FILTER_GROUP_PATTERN, o);
		log.info("Before insert : " + f.toString());
		repository.saveAndFlush(f);

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findAll()}.
	 */
	@Test
	public void testFindAll() {
		Organization o = orgRepo.findAll().get(0);
		repository
				.saveAndFlush(ObjTest.newFilterGroup(FILTER_GROUP_PATTERN, o));
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
		assertTrue(repository.count() == 1);
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
