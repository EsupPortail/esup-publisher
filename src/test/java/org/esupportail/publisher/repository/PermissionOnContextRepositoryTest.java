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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.AbstractPermission;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.PermissionOnContext;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.esupportail.publisher.repository.predicates.PermissionPredicates;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author GIP RECIA - Julien Gribonvald 7 juil. 2014
 */
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Rollback
@Transactional
@Slf4j
public class PermissionOnContextRepositoryTest {

	@Inject
	private PermissionOnContextRepository repository;
	@Inject
	private PermissionRepository<AbstractPermission> abstractRepo;
    @Inject
    private EvaluatorRepository<AbstractEvaluator> evaluatorRepository;
	@Inject
	private OrganizationRepository orgRepo;

	// private QPermissionOnContext permission =
	// QPermissionOnContext.permissionOnContext;

	final static String PERM_INDICE_1 = "perm1";
	final static String PERM_INDICE_2 = "perm2";
	final static String PERM_INDICE_3 = "perm3";
	final static String PERM_INDICE_4 = "perm4";

	@BeforeEach
	public void setUp() {
		log.info("starting up {}", this.getClass().getName());

		Organization o = orgRepo.saveAndFlush(ObjTest
				.newOrganization(PERM_INDICE_1));
		PermissionOnContext e = ObjTest.newPermissionOnCtx(PERM_INDICE_1,
				PermissionType.ADMIN, o);
		log.info("Before insert : {}", e);
		repository.saveAndFlush(e);
		PermissionOnContext e2 = ObjTest.newPermissionOnCtx(PERM_INDICE_2,
				PermissionType.CONTRIBUTOR, o);
		log.info("Before insert : {}", e2);
		repository.saveAndFlush(e2);

		log.info(e.getEvaluator().toString());
		log.info(e2.getEvaluator().toString());
		// int i=0;
		// for (AbstractEvaluator ev : ((OperatorEvaluator)
		// e.getEvaluator()).getEvaluators()) {
		// log.info("==============iter : {}", i);
		// log.info(ev.toString());
		// AbstractEvaluator ev2 = ((OperatorEvaluator)
		// e2.getEvaluator()).getEvaluators().iterator().next();
		// log.info(ev2.toString());
		// log.info("test = {}", ev.equals(ev2));
		// i++;
		// }
		// // ((OperatorEvaluator) e.getEvaluator()).setAbstractEvaluators(new
		// ArrayList<AbstractEvaluator>());
		// // ((OperatorEvaluator) e2.getEvaluator()).setAbstractEvaluators(new
		// ArrayList<AbstractEvaluator>());
		// log.info("Operator : {}", ((OperatorEvaluator)
		// e.getEvaluator()).getType());
		// log.info("Operator 2 : {}", ((OperatorEvaluator)
		// e2.getEvaluator()).getType());
		// log.info("last test = {}",
		// e.getEvaluator().equals(e2.getEvaluator()));
		assertThat(e.getEvaluator(), equalTo(e2.getEvaluator()));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findAll()}.
	 */
	@Test
	public void testInsert() {
		Organization o = orgRepo.findAll().get(0);
		PermissionOnContext e = ObjTest.newPermissionOnCtx(PERM_INDICE_3,
				PermissionType.MANAGER, o);
		log.info("Before insert : {}", e);
		repository.save(e);
		assertThat(e.getId(), notNullValue());
        assertThat(e.getEvaluator(), notNullValue());
		log.info("After insert : {}", e);

		Optional<PermissionOnContext> optionalPermissionOnContext = repository.findById(e.getId());
		PermissionOnContext e2 = optionalPermissionOnContext == null || !optionalPermissionOnContext.isPresent()? null : optionalPermissionOnContext.get();
		log.info("After select : {}", e2);
		assertThat(e2, notNullValue());
		assertThat(e, equalTo( e2));
        assertThat(e2.getEvaluator(), notNullValue());
        assertThat(e.getEvaluator(), equalTo( e2.getEvaluator()));

		e = ObjTest.newPermissionOnCtx(PERM_INDICE_4, PermissionType.LOOKOVER,
				o);
		repository.save(e);
		log.info("After update : {}", e);
		assertThat(e.getId(), notNullValue());
		assertThat(repository.existsById(e.getId()), is(true));
		e = repository.getById(e.getId());
		assertThat(e, notNullValue());
		log.info("After select : {}", e);
		assertThat(repository.count(), equalTo(4L));

		List<PermissionOnContext> result = repository.findAll();
		assertThat(result.size(), is(4));
		assertThat(result, hasItem(e));
		List<AbstractPermission> result2 = Lists.newArrayList(abstractRepo
				.findAll(PermissionPredicates.OnCtxType(
                    ContextType.ORGANIZATION, PermissionClass.CONTEXT, true)));

		assertThat(result2.size(), is(4));
		assertThat(result2, hasItem(e));

        List<PermissionOnContext> result3 = Lists.newArrayList(repository
            .findAll(PermissionPredicates.OnCtxType(
                ContextType.ORGANIZATION, PermissionClass.CONTEXT, false)));
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
        assertThat(evaluatorRepository.count(), equalTo(0L));
	}

}