package org.esupportail.publisher.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.User;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
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
public class UserRepositoryTest {

	@Inject
	private UserRepository repository;

	@Before
	public void setUp() throws Exception {
		log.info("starting up " + this.getClass().getName());
        assertThat(repository.findAll().size(), is(4));
		/*User u = new User(ObjTest.subject1, "Prénom NOM");
		log.info("Before insert : " + u.toString());
		u = repository.saveAndFlush(u);
		u = new User(ObjTest.subject2, "Prénom NOM");
		log.info("Before insert : " + u.toString());
		u = repository.saveAndFlush(u);*/
	}

	@Test
	public void testInserted() {
        long count = repository.count();
		DateTime d = new DateTime();
		User u = repository.findOne(ObjTest.subject1);
		log.debug("loaded user :" + u.toString());
		assertNotNull(u);
		log.debug(ObjTest.subjectKey1.toString() + " test equals with "
				+ u.getSubject().toString());
		assertTrue(ObjTest.subjectKey1.equals(u.getSubject()));
		assertNotNull(u.getCreatedDate());
		log.debug(d.toString() + " is same or before  "
				+ u.getCreatedDate().toString());
		assertTrue(d.compareTo(u.getCreatedDate()) >= 0);

		User u2 = new User(ObjTest.subject1, "Prénom NOM");
		assertEquals(u, u2);

		u.setAcceptNotifications(true);
		repository.saveAndFlush(u);
		u = repository.findOne(ObjTest.subject2);
		assertFalse(u.equals(u2));
		u2.setAcceptNotifications(true);
		// assertEquals(u, u2);

		repository.saveAndFlush(u);
		u = repository.findOne(ObjTest.subject1);
		assertTrue(u.equals(u2));
		assertEquals(u, u2);

		u.setEnabled(false);
		repository.saveAndFlush(u);
		u2 = repository.findOne(ObjTest.subject1);
		assertTrue(u.equals(u2));
		u2.setEnabled(true);
		assertTrue(u.equals(u2));

		repository.delete(u2.getLogin());
        assertTrue(repository.count() == count - 1);
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findAll()}.
	 */
	@Test
	public void testFindAll() {
		assertThat(repository.findAll().size(), is(4));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)}
	 * .
	 */
	@Test
	public void testExists() {
		assertTrue(repository.exists(repository.findAll().get(0).getLogin()));

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#count()}.
	 */
	@Test
	public void testCount() {
		assertTrue(repository.count() == 4);
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
