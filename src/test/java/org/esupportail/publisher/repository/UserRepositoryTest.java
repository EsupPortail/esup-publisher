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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.time.Instant;
import java.util.Optional;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.User;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Rollback
@Transactional
@Slf4j
public class UserRepositoryTest {
	@Autowired
	private UserRepository repository;

	@BeforeEach
	public void setUp() throws Exception {
		log.info("starting up {}", this.getClass().getName());
        assertThat(repository.findAll().size(), is(4));
		/*User u = new User(ObjTest.subject1, "Prénom NOM");
		log.info("Before insert : {}", u);
		u = repository.saveAndFlush(u);
		u = new User(ObjTest.subject2, "Prénom NOM");
		log.info("Before insert : {}", u);
		u = repository.saveAndFlush(u);*/
	}

	@Test
	public void testInserted() {
        long count = repository.count();
		Instant d = Instant.now();
		Optional<User> optionalUser = repository.findById(ObjTest.subject1);
		User u = optionalUser == null || !optionalUser.isPresent()? null : optionalUser.get();
		log.info("loaded user :{}", u);
		assertThat(u, notNullValue());
		log.debug("{} test equals with {}", ObjTest.subjectKey1, u.getSubject());
		assertThat(u.getSubject(), equalTo(ObjTest.subjectKey1));
		assertThat(u.getCreatedDate(), notNullValue());
		log.info("{} is same or before  {}", d, u.getCreatedDate());
		assertThat(u.getCreatedDate(), is(lessThanOrEqualTo(d)));

		User u2 = new User(ObjTest.subject1, "Prénom NOM");
		assertThat(u, equalTo(u2));

		u.setAcceptNotifications(true);
		repository.saveAndFlush(u);
		Optional<User> optionalU = repository.findById(ObjTest.subject2);
		u = optionalU == null || !optionalU.isPresent()? null : optionalU.get();
		assertThat(u, notNullValue());
		assertThat(u, not(equalTo(u2)));
		u2.setAcceptNotifications(true);
		// assertEquals(u, u2);

		repository.saveAndFlush(u);
		Optional<User> optionalU1 = repository.findById(ObjTest.subject1);
		u = (optionalU1 == null || !optionalU1.isPresent()) ? null : optionalU1.get();
		assertThat(u, notNullValue());
		assertThat(u, equalTo(u2));
		assertThat(u, equalTo(u2));

		u.setEnabled(false);
		repository.saveAndFlush(u);
		Optional<User> optionalU2 = repository.findById(ObjTest.subject1);
		u2 = optionalU2 == null || !optionalU2.isPresent()? null : optionalU2.get();
		assertThat(u2, notNullValue());
		assertThat(u2, equalTo(u));
		u2.setEnabled(true);
		assertThat(u2, equalTo(u));

		repository.deleteById(u2.getLogin());
        assertThat(repository.count(), equalTo(count - 1));
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
		assertThat(repository.existsById(repository.findAll().get(0).getLogin()), is(true));

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#count()}.
	 */
	@Test
	public void testCount() {
		assertThat(repository.count(), equalTo(4L));
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
		assertThat(repository.count(), equalTo(count - 1));
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

}