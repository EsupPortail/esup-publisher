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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.SubscribeType;
import org.esupportail.publisher.repository.predicates.SubscriberPredicates;

import com.google.common.collect.Lists;
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

	@BeforeAll
	public void setUp() throws Exception {
		log.info("starting up {}", this.getClass().getName());

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

        Subscriber s3 = ObjTest.newSubscriberPersonFromAttr(org1.getContextKey());
        s3 = repository.saveAndFlush(s3);
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
		log.info("Before insert : {}", s);
		s = repository.saveAndFlush(s);
		log.info("After insert : {}", s);

		Optional<Subscriber> optionalSubscriber = repository.findById(s.getSubjectCtxId());
		Subscriber s2 = optionalSubscriber == null || !optionalSubscriber.isPresent()? null : optionalSubscriber.get();
		log.info("After select : {}", s2);
		assertThat(s2, notNullValue());
		assertThat( s2, equalTo(s));

		s2.setSubscribeType(SubscribeType.FREE);
		s2 = repository.saveAndFlush(s2);
		log.info("After update : {}", s2);

		Subscriber s4 = ObjTest.newSubscriber(news2.getContextKey());
		log.info("Before insert : {}", s4);
		repository.save(s4);
		log.info("After insert : {}", s4);
		assertThat(s4.getSubjectCtxId(), notNullValue());
		assertThat(repository.existsById(s4.getSubjectCtxId()), is(true));
		s = repository.getById(s.getSubjectCtxId());
		assertThat(s, notNullValue());
		log.info("After select : {}", s);
		assertThat(repository.count(), equalTo(5L));

		List<Subscriber> results = repository.findAll();
        log.info("List of all subscribers {}", results);
		assertThat(results.size(), is(5));
		assertThat(results, hasItem(s));

		results = Lists.newArrayList(repository.findAll(SubscriberPredicates
				.onCtx(org1.getContextKey())));
		assertThat(results.size(), is(2));

		repository.deleteById(s.getSubjectCtxId());
		assertThat(repository.findAll().size(), equalTo(4));
		assertThat(repository.existsById(s.getSubjectCtxId()), is(false));

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findAll()}.
	 */
	@Test
	public void testFindAll() {
		assertThat(repository.findAll().size(), is(3));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#saveAll(java.lang.Iterable)}
	 * .
	 */
	@Test
	public void testSaveIterableOfS() {
		Subscriber s1 = ObjTest.newSubscriber(org2.getContextKey());
		Subscriber s2 = ObjTest.newSubscriber(news2.getContextKey());
		repository.saveAll(Arrays.asList(s1, s2));
		assertThat(repository.findAll().size(), is(5));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#existsById(Object)}
	 * .
	 */
	@Test
	public void testExists() {
		assertThat(repository.existsById(repository.findAll().get(0).getSubjectCtxId()), is(true));

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#count()}.
	 */
	@Test
	public void testCount() {
		assertThat(repository.count(), equalTo(3L));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#delete(java.lang.Object)}
	 * .
	 */
	@Test
	public void testDelete() {
		repository.delete(repository.findAll().get(0));
		assertThat(repository.count(), equalTo(2L));
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