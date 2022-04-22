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

import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.Filter;
import org.esupportail.publisher.domain.Organization;

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
public class FilterRepositoryTest {

	@Inject
	private FilterRepository repository;

	@Inject
	private OrganizationRepository orgRepo;

	final static String FILTER_INDICE_1 = "filter1";
	final static String FILTER_LDAP_PATTERN = "(ESCOUAI=0450822X)";
	final static String FILTER_GROUP_PATTERN = "esco:Etablissements:FICTIF_0450822X";

	@BeforeEach
	public void setUp() throws Exception {
		log.info("starting up {}", this.getClass().getName());

		Organization o = orgRepo.saveAndFlush(ObjTest
				.newOrganization(FILTER_INDICE_1));

		Filter f = ObjTest.newFilterLDAP(FILTER_LDAP_PATTERN, o);
		log.info("Before insert : {}", f);
		repository.saveAndFlush(f);
	}

	@Test
	public void testInsertDuplicate() {
		Organization o = orgRepo.findAll().get(0);
		Filter f = ObjTest.newFilterLDAP(FILTER_LDAP_PATTERN, o);
		log.info("Before insert : {}", f);
		Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			repository.saveAndFlush(f);
		});
	}

	@Test
	public void testInsert() {
		Organization o = orgRepo.findAll().get(0);

		Filter f = ObjTest.newFilterGroup(FILTER_GROUP_PATTERN, o);
		log.info("Before insert :  {}", f);
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
		assertThat(repository.count(), equalTo(1L));
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
		assertThat(repository.count() , equalTo(count - 1));
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