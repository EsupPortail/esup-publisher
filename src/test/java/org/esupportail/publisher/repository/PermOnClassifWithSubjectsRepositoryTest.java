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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.AbstractPermission;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.PermissionOnClassificationWithSubjectList;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.repository.predicates.PermissionPredicates;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author GIP RECIA - Julien Gribonvald 19 août 2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Rollback
@Transactional
@Slf4j
public class PermOnClassifWithSubjectsRepositoryTest {

	@Inject
	private PermOnClassifWithSubjectsRepository repository;

	@Inject
	private PermissionRepository<AbstractPermission> abstractRepo;

	@Inject
	private OrganizationRepository orgRepo;

	// private QPermissionOnClassificationWithSubjectList permission =
	// QPermissionOnClassificationWithSubjectList.PermissionOnClassificationWithSubjectList;

	final static String PERM_INDICE_1 = "perm1";
	final static String PERM_INDICE_2 = "perm2";
	final static String PERM_INDICE_3 = "perm3";
	final static String PERM_INDICE_4 = "perm4";

	final static SubjectKey[] subkeys1 = { ObjTest.subjectKey1,
			ObjTest.subjectKey2, ObjTest.subjectKey3 };
	final static SubjectKey[] subkeys2 = { ObjTest.subjectKey2,
			ObjTest.subjectKey3 };
	final static SubjectKey[] subkeys3 = { ObjTest.subjectKey1,
			ObjTest.subjectKey2 };
	final static SubjectKey[] subkeys4 = { ObjTest.subjectKey3 };
	final static SubjectKey[] subkeysEmpty = {};

	@Before
	public void setUp() {
		log.info("starting up {}", this.getClass().getName());

		Organization o = orgRepo.saveAndFlush(ObjTest
				.newOrganization(PERM_INDICE_1));
		PermissionOnClassificationWithSubjectList e = ObjTest
				.newPermissionOnClassificationWSL(PERM_INDICE_1,
						PermissionType.ADMIN, o, Sets.newHashSet(subkeys1));
		log.info("Before insert : {}", e);
		repository.saveAndFlush(e);
		e = ObjTest.newPermissionOnClassificationWSL(PERM_INDICE_2,
				PermissionType.CONTRIBUTOR, o, Sets.newHashSet(subkeys2));
		log.info("Before insert : {}", e);
		repository.saveAndFlush(e);
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findAll()}.
	 */
	@Test
	public void testInsert() {
		Organization o = orgRepo.findAll().get(0);
		PermissionOnClassificationWithSubjectList e = ObjTest
				.newPermissionOnClassificationWSL(PERM_INDICE_3,
						PermissionType.MANAGER, o, Sets.newHashSet(subkeys3));
		log.info("Before insert : {}", e);
		repository.save(e);
		assertThat(e.getId(), notNullValue());
		log.info("After insert : {}", e);

		Optional<PermissionOnClassificationWithSubjectList> optionalPerm = repository.findById(e.getId());
		PermissionOnClassificationWithSubjectList e2 = optionalPerm == null || !optionalPerm.isPresent()? null : optionalPerm.get();
		log.info("After select : {}", e2);
		assertThat(e2, notNullValue());
		assertThat((Sets.newHashSet(subkeys3)), equalTo(e.getAuthorizedSubjects()));
		assertThat( e2, equalTo(e));

		assertThat((e2.getAuthorizedSubjects()), equalTo(e.getAuthorizedSubjects()));
		e2.getAuthorizedSubjects().remove(subkeys3[1]);
		repository.save(e2);

		Optional<PermissionOnClassificationWithSubjectList> optionalPermission = repository.findById(e2.getId());
		e = optionalPermission == null || !optionalPermission.isPresent()? null : optionalPermission.get();

		assertThat((Sets.newHashSet(subkeys3)), not(equalTo(e.getAuthorizedSubjects())));

		e = ObjTest.newPermissionOnClassificationWSL(PERM_INDICE_4,
				PermissionType.LOOKOVER, o, Sets.newHashSet(subkeys4));
		repository.save(e);
		log.info("After update : {}", e);
		assertThat(e.getId(), notNullValue());
		assertThat(repository.existsById(e.getId()), is(true));
		e = repository.getById(e.getId());
		assertThat(e, notNullValue());
		log.info("After select : {}", e);
		assertThat(repository.count(), equalTo(4L));

		List<PermissionOnClassificationWithSubjectList> result = repository
				.findAll();
		assertThat(result.size(), is(4));
		assertThat(result, hasItem(e));
		List<AbstractPermission> result2 = Lists.newArrayList(abstractRepo
				.findAll(PermissionPredicates.OnCtxType(
                    ContextType.ORGANIZATION,
                    PermissionClass.CONTEXT_WITH_SUBJECTS, true)));
		assertThat(result2.size(), is(4));
		assertThat(result2, hasItem(e));

        List<PermissionOnClassificationWithSubjectList> result3 = Lists.newArrayList(repository
				.findAll(PermissionPredicates.OnCtxType(
                    ContextType.ORGANIZATION,
                    PermissionClass.CONTEXT_WITH_SUBJECTS, false)));

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
		List<PermissionOnClassificationWithSubjectList> result = repository
				.findAll();
		assertThat(result.size(), is(2));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#existsById(Object)}
	 * .
	 */
	@Test
	public void testExists() {
		List<PermissionOnClassificationWithSubjectList> result = repository
				.findAll();
		assertThat(repository.existsById(result.get(0).getId()), is(true));

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
		List<PermissionOnClassificationWithSubjectList> result = repository
				.findAll();
		repository.delete(result.get(0));
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