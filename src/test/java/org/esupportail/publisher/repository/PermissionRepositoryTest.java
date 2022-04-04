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
import javax.inject.Named;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.AbstractPermission;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.PermissionOnClassificationWithSubjectList;
import org.esupportail.publisher.domain.PermissionOnContext;
import org.esupportail.publisher.domain.PermissionOnSubjects;
import org.esupportail.publisher.domain.PermissionOnSubjectsWithClassificationList;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.esupportail.publisher.repository.predicates.PermissionPredicates;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)//@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Rollback
// @Transactional
@Slf4j
public class PermissionRepositoryTest {

	@Inject
	@Named("permissionRepository")
	private PermissionRepository<AbstractPermission> repository;

    @Inject
    private EvaluatorRepository<AbstractEvaluator> evaluatorRepository;

	@Inject
	private OrganizationRepository orgRepo;

	// @Inject
	// private UserRepository userRepo;

	final static String INDICE_1 = "ind1";
	final static String INDICE_2 = "ind2";
	final static String INDICE_3 = "ind3";
	final static String INDICE_4 = "ind4";

	private Organization org1;
	private Organization org2;

	@BeforeAll
	public void setUp() throws Exception {
		log.info("starting up {}", this.getClass().getName());

		// userRepo.save(new User(ObjTest.subject1, "test"));
		// userRepo.save(new User(ObjTest.subject2, "test"));
		// userRepo.save(new User(ObjTest.subject3, "test"));

		org1 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_1));
		org2 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_2));

		PermissionOnContext p1 = new PermissionOnContext(org1.getContextKey(),
				PermissionType.ADMIN, ObjTest.newGlobalEvaluator());
		repository.saveAndFlush(p1);
		PermissionOnSubjects p2 = new PermissionOnSubjects(
				org1.getContextKey(),
				ObjTest.newGlobalEvaluator(), Sets.newHashSet(ObjTest.subkeys2));
		repository.saveAndFlush(p2);

		PermissionOnClassificationWithSubjectList p3 = new PermissionOnClassificationWithSubjectList(
				org1.getContextKey(), PermissionType.ADMIN,
				ObjTest.newGlobalEvaluator(),
				Sets.newHashSet(ObjTest.subjectKey2));
		repository.saveAndFlush(p3);

		PermissionOnSubjectsWithClassificationList p4 = new PermissionOnSubjectsWithClassificationList(
				org1.getContextKey(),
				ObjTest.newGlobalEvaluator(),
				Sets.newHashSet(ObjTest.subkeys3), Sets.newHashSet(org2
						.getContextKey()));
		repository.saveAndFlush(p4);
	}

	// @Test
	// public void testDTO() throws ObjectNotFoundException {
	// List<AbstractPermission> list = repository.findAll();
	// List<Permission> permList = Lists.newArrayList();
	// permList.addAll(list);
	// log.debug("models before : {}", permList);
	// List<PermissionDTO> dtos = factoryDTO.asDTOList(permList);
	// log.debug("To dto before : {}", dtos);
	// Permission m = permList.get(0);
	// log.debug("model before all : {}", m);
	// PermissionDTO c = dtos.get(0);
	// PermissionDTO c_old = factoryDTO.from(m);
	// c.setCreatedBy(new SubjectDTO(new SubjectKeyDTO(ObjTest.subject3
	// .getKeyId(), ObjTest.subject3.getKeyType()), "test"));
	// assertThat(c_old, not(samePropertyValuesAs(c)));
	// log.debug("DTO after modif : {}", c);
	// m = factoryDTO.from(c);
	// log.debug("model before saved modif : {}", m);
	// m = repository.save((AbstractPermission) m);
	// log.debug("model after saved modif : {}", m);
	// PermissionDTO c2 = factoryDTO.from(m);
	// log.debug("DTO after saved model modifs : {}", c2);
	// assertThat(c2, samePropertyValuesAs(c));
	// assertThat(c_old, not(samePropertyValuesAs(c2)));
	// }

	@Test
	@Transactional
	public void testInsert() {
		PermissionOnContext p1 = new PermissionOnContext(org1.getContextKey(),
				PermissionType.ADMIN,
				ObjTest.newMVUEvaluatorForGroup("esco:admin:central"));
		log.info("Before insert : {}", p1);
		p1 = repository.save(p1);
		assertThat(p1.getId(), notNullValue());
		log.info("After insert : {}", p1);

		Optional<AbstractPermission> optionalPerm = repository.findById(p1.getId());
		PermissionOnContext p2 = optionalPerm == null || !optionalPerm.isPresent()? null : (PermissionOnContext) optionalPerm.get();
		log.info("After select : {}", p2);
		assertThat(p2, notNullValue());
		assertThat(p2, equalTo(p1));

		p2.setRole(PermissionType.LOOKOVER);
		p2.setContext(org2.getContextKey());
		p2 = repository.save(p2);
		log.info("After update : {}", p2);
		assertThat(repository.existsById(p2.getId()), is(true));
		p2 = (PermissionOnContext) repository.getById(p2.getId());
		assertThat(p2, notNullValue());
		log.info("After select : {}", p2);
		assertThat(repository.count(), equalTo(5L));

		List<AbstractPermission> result = repository.findAll();
		assertThat(result.size(), is(5));
		assertThat(result, hasItem(p2));
		result = Lists.newArrayList(repository
				.findAll(PermissionPredicates.OnCtxType(
                    ContextType.ORGANIZATION, PermissionClass.CONTEXT, true)));
		assertThat(result.size(), is(2));
		assertThat(result, hasItem(p2));

		repository.deleteById(p2.getId());
		log.debug("nb returned : {}", repository.findAll().size());
		assertThat(repository.findAll().size(), is(4));
		assertThat(repository.existsById(p2.getId()), is(false));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findAll()}.
	 */
	@Test
	@Transactional
	public void testFindAll() {
		assertThat(repository.findAll().size(), is(4));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#existsById(Object)}
	 * .
	 */
	@Test
	@Transactional
	public void testExists() {
		assertThat(repository.existsById(repository.findAll().get(0).getId()), is(true));

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#count()}.
	 */
	@Test
	@Transactional
	public void testCount() {
		assertThat(repository.count(), equalTo(4L));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#delete(java.lang.Object)}
	 * .
	 */
	@Test
	@Transactional
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
	@Transactional
	public void testDeleteAll() {
		repository.deleteAll();
		assertThat(repository.count(), equalTo(0L));
        assertThat(evaluatorRepository.count(), equalTo(0L));
	}

}