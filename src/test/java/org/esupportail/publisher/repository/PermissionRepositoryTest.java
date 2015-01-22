package org.esupportail.publisher.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.repository.predicates.PermissionPredicates;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
// @Transactional
@Slf4j
public class PermissionRepositoryTest {

	@Inject
	@Named("permissionRepository")
	private PermissionRepository<AbstractPermission> repository;

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

	@Before
	public void setUp() throws Exception {
		log.info("starting up " + this.getClass().getName());

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
		log.info("Before insert : " + p1.toString());
		p1 = repository.save(p1);
		assertNotNull(p1.getId());
		log.info("After insert : " + p1.toString());

		PermissionOnContext p2 = (PermissionOnContext) repository.findOne(p1
				.getId());
		log.info("After select : " + p2.toString());
		assertNotNull(p2);
		assertEquals(p1, p2);

		p2.setRole(PermissionType.LOOKOVER);
		p2.setContext(org2.getContextKey());
		p2 = repository.save(p2);
		log.info("After update : " + p2.toString());
		assertTrue(repository.exists(p2.getId()));
		p2 = (PermissionOnContext) repository.getOne(p2.getId());
		assertNotNull(p2);
		log.info("After select : " + p2.toString());
		assertTrue(repository.count() == 5);

		List<AbstractPermission> result = repository.findAll();
		assertThat(result.size(), is(5));
		assertThat(result, hasItem(p2));
		result = Lists.newArrayList(repository
				.findAll(PermissionPredicates.OnCtxType(
                    ContextType.ORGANIZATION, PermissionClass.CONTEXT, true)));
		assertThat(result.size(), is(2));
		assertThat(result, hasItem(p2));

		repository.delete(p2.getId());
		log.debug("nb returned : {}", repository.findAll().size());
		assertThat(repository.findAll().size(), is(4));
		assertFalse(repository.exists(p2.getId()));
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
	 * {@link org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)}
	 * .
	 */
	@Test
	@Transactional
	public void testExists() {
		assertTrue(repository.exists(repository.findAll().get(0).getId()));

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#count()}.
	 */
	@Test
	@Transactional
	public void testCount() {
		assertTrue(repository.count() == 4);
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
		assertTrue(repository.count() == count - 1);
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#deleteAll()}.
	 */
	@Test
	@Transactional
	public void testDeleteAll() {
		repository.deleteAll();
		assertTrue(repository.count() == 0);
	}

}
