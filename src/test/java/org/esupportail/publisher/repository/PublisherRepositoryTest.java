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

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Rollback
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

		Publisher e1 = new Publisher(org1, reader2, redactor1,"PUB " + INDICE_1,
            PermissionClass.CONTEXT, false, true, true);
		repository.saveAndFlush(e1);
		Publisher e2 = new Publisher(org2, reader1, redactor2,"PUB " + INDICE_2,
            PermissionClass.CONTEXT, false, true, true);
		repository.saveAndFlush(e2);
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void testUnique() {
		Publisher e = new Publisher(org1, reader2, redactor1, "PUB " + INDICE_3,
            PermissionClass.SUBJECT, true, true, true);
		repository.saveAndFlush(e);
		assertEquals(e,
				repository.findOne(PublisherPredicates.AllOfOrganization(org1)));
	}

	@Test
	public void testInsert() {
		Publisher e = new Publisher(org1, reader1, redactor1, "PUB " + INDICE_3,
            PermissionClass.CONTEXT, true, true, true);

		log.info("Before insert : " + e.toString());
		repository.save(e);
		assertNotNull(e.getId());
		log.info("After insert : " + e.toString());
		Optional<Publisher> optionalPublisher = repository.findById(e.getId());
		Publisher e2 = optionalPublisher == null || !optionalPublisher.isPresent()? null : optionalPublisher.get();
		log.info("After select : " + e2.toString());
		assertNotNull(e2);
		assertEquals(e, e2);

		e = new Publisher(org2, reader2, redactor2, "PUB " + INDICE_4,
            PermissionClass.CONTEXT_WITH_SUBJECTS, true, true, true);
		repository.save(e);
		log.info("After update : " + e.toString());
		assertNotNull(e.getId());
		assertTrue(repository.existsById(e.getId()));
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

		repository.deleteById(e.getId());
		log.debug("nb returned : {}", repository.findAll().size());
		assertThat(repository.findAll().size(), is(3));
		assertFalse(repository.existsById(e.getId()));
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
		assertTrue(repository.existsById(repository.findAll().get(0).getId()));

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
