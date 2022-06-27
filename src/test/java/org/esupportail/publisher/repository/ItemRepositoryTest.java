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
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Flash;
import org.esupportail.publisher.domain.Media;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.Resource;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.ItemDTOSelectorFactory;
import org.esupportail.publisher.web.rest.dto.ItemDTO;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;
import org.esupportail.publisher.web.rest.dto.SubjectKeyDTO;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Rollback
@Transactional
@Slf4j
public class ItemRepositoryTest {

	@Inject
	private ItemRepository<AbstractItem> repository;
    @Inject
    private ItemDTOSelectorFactory factoryDTO;
	@Inject
	private OrganizationRepository orgRepo;
	@Inject
	private RedactorRepository redactorRepo;
	@Inject
	private UserRepository userRepo;

	final static String INDICE_1 = "ind1";
	final static String INDICE_2 = "ind2";
	final static String INDICE_3 = "ind3";
	final static String INDICE_4 = "ind4";
	final static String INDICE_A = "indA";
    final static ItemStatus DEFAULT_STATUS = ItemStatus.PUBLISHED;
    final static ItemStatus UPDATED_STATUS = ItemStatus.ARCHIVED;

    final static int nbDaysMaxDuration = 90;

	private Organization org1;
	private Organization org2;
	private Redactor redactor1;
	private Redactor redactor2;
	private Redactor redactorOptionalEndDate;

    private User user1;private User user2;private User user3;

	@BeforeEach
	public void setUp() throws Exception {
		log.info("starting up {}", this.getClass().getName());

        user1 = userRepo.findById(ObjTest.subject1).orElse(null);
        user2 = userRepo.findById(ObjTest.subject2).orElse(null);
        user3 = userRepo.findById(ObjTest.subject3).orElse(null);

		org1 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_1));
		org2 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_2));

		redactor1 = redactorRepo.saveAndFlush(ObjTest.newRedactor(INDICE_1));
		redactor2 = redactorRepo.saveAndFlush(ObjTest.newRedactor(INDICE_2));
		redactorOptionalEndDate = ObjTest.newRedactor(INDICE_3);
        redactorOptionalEndDate.setOptionalPublishTime(true);
        redactorOptionalEndDate.setNbDaysMaxDuration(nbDaysMaxDuration);
        redactorOptionalEndDate = redactorRepo.saveAndFlush(redactorOptionalEndDate);

		Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
            ObjTest.instantToLocalDate(ObjTest.d1), ObjTest.instantToLocalDate(ObjTest.d3), ObjTest.d2,
				user2, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
				org1, redactor1);
		repository.saveAndFlush(m1);
		News m2 = new News("Titre " + INDICE_2, "enclosure" + INDICE_2, "body"
				+ INDICE_2, ObjTest.instantToLocalDate(ObjTest.d1), ObjTest.instantToLocalDate(ObjTest.d3),
				ObjTest.d2, user2, DEFAULT_STATUS, "summary" + INDICE_2,  true,
            true, org2, redactor2);
		repository.saveAndFlush(m2);
		Resource m3 = new Resource("Titre " + INDICE_3, "enclosure" + INDICE_3,
				"ressourceUrl" + INDICE_3, ObjTest.instantToLocalDate(ObjTest.d1),
				ObjTest.instantToLocalDate(ObjTest.d3), ObjTest.d2, user2,
				DEFAULT_STATUS, "summary" + INDICE_3,  true, true, org1, redactor2);
		repository.saveAndFlush(m3);
        Flash m4 = new Flash("Titre " + INDICE_4, "enclosure" + INDICE_4, "body"
            + INDICE_4, ObjTest.instantToLocalDate(ObjTest.d1), ObjTest.instantToLocalDate(ObjTest.d3),
            ObjTest.d2, user2, DEFAULT_STATUS, "summary" + INDICE_4, true,
            true, org2, redactor2);
        repository.saveAndFlush(m4);
	}

    @Test
    public void testDTO() throws ObjectNotFoundException {
        List<AbstractItem> itemList = repository.findAll();
        log.debug("models before : {}", itemList);
        List<ItemDTO> dtos = factoryDTO.asDTOList(itemList);
        log.debug("To dto before : {}", dtos);
        AbstractItem m = itemList.get(0);
        log.debug("model before all : {}", m);
        ItemDTO c = dtos.get(0);
        ItemDTO c_old = factoryDTO.from(m);
        c.setValidatedBy(new SubjectDTO(new SubjectKeyDTO(ObjTest.subjectKey1.getKeyId(), ObjTest.subjectKey1.getKeyType()), "test", true));
        assertThat(c_old, not(samePropertyValuesAs(c)));
        log.debug("DTO after modif : {}", c);
        log.debug("DTO attribute after modif : {}", c.getValidatedBy());
        m = factoryDTO.from(c);
        log.debug("model before saved modif : {}", m);
        log.debug("model attribute before saved modif : {}", m.getValidatedBy());
        m = repository.save(m);
        log.debug("model after saved modif : {}", m);
        log.debug("model attribute after saved modif : {}", m.getValidatedBy());
        ItemDTO c2 = factoryDTO.from(m);
        log.debug("DTO after saved model modifs : {}", c2);
        log.debug("DTO attribute after saved model modifs : {}", c2.getValidatedBy());
        assertThat(c2, equalTo(c));
        assertThat(c_old, not(samePropertyValuesAs(c2)));
    }

	@Test
	public void testDateOK() {
		Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
				ObjTest.instantToLocalDate(ObjTest.d1), ObjTest.instantToLocalDate(ObjTest.d2), ObjTest.d3,
				user1, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
				org1, redactor1);
		repository.saveAndFlush(m1);
	}

    @Test
    public void testOptionalDateOK() {
        Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
            null, null, ObjTest.d3,
            user1, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
            org1, redactorOptionalEndDate);
		Assertions.assertThrows(javax.validation.ValidationException.class, () -> {
			repository.saveAndFlush(m1);
		});

    }
    @Test
    public void testOptionalDateOK2() {
        Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
            ObjTest.instantToLocalDate(ObjTest.d1), ObjTest.instantToLocalDate(ObjTest.d2), ObjTest.d3,
            user1, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
            org1, redactorOptionalEndDate);
        repository.saveAndFlush(m1);
    }
    @Test
    public void testOptionalDateOK3() {
        Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
            ObjTest.instantToLocalDate(ObjTest.d1), null, ObjTest.d3,
            user1, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
            org1, redactorOptionalEndDate);
        repository.saveAndFlush(m1);
    }
    @Test
    public void testOptionalDateOK4() {
        Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
            null, ObjTest.instantToLocalDate(ObjTest.d2), ObjTest.d3,
            user1, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
            org1, redactorOptionalEndDate);
		Assertions.assertThrows(javax.validation.ValidationException.class, () -> {
			repository.saveAndFlush(m1);
		});
    }

    @Test
    public void testNotOptionalDateKO1() {
        Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
            null, null, ObjTest.d3,
            user1, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
            org1, redactor1);
		Assertions.assertThrows(javax.validation.ValidationException.class, () -> {
			repository.saveAndFlush(m1);
		});
    }
    @Test
    public void testNotOptionalDateKO2() {
        Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
            null, ObjTest.instantToLocalDate(ObjTest.d2), ObjTest.d3,
            user1, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
            org1, redactor1);
		Assertions.assertThrows(javax.validation.ValidationException.class, () -> {
			repository.saveAndFlush(m1);
		});
    }
    @Test
    public void testNotOptionalDateKO3() {
        Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
            ObjTest.instantToLocalDate(ObjTest.d1), null, ObjTest.d3,
            user1, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
            org1, redactor1);
		Assertions.assertThrows(javax.validation.ValidationException.class, () -> {
			repository.saveAndFlush(m1);
		});
    }
    @Test
    public void testMaxEndDateKO() {
        Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
            null, ObjTest.instantToLocalDate(ObjTest.d2), ObjTest.d2.plus(nbDaysMaxDuration+1, ChronoUnit.DAYS),
            user1, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
            org1, redactor1);
		Assertions.assertThrows(javax.validation.ValidationException.class, () -> {
			repository.saveAndFlush(m1);
		});
    }

	@Test
	public void testDateOKUpdateKO() {
		Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
				ObjTest.instantToLocalDate(ObjTest.d1), ObjTest.instantToLocalDate(ObjTest.d2), ObjTest.d3,
				user1, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
				org1, redactor1);
		repository.saveAndFlush(m1);
		m1.setEndDate(ObjTest.instantToLocalDate(ObjTest.d1));
		Assertions.assertThrows(javax.validation.ValidationException.class, () -> {
			repository.saveAndFlush(m1);
		});
	}

    @Test
    public void testDateOptionalOKUpdateKO() {
        Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
            ObjTest.instantToLocalDate(ObjTest.d1), null, ObjTest.d3,
            user1, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
            org1, redactorOptionalEndDate);
        repository.saveAndFlush(m1);
        m1.setEndDate(ObjTest.instantToLocalDate(ObjTest.d1));
		Assertions.assertThrows(javax.validation.ValidationException.class, () -> {
			repository.saveAndFlush(m1);
		});
    }
    @Test
    public void testDateOptionalOKUpdateOK() {
        Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
            ObjTest.instantToLocalDate(ObjTest.d1), null, ObjTest.d3,
            user1, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
            org1, redactorOptionalEndDate);
        repository.saveAndFlush(m1);
        m1.setEndDate(ObjTest.instantToLocalDate(ObjTest.d2));
        repository.saveAndFlush(m1);
    }

	@Test
	public void testDateKOLT() {
		Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
				ObjTest.instantToLocalDate(ObjTest.d3), ObjTest.instantToLocalDate(ObjTest.d1), ObjTest.d1,
				user1, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
				org1, redactor1);
		Assertions.assertThrows(javax.validation.ValidationException.class, () -> {
			repository.saveAndFlush(m1);
		});
	}
    @Test
    public void testDateOptionalKOLT() {
        Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
            ObjTest.instantToLocalDate(ObjTest.d3), ObjTest.instantToLocalDate(ObjTest.d1), ObjTest.d1,
            user1, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
            org1, redactorOptionalEndDate);
		Assertions.assertThrows(javax.validation.ValidationException.class, () -> {
        	repository.saveAndFlush(m1);
		});
    }

	@Test
	public void testDateKOEQ() {
		Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
				ObjTest.instantToLocalDate(ObjTest.d1), ObjTest.instantToLocalDate(ObjTest.d1), ObjTest.d1,
				user1, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
				org1, redactor1);
		Assertions.assertThrows(javax.validation.ValidationException.class, () -> {
			repository.saveAndFlush(m1);
		});
	}
    @Test
    public void testDateOptionalKOEQ() {
        Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
            ObjTest.instantToLocalDate(ObjTest.d1), ObjTest.instantToLocalDate(ObjTest.d1), ObjTest.d1,
            user1, DEFAULT_STATUS, "summary" + INDICE_1, true, true,
            org1, redactorOptionalEndDate);
		Assertions.assertThrows(javax.validation.ValidationException.class, () -> {
        	repository.saveAndFlush(m1);
		});
    }

	@Test
	public void testInsert() {
		News n1 = new News("Titre " + INDICE_A, "enclosure" + INDICE_A, "body"
				+ INDICE_A, ObjTest.instantToLocalDate(ObjTest.d1), ObjTest.instantToLocalDate(ObjTest.d3),
				ObjTest.d2, user1, ItemStatus.DRAFT, "summary"
						+ INDICE_A,  true, true, org2, redactor2);

		log.info("Before insert : {}", n1);
		repository.save(n1);
		assertThat(n1.getId(), not(nullValue()));
		log.info("After insert : {}", n1);

		Optional<AbstractItem> optionalItem = repository.findById(n1.getId());
		News n2 = (News) optionalItem.orElse(null);
		log.info("After select : {}", n2);
		assertThat(n2, not(nullValue()));
		assertThat(n1, equalTo(n2));

		n2.setBody("UPDATE BODY");
		n2.setEndDate(ObjTest.instantToLocalDate(ObjTest.d3));
		n2.setStatus(DEFAULT_STATUS);
		repository.save(n2);
		log.info("After update : {}", n2);
		assertThat(repository.existsById(n2.getId()) , is(true));
		n2 = (News) repository.getReferenceById(n2.getId());
		assertThat(n2, not(nullValue()));
		log.info("After select : {}", n2);
        assertThat(repository.count(), equalTo(5L));

		List<AbstractItem> result = repository.findAll();
		assertThat(result.size(), is(5));
		assertThat(result, hasItem(n2));
		result = Lists.newArrayList(repository.findAll(ItemPredicates
				.NewsItems()));
		assertThat(result.size(), is(2));
		assertThat(result, hasItem(n2));

		repository.deleteById(n2.getId());
		log.debug("nb returned : {}", repository.findAll().size());
		assertThat(repository.findAll().size(), is(4));
		assertThat(repository.existsById(n2.getId()), is(false));
	}

    @Test
    public void testRequestArchiving() {
        News n1 = new News("Titre " + INDICE_A, "enclosure" + INDICE_A, "body"
            + INDICE_A, LocalDate.now().minusDays(7), LocalDate.now().minusDays(1),
            ObjTest.d2, user1, ItemStatus.PUBLISHED, "summary"
            + INDICE_A, true, true, org2, redactor2);

        repository.save(n1);
        assertThat(n1.getId(), not(nullValue()));

        repository.archiveExpiredPublished();
        Optional<AbstractItem> optionalItem = repository.findById(n1.getId());
		News n2 = (News) optionalItem.orElse(null);
        assertThat(n2, not(nullValue()));
        assertThat(n2.getStatus(), not(nullValue()));

        assertThat(n2.getStatus(), equalTo(ItemStatus.ARCHIVED));
    }

    @Test
    public void testRequestArchivingOptionalDate() {
        News n1 = new News("Titre " + INDICE_A, "enclosure" + INDICE_A, "body"
            + INDICE_A, LocalDate.now().minusDays(7), null,
            ObjTest.d2, user1, ItemStatus.PUBLISHED, "summary"
            + INDICE_A, true, true, org2, redactorOptionalEndDate);

        repository.save(n1);
        assertThat(n1.getId(), not(nullValue()));

        repository.archiveExpiredPublished();
        Optional<AbstractItem> optionalItem = repository.findById(n1.getId());
		News n2 = (News) optionalItem.orElse(null);
        assertThat(n2, not(nullValue()));

        assertThat(n1, equalTo(n2));
    }

    @Test
    public void testRequestScheduledPublished() {
        News n1 = new News("Titre " + INDICE_A, "enclosure" + INDICE_A, "body"
            + INDICE_A, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1),
            ObjTest.d2, user1, ItemStatus.SCHEDULED, "summary"
            + INDICE_A, true, true, org2, redactor2);

        repository.save(n1);
        assertThat(n1.getId(), not(nullValue()));

        repository.publishScheduled();

        Optional<AbstractItem> optionalItem = repository.findById(n1.getId());
		News n2 = (News) optionalItem.orElse(null);
        assertThat(n2, not(nullValue()));
        assertThat(n2.getStatus(), not(nullValue()));

        assertThat(n2.getStatus(), equalTo(ItemStatus.PUBLISHED));
    }

    @Test
    public void testRequestScheduledPublishedOptionalDate() {
        News n1 = new News("Titre " + INDICE_A, "enclosure" + INDICE_A, "body"
            + INDICE_A, LocalDate.now().minusDays(1), null,
            ObjTest.d2, user1, ItemStatus.SCHEDULED, "summary"
            + INDICE_A, true, true, org2, redactorOptionalEndDate);

        repository.save(n1);
        assertThat(n1.getId(), not(nullValue()));

        repository.publishScheduled();

        Optional<AbstractItem> optionalItem = repository.findById(n1.getId());
		News n2 = (News) optionalItem.orElse(null);
        assertThat(n2, not(nullValue()));
        assertThat(n2.getStatus() , not(nullValue()));

        assertThat(n2.getStatus(), equalTo(ItemStatus.PUBLISHED));

		// Check on Timezone between JPA and database
		assertThat("The PublishScheduled task run an update with a wrong timezone ! Check your DB/OS/JVM/DB URL property time_zone.",
				n2.getLastModifiedDate().isAfter(n1.getLastModifiedDate().minusSeconds(1)));
    }

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findAll()}.
	 */
	@Test
	public void testFindAll() {
		assertThat(repository.findAll().size(), is(4));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#existsById(Object)} (java.io.Serializable)}
	 * .
	 */
	@Test
	public void testExists() {
		assertThat(repository.existsById(repository.findAll().get(0).getId()) , is(true));

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#count()}.
	 */
	@Test
	public void testCount() {
        assertThat(repository.count(), equalTo(4L));
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
	}
}