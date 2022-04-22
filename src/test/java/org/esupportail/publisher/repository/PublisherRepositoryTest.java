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

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

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
	Publisher publisher;

	@BeforeEach
	public void setUp() throws Exception {
		log.info("starting up {}", this.getClass().getName());

		org1 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_1));
		org2 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_2));

		reader1 = readerRepo.saveAndFlush(ObjTest.newReader(INDICE_1));
		reader2 = readerRepo.saveAndFlush(ObjTest.newReader(INDICE_2));

		redactor1 = redactorRepo.saveAndFlush(ObjTest.newRedactor(INDICE_1));
		redactor2 = redactorRepo.saveAndFlush(ObjTest.newRedactor(INDICE_2));

		publisher = new Publisher(org1, reader2, redactor1,"PUB " + INDICE_1,
            PermissionClass.CONTEXT, false, true, true);
		repository.saveAndFlush(publisher);
		Publisher e2 = new Publisher(org2, reader1, redactor2,"PUB " + INDICE_2,
            PermissionClass.CONTEXT, false, true, true);
		repository.saveAndFlush(e2);
	}

	@Test
	public void testUnique() {
		Publisher e = new Publisher(org1, reader2, redactor1, "PUB " + INDICE_3,
            PermissionClass.SUBJECT, true, true, true);
		Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			repository.saveAndFlush(e);
		});
	}

	@Test
	public void testUnique2() {
		assertThat(publisher, equalTo(repository.findOne(PublisherPredicates.AllOfOrganization(org1)).orElse(null)));
	}

	@Test
	public void testInsert() {
		Publisher e = new Publisher(org1, reader1, redactor1, "PUB " + INDICE_3,
            PermissionClass.CONTEXT, true, true, true);

		log.info("Before insert : {}", e);
		repository.save(e);
		assertThat(e.getId(), notNullValue());
		log.info("After insert : {}", e);
		Optional<Publisher> optionalPublisher = repository.findById(e.getId());
		Publisher e2 = optionalPublisher == null || !optionalPublisher.isPresent()? null : optionalPublisher.get();
		log.info("After select : {}", e2);
		assertThat(e2, notNullValue());
		assertThat(e2, equalTo(e));

		e = new Publisher(org2, reader2, redactor2, "PUB " + INDICE_4,
            PermissionClass.CONTEXT_WITH_SUBJECTS, true, true, true);
		repository.save(e);
		log.info("After update : {}", e);
		assertThat(e.getId(), notNullValue());
		assertThat(repository.existsById(e.getId()), is(true));
		e = repository.getById(e.getId());
		assertThat(e, notNullValue());
		log.info("After select : {}", e);
		assertThat(repository.count(), equalTo(4L));

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
		assertThat(repository.existsById(e.getId()), is(false));
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
	 * {@link org.springframework.data.repository.CrudRepository#existsById(Object)}
	 * .
	 */
	@Test
	public void testExists() {
		assertThat(repository.existsById(repository.findAll().get(0).getId()), is(true));

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