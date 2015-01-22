package org.esupportail.publisher.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.AbstractPermission;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.PermissionOnClassificationWithSubjectList;
import org.esupportail.publisher.domain.SubjectKey;
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
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author GIP RECIA - Julien Gribonvald 19 août 2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
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
		log.info("starting up " + this.getClass().getName());

		Organization o = orgRepo.saveAndFlush(ObjTest
				.newOrganization(PERM_INDICE_1));
		PermissionOnClassificationWithSubjectList e = ObjTest
				.newPermissionOnClassificationWSL(PERM_INDICE_1,
						PermissionType.ADMIN, o, Sets.newHashSet(subkeys1));
		log.info("Before insert : " + e.toString());
		repository.saveAndFlush(e);
		e = ObjTest.newPermissionOnClassificationWSL(PERM_INDICE_2,
				PermissionType.CONTRIBUTOR, o, Sets.newHashSet(subkeys2));
		log.info("Before insert : " + e.toString());
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
		log.info("Before insert : " + e.toString());
		repository.save(e);
		assertNotNull(e.getId());
		log.info("After insert : " + e.toString());

		PermissionOnClassificationWithSubjectList e2 = repository.findOne(e
				.getId());
		log.info("After select : " + e2.toString());
		assertNotNull(e2);
		assertTrue(e.getAuthorizedSubjects().equals(Sets.newHashSet(subkeys3)));
		assertEquals(e, e2);

		assertTrue(e.getAuthorizedSubjects().equals(e2.getAuthorizedSubjects()));
		e2.getAuthorizedSubjects().remove(subkeys3[1]);
		repository.save(e2);
		e = repository.findOne(e2.getId());

		assertFalse(e.getAuthorizedSubjects().equals(Sets.newHashSet(subkeys3)));

		e = ObjTest.newPermissionOnClassificationWSL(PERM_INDICE_4,
				PermissionType.LOOKOVER, o, Sets.newHashSet(subkeys4));
		repository.save(e);
		log.info("After update : " + e.toString());
		assertNotNull(e.getId());
		assertTrue(repository.exists(e.getId()));
		e = repository.getOne(e.getId());
		assertNotNull(e);
		log.info("After select : " + e.toString());
		assertTrue(repository.count() == 4);

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

		repository.delete(e.getId());
		log.debug("nb returned : {}", repository.findAll().size());
		assertThat(repository.findAll().size(), is(3));
		assertFalse(repository.exists(e.getId()));
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
	 * {@link org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)}
	 * .
	 */
	@Test
	public void testExists() {
		List<PermissionOnClassificationWithSubjectList> result = repository
				.findAll();
		assertTrue(repository.exists(result.get(0).getId()));

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
		List<PermissionOnClassificationWithSubjectList> result = repository
				.findAll();
		repository.delete(result.get(0));
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
