package org.esupportail.publisher.repository;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.Reader;
import org.esupportail.publisher.repository.predicates.ReaderPredicates;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

/**
 * @author GIP RECIA - Julien Gribonvald 25 sept. 2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
@Slf4j
public class ReaderRepositoryTest {

	@Inject
	private ReaderRepository repository;

	@Before
	public void setUp() throws Exception {
		log.info("starting up " + this.getClass().getName());
		Reader r = ObjTest.newReader("1");
		repository.saveAndFlush(r);

		Reader r2 = ObjTest.newReader("2");
		repository.saveAndFlush(r2);
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void testUnique() {
		Reader r2 = ObjTest.newReader("1");
		repository.saveAndFlush(r2);
	}

	@Test
	public void testInsert() {
		Reader r = ObjTest.newReader("3");
		log.info("Before insert : " + r.toString());
		repository.saveAndFlush(r);
		assertNotNull(r.getId());
		log.info("After insert : " + r.toString());

		Reader r2 = repository.findOne(r.getId());
		log.info("After select : " + r2.toString());
		assertNotNull(r2);
		assertEquals(r, r2);

		r2.setName("TEST UPDATE");
		r2 = repository.saveAndFlush(r2);
		log.info("After update : " + r2.toString());
		assertTrue("TEST UPDATE".equals(r2.getName()));

		Reader r4 = ObjTest.newReader("4");
		log.info("Before insert : " + r4.toString());
		repository.save(r4);
		log.info("After insert : " + r4.toString());
		assertNotNull(r4.getId());
		assertTrue(repository.exists(r4.getId()));
		r = repository.getOne(r.getId());
		assertNotNull(r);
		log.info("After select : " + r.toString());
		assertTrue(repository.count() == 4);

		List<Reader> results = repository.findAll();
		assertThat(results.size(), is(4));
		assertThat(results, hasItem(r));

		r2 = repository.findByName(r.getName());
		assertNotNull(r2);

		results = Lists.newArrayList(repository.findAll(ReaderPredicates
				.sameName(r2)));
		assertTrue(results.isEmpty());

		repository.delete(r.getId());
		assertTrue(repository.findAll().size() == 3);
		assertFalse(repository.exists(r.getId()));

		r = repository.findOne((long) 0);
		assertNull(r);

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
		Reader e = ObjTest.newReader("init3");
		Reader e2 = ObjTest.newReader("init4");
		repository.save(Arrays.asList(e, e2));
		assertThat(repository.findAll().size(), is(4));
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

}
