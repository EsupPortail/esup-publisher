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
import org.esupportail.publisher.domain.PermissionOnSubjectsWithClassificationList;
import org.esupportail.publisher.domain.SubjectPermKey;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.repository.predicates.PermissionPredicates;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author GIP RECIA - Julien Gribonvald 19 août 2014
 */
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Rollback
@Transactional
@Slf4j
public class PermOnSubjectsWCLRepositoryTest {

	@Inject
	private PermOnSubjectsWithClassifsRepository repository;
	@Inject
	private PermissionRepository<AbstractPermission> abstractRepo;
	@Inject
	private OrganizationRepository orgRepo;

	// private QPermissionOnSubjectsWithClassificationList permission =
	// QPermissionOnSubjectsWithClassificationList.PermissionOnSubjectsWithClassificationList;

	final static String PERM_INDICE_1 = "perm1";
	final static String PERM_INDICE_2 = "perm2";
	final static String PERM_INDICE_3 = "perm3";
	final static String PERM_INDICE_4 = "perm4";

	final static SubjectPermKey[] subkeys1 = { ObjTest.subjectPerm1,
			ObjTest.subjectPerm2, ObjTest.subjectPerm3 };
	final static SubjectPermKey[] subkeys2 = { ObjTest.subjectPerm2,
			ObjTest.subjectPerm3 };
	final static SubjectPermKey[] subkeys3 = { ObjTest.subjectPerm1,
			ObjTest.subjectPerm2 };
	final static SubjectPermKey[] subkeys4 = { ObjTest.subjectPerm3 };
	final static SubjectPermKey[] subkeysEmpty = {};

	@BeforeEach
	public void setUp() {
		log.info("starting up {}", this.getClass().getName());

		Organization o = orgRepo.saveAndFlush(ObjTest
				.newOrganization(PERM_INDICE_1));
		PermissionOnSubjectsWithClassificationList e = ObjTest
				.newPermissionOnSubjectsWCL(PERM_INDICE_1,
						o, Sets.newHashSet(subkeys1),
						Sets.newHashSet(o.getContextKey()));
		log.info("Before insert : {}", e);
		repository.saveAndFlush(e);
		e = ObjTest.newPermissionOnSubjectsWCL(PERM_INDICE_2,
				o, Sets.newHashSet(subkeys2),
				Sets.newHashSet(o.getContextKey()));
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
		PermissionOnSubjectsWithClassificationList e = ObjTest
				.newPermissionOnSubjectsWCL(PERM_INDICE_3,
						o, Sets.newHashSet(subkeys3),
						Sets.newHashSet(o.getContextKey()));
		log.info("Before insert : {}", e);
		repository.save(e);
		assertThat(e.getId(), notNullValue());
		log.info("After insert : {}", e);
		Optional<PermissionOnSubjectsWithClassificationList> optionalPerm = repository.findById(e.getId());
		PermissionOnSubjectsWithClassificationList e2 = optionalPerm == null || !optionalPerm.isPresent()? null : optionalPerm.get();
		log.info("After select : {}", e2);
		assertThat(e2, notNullValue());
		assertThat(e.getRolesOnSubjects(), equalTo(Sets.newHashSet(subkeys3)));
		assertThat(e2, equalTo(e));

		assertThat(e2.getRolesOnSubjects(), equalTo(e.getRolesOnSubjects()));
		e2.getRolesOnSubjects().remove(ObjTest.subjectPerm1);
		e2.getRolesOnSubjects().add(ObjTest.subjectPerm1WithValidation);
		repository.save(e2);
		Optional<PermissionOnSubjectsWithClassificationList> optionalPermission = repository.findById(e2.getId());
		e = optionalPermission == null || !optionalPermission.isPresent()? null : optionalPermission.get();
		assertThat(e, notNullValue());
		assertThat(e.getRolesOnSubjects(), not(equalTo(Sets.newHashSet(subkeys3))));

		e = ObjTest.newPermissionOnSubjectsWCL(PERM_INDICE_4,
				o, Sets.newHashSet(subkeys4),
				Sets.newHashSet(o.getContextKey()));
		repository.save(e);
		log.info("After update : {}", e);
		assertThat(e.getId(), notNullValue());
		assertThat(repository.existsById(e.getId()), is(true));
		e = repository.getReferenceById(e.getId());
		assertThat(e, notNullValue());
		log.info("After select : {}", e);
		assertThat(repository.count(), equalTo(4L));

		List<PermissionOnSubjectsWithClassificationList> result = repository
				.findAll();
		assertThat(result.size(), is(4));
		assertThat(result, hasItem(e));
		List<AbstractPermission> result2 = Lists.newArrayList(abstractRepo
				.findAll(PermissionPredicates.OnCtxType(
                    ContextType.ORGANIZATION,
                    PermissionClass.SUBJECT_WITH_CONTEXT, true)));
		assertThat(result2.size(), is(4));
		assertThat(result2, hasItem(e));

        List<PermissionOnSubjectsWithClassificationList> result3 = Lists.newArrayList(repository
            .findAll(PermissionPredicates.OnCtxType(
                ContextType.ORGANIZATION,
                PermissionClass.SUBJECT_WITH_CONTEXT, false)));

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
		List<PermissionOnSubjectsWithClassificationList> result = repository
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
		List<PermissionOnSubjectsWithClassificationList> result = repository
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
		List<PermissionOnSubjectsWithClassificationList> result = repository.findAll();
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