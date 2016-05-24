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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.QOrganization;
import org.esupportail.publisher.repository.predicates.OrganizationPredicates;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author GIP RECIA - Julien Gribonvald 7 juil. 2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
@Slf4j
public class OrganizationRepositoryTest {

	@Inject
	private OrganizationRepository repository;

	private QOrganization organization = QOrganization.organization;

    final static Set<String> DEFAULT_IDS = Sets.newHashSet("0450822x");

	@Before
	public void setUp() {
		log.info("starting up " + this.getClass().getName());
		Organization e = ObjTest.newOrganization("init1");
        e.setIdentifiers(DEFAULT_IDS);
		repository.save(e);
		Organization e2 = ObjTest.newOrganization("init2");
        e2.setIdentifiers(DEFAULT_IDS);
		repository.save(e2);
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findAll()}.
	 */
	@Test
	public void testInsert() {
		Organization e = ObjTest.newOrganization("");
        e.setIdentifiers(DEFAULT_IDS);
		log.info("Before insert : " + e.toString());
		repository.save(e);
		assertNotNull(e.getId());
		log.info("After insert : " + e.toString());
        assertThat(e.getIdentifiers().size(), is(DEFAULT_IDS.size()));

		Organization e2 = repository.findOne(e.getId());
        e2.setIdentifiers(DEFAULT_IDS);
		log.info("After select : " + e2.toString());
		assertNotNull(e2);
		assertEquals(e, e2);

		e = ObjTest.newOrganization("1");
        e.setIdentifiers(DEFAULT_IDS);
		repository.save(e);
		log.info("After insert : " + e.toString());
		assertNotNull(e.getId());
		assertTrue(repository.exists(e.getId()));
		e = repository.getOne(e.getId());
		assertNotNull(e);
		log.info("After select : " + e.toString());
		assertTrue(repository.count() == 4);

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
		assertTrue(results.isEmpty());

		/*
		 * assertTrue(repository.canDelete(e.getId()));
		 * assertTrue(repository.selectAll().size() > 0 );
		 */
		/*
		 * Category cat = newCategory(true); cat.setOrganizationId(e.getId());
		 * categoryDao.updateById(cat);
		 * assertFalse(repository.canDelete(e.getId()));
		 * assertTrue(repository.insertLinkToOneType(e.getId(), new Long(1)) ==
		 * 1); List<Long> typeList = new ArrayList<Long>(); typeList.add(new
		 * Long(4)); typeList.add(new Long(5));
		 * assertTrue(repository.insertLinkToListOfType(e.getId(), typeList) ==
		 * 2); List<Type> listType = repository.selectLinkedType(e.getId());
		 * log.info("Liste de Type : " + listType.toString());
		 * assertTrue(listType.size() == 3); List<Organization> listOrganization
		 * = repository.selectAllOfType(new Long(5));
		 * log.info("Liste d'Organization : " + listOrganization.toString());
		 * assertTrue(listOrganization.size() == 1);
		 * assertTrue(repository.deleteLinkToOneType(e.getId(), new Long(5)) ==
		 * 1); assertTrue(repository.deleteAllLinkToType(e.getId()) == 2);
		 * categoryDao.deleteById(cat.getCatId());
		 * assertTrue(repository.selectLinkedType(e.getId()).size() == 0);
		 */

		repository.delete(e.getId());
		assertTrue(repository.findAll().size() == 3);
		assertFalse(repository.exists(e.getId()));

		e = repository.findOne((long) 0);
		assertNull(e);
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
		Organization e = ObjTest.newOrganization("init3");
		Organization e2 = ObjTest.newOrganization("init4");
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
