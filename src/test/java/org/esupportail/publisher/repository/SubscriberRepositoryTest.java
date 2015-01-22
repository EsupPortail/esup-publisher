package org.esupportail.publisher.repository;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.SubscribeType;
import org.esupportail.publisher.repository.predicates.SubscriberPredicates;
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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
@Slf4j
public class SubscriberRepositoryTest {

	@Inject
	private SubscriberRepository repository;

	@Inject
	private OrganizationRepository orgRepo;

	@Inject
	private RedactorRepository redactorRepo;

	@Inject
	private ItemRepository<News> itemRepo;

	// @Inject
	// private SubscriberDTOFactory factoryDTO;

	final static String INDICE_1 = "ind1";
	final static String INDICE_2 = "ind2";
	final static String INDICE_3 = "ind3";
	final static String INDICE_4 = "ind4";

	private Organization org1;
	private Organization org2;
	private Redactor redactor1;
	private Redactor redactor2;
	private News news1;
	private News news2;

	@Before
	public void setUp() throws Exception {
		log.info("starting up " + this.getClass().getName());

		org1 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_1));
		org2 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_2));

		redactor1 = redactorRepo.saveAndFlush(ObjTest.newRedactor(INDICE_1));
		redactor2 = redactorRepo.saveAndFlush(ObjTest.newRedactor(INDICE_2));

		news1 = itemRepo.saveAndFlush(ObjTest
				.newNews(INDICE_1, org1, redactor1));
		news2 = itemRepo.saveAndFlush(ObjTest
				.newNews(INDICE_2, org2, redactor2));

		Subscriber s1 = ObjTest.newSubscriber(org1.getContextKey());
		s1 = repository.saveAndFlush(s1);

		Subscriber s2 = ObjTest.newSubscriber(news1.getContextKey());
		s2 = repository.saveAndFlush(s2);
	}

	// @Test
	// public void testDTO() throws ObjectNotFoundException {
	// List<Subscriber> subsList = repository.findAll();
	// log.debug("models before : {}", subsList);
	// List<SubscriberDTO> dtos = factoryDTO.asDTOList(subsList);
	// log.debug("To dto before : {}", dtos);
	// Subscriber m = subsList.get(0);
	// log.debug("model before all : {}", m);
	// SubscriberDTO c = dtos.get(0);
	// SubscriberDTO c_old = factoryDTO.from(m);
	// if (!SubscribeType.SUB_PRE.equals(c.getSubscribeType())) {
	// c.setSubscribeType(SubscribeType.SUB_PRE);
	// } else {
	// c.setSubscribeType(SubscribeType.SUB_FORCED);
	// }
	// assertThat(c_old, not(samePropertyValuesAs(c)));
	// log.debug("DTO after modif : {}", c);
	// m = factoryDTO.from(c);
	// log.debug("model before saved modif : {}", m);
	// m = repository.save(m);
	// log.debug("model after saved modif : {}", m);
	// SubscriberDTO c2 = factoryDTO.from(m);
	// log.debug("DTO after saved model modifs : {}", c2);
	// assertThat(c2, samePropertyValuesAs(c));
	// assertThat(c_old, not(samePropertyValuesAs(c2)));
	// }

	@Test
	public void testInsert() {
		Subscriber s = ObjTest.newSubscriber(org2.getContextKey());
		log.info("Before insert : " + s.toString());
		s = repository.saveAndFlush(s);
		log.info("After insert : " + s.toString());

		Subscriber s2 = repository.findOne(s.getSubjectCtxId());
		log.info("After select : " + s2.toString());
		assertNotNull(s2);
		assertEquals(s, s2);

		s2.setSubscribeType(SubscribeType.FREE);
		s2 = repository.saveAndFlush(s2);
		log.info("After update : " + s2.toString());

		Subscriber s4 = ObjTest.newSubscriber(news2.getContextKey());
		log.info("Before insert : " + s4.toString());
		repository.save(s4);
		log.info("After insert : " + s4.toString());
		assertNotNull(s4.getSubjectCtxId());
		assertTrue(repository.exists(s4.getSubjectCtxId()));
		s = repository.getOne(s.getSubjectCtxId());
		assertNotNull(s);
		log.info("After select : " + s.toString());
		assertTrue(repository.count() == 4);

		List<Subscriber> results = repository.findAll();
		assertThat(results.size(), is(4));
		assertThat(results, hasItem(s));

		results = Lists.newArrayList(repository.findAll(SubscriberPredicates
				.onCtx(org1.getContextKey())));
		assertThat(results.size(), is(1));

		repository.delete(s.getSubjectCtxId());
		assertTrue(repository.findAll().size() == 3);
		assertFalse(repository.exists(s.getSubjectCtxId()));

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
		Subscriber s1 = ObjTest.newSubscriber(org2.getContextKey());
		Subscriber s2 = ObjTest.newSubscriber(news2.getContextKey());
		repository.save(Arrays.asList(s1, s2));
		assertThat(repository.findAll().size(), is(4));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)}
	 * .
	 */
	@Test
	public void testExists() {
		assertTrue(repository.exists(repository.findAll().get(0)
				.getSubjectCtxId()));

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
