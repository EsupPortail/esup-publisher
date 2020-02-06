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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Optional;

import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Rollback
@Transactional
@Slf4j
public class UserRepositoryTest {

	@Autowired
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
		Instant d = Instant.now();
		Optional<User> optionalUser = repository.findById(ObjTest.subject1);
		User u = optionalUser == null || !optionalUser.isPresent()? null : optionalUser.get();
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
		Optional<User> optionalU = repository.findById(ObjTest.subject2);
		u = optionalU == null || !optionalU.isPresent()? null : optionalU.get();
		assertFalse(u.equals(u2));
		u2.setAcceptNotifications(true);
		// assertEquals(u, u2);

		repository.saveAndFlush(u);
		Optional<User> optionalU1 = repository.findById(ObjTest.subject1);
		u = optionalU1 == null || !optionalU1.isPresent()? null : optionalU1.get();
		assertTrue(u.equals(u2));
		assertEquals(u, u2);

		u.setEnabled(false);
		repository.saveAndFlush(u);
		Optional<User> optionalU2 = repository.findById(ObjTest.subject1);
		u2 = optionalU2 == null || !optionalU2.isPresent()? null : optionalU2.get();
		assertTrue(u.equals(u2));
		u2.setEnabled(true);
		assertTrue(u.equals(u2));

		repository.deleteById(u2.getLogin());
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
	 * {@link org.springframework.data.repository.CrudRepository#existsById(Object)}
	 * .
	 */
	@Test
	public void testExists() {
		assertTrue(repository.existsById(repository.findAll().get(0).getLogin()));

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
