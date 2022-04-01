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
/**
 *
 */
package org.esupportail.publisher.repository;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.QOrganization;
import org.esupportail.publisher.repository.predicates.OrganizationPredicates;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

/**
 * @author GIP RECIA - Julien Gribonvald 7 juil. 2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Rollback
@Transactional
@Slf4j
public class OrganizationRepositoryTest {

	@Inject
	private OrganizationRepository repository;

	private QOrganization organization = QOrganization.organization;

    final static Set<String> DEFAULT_IDS1 = Sets.newHashSet("ID1");
    final static Set<String> DEFAULT_IDS2 = Sets.newHashSet("ID2", "IDX");
    final static Set<String> DEFAULT_IDS3 = Sets.newHashSet("ID3");
    final static Set<String> DEFAULT_IDS4 = Sets.newHashSet("ID4");

	@Before
	public void setUp() {
		log.info("starting up {}", this.getClass().getName());
		Organization e = ObjTest.newOrganization("init1");
        e.setIdentifiers(DEFAULT_IDS1);
		repository.save(e);
		Organization e2 = ObjTest.newOrganization("init2");
        e2.setIdentifiers(DEFAULT_IDS2);
		repository.save(e2);
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findAll()}.
	 */
	@Test
	public void testInsert() {
		Organization e = ObjTest.newOrganization("");
        e.setIdentifiers(DEFAULT_IDS3);
		log.info("Before insert : {}", e);
		repository.save(e);
		assertThat(e.getId(), notNullValue());
		log.info("After insert : {}", e);
        assertThat(e.getIdentifiers().size(), is(DEFAULT_IDS3.size()));

        Optional<Organization> optionalOrganization = repository.findById(e.getId());
        Organization e2 = optionalOrganization == null || !optionalOrganization.isPresent()? null : optionalOrganization.get();
        //e2.setIdentifiers(DEFAULT_IDS3);
		log.info("After select : {}", e2);
		assertThat(e2, notNullValue());
		assertThat(e, equalTo( e2));

		e = ObjTest.newOrganization("1");
        e.setIdentifiers(DEFAULT_IDS4);
		repository.save(e);
		log.info("After insert : {}", e);
		assertThat(e.getId(), notNullValue());
		assertThat(repository.existsById(e.getId()), is(true));
		e = repository.getById(e.getId());
		assertThat(e, notNullValue());
		log.info("After select : {}", e);
		assertThat(repository.count(), equalTo(4L));

		List<Organization> result = repository.findAll();
		assertThat(result.size(), is(4));
		assertThat(result, hasItem(e));

		e2 = repository.findByName(e.getName());
		List<Organization> results = Lists.newArrayList(repository
				.findAll(organization.name.eq(e.getName())));
		assertThat(results, hasItem(e2));

		// BooleanExpression orgExistId = organization.id.eq(e.getId());
		results = Lists.newArrayList(repository.findAll(OrganizationPredicates
				.sameName(e)));
		assertThat(results, is(empty()));

		repository.deleteById(e.getId());
		assertThat(repository.findAll().size(), equalTo(3));
		assertThat(repository.existsById(e.getId()), is(false));

		Optional<Organization> optionalO = repository.findById((long) 0);
        e2 = optionalO == null || !optionalO.isPresent()? null : optionalO.get();
		assertThat(e2, is(nullValue()));
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
	 * {@link org.springframework.data.jpa.repository.JpaRepository#saveAll(java.lang.Iterable)}
	 * .
	 */
	@Test
	public void testSaveIterableOfS() {
		Organization e = ObjTest.newOrganization("init3");
		Organization e2 = ObjTest.newOrganization("init4");
		repository.saveAll(Arrays.asList(e, e2));
		assertThat(repository.findAll().size(), is(4));
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
		repository.delete(repository.findAll().get(0));
		assertThat(repository.count(), equalTo(1L));
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