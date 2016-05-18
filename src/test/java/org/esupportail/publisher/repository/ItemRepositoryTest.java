package org.esupportail.publisher.repository;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.ItemDTOSelectorFactory;
import org.esupportail.publisher.web.rest.dto.ItemDTO;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;
import org.esupportail.publisher.web.rest.dto.SubjectKeyDTO;
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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
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

	private Organization org1;
	private Organization org2;
	private Redactor redactor1;
	private Redactor redactor2;

    private User user1;private User user2;private User user3;

	@Before
	public void setUp() throws Exception {
		log.info("starting up " + this.getClass().getName());

        user1 = userRepo.findOne(ObjTest.subject1);
        user2 = userRepo.findOne(ObjTest.subject2);
        user3 = userRepo.findOne(ObjTest.subject3);

		org1 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_1));
		org2 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_2));

		redactor1 = redactorRepo.saveAndFlush(ObjTest.newRedactor(INDICE_1));
		redactor2 = redactorRepo.saveAndFlush(ObjTest.newRedactor(INDICE_2));

		Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
				ObjTest.d1.toLocalDate(), ObjTest.d3.toLocalDate(), ObjTest.d2,
				user2, DEFAULT_STATUS, "summary" + INDICE_1,
				org1, redactor1);
		repository.saveAndFlush(m1);
		News m2 = new News("Titre " + INDICE_2, "enclosure" + INDICE_2, "body"
				+ INDICE_2, ObjTest.d1.toLocalDate(), ObjTest.d3.toLocalDate(),
				ObjTest.d2, user2, DEFAULT_STATUS, "summary"
						+ INDICE_2, org2, redactor2);
		repository.saveAndFlush(m2);
		Resource m3 = new Resource("Titre " + INDICE_3, "enclosure" + INDICE_3,
				"ressourceUrl" + INDICE_3, ObjTest.d1.toLocalDate(),
				ObjTest.d3.toLocalDate(), ObjTest.d2, user2,
				DEFAULT_STATUS, "summary" + INDICE_3, org1, redactor2);
		repository.saveAndFlush(m3);
        Flash m4 = new Flash("Titre " + INDICE_4, "enclosure" + INDICE_4, "body"
            + INDICE_4, ObjTest.d1.toLocalDate(), ObjTest.d3.toLocalDate(),
            ObjTest.d2, user2, DEFAULT_STATUS, "summary"
            + INDICE_4, org2, redactor2);
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
				ObjTest.d1.toLocalDate(), ObjTest.d2.toLocalDate(), ObjTest.d3,
				user1, DEFAULT_STATUS, "summary" + INDICE_1,
				org1, redactor1);
		repository.saveAndFlush(m1);
	}

	@Test(expected = javax.validation.ValidationException.class)
	public void testDateOKUpdateKO() {
		Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
				ObjTest.d1.toLocalDate(), ObjTest.d2.toLocalDate(), ObjTest.d3,
				user1, DEFAULT_STATUS, "summary" + INDICE_1,
				org1, redactor1);
		repository.saveAndFlush(m1);
		m1.setEndDate(ObjTest.d1.toLocalDate());
		repository.saveAndFlush(m1);
	}

	@Test(expected = javax.validation.ValidationException.class)
	public void testDateKOLT() {
		Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
				ObjTest.d3.toLocalDate(), ObjTest.d1.toLocalDate(), ObjTest.d1,
				user1, DEFAULT_STATUS, "summary" + INDICE_1,
				org1, redactor1);
		repository.saveAndFlush(m1);
	}

	@Test(expected = javax.validation.ValidationException.class)
	public void testDateKOEQ() {
		Media m1 = new Media("Titre " + INDICE_1, "enclosure" + INDICE_1,
				ObjTest.d1.toLocalDate(), ObjTest.d1.toLocalDate(), ObjTest.d1,
				user1, DEFAULT_STATUS, "summary" + INDICE_1,
				org1, redactor1);
		repository.saveAndFlush(m1);
	}

	@Test
	public void testInsert() {
		News n1 = new News("Titre " + INDICE_A, "enclosure" + INDICE_A, "body"
				+ INDICE_A, ObjTest.d1.toLocalDate(), ObjTest.d3.toLocalDate(),
				ObjTest.d2, user1, ItemStatus.DRAFT, "summary"
						+ INDICE_A, org2, redactor2);

		log.info("Before insert : " + n1.toString());
		repository.save(n1);
		assertNotNull(n1.getId());
		log.info("After insert : " + n1.toString());

		News n2 = (News) repository.findOne(n1.getId());
		log.info("After select : " + n2.toString());
		assertNotNull(n2);
		assertEquals(n1, n2);

		n2.setBody("UPDATE BODY");
		n2.setEndDate(ObjTest.d3.toLocalDate());
		n2.setStatus(DEFAULT_STATUS);
		repository.save(n2);
		log.info("After update : " + n2.toString());
		assertTrue(repository.exists(n2.getId()));
		n2 = (News) repository.getOne(n2.getId());
		assertNotNull(n2);
		log.info("After select : " + n2.toString());
		assertTrue(repository.count() == 5);

		List<AbstractItem> result = repository.findAll();
		assertThat(result.size(), is(5));
		assertThat(result, hasItem(n2));
		result = Lists.newArrayList(repository.findAll(ItemPredicates
				.NewsItems()));
		assertThat(result.size(), is(2));
		assertThat(result, hasItem(n2));

		repository.delete(n2.getId());
		log.debug("nb returned : {}", repository.findAll().size());
		assertThat(repository.findAll().size(), is(4));
		assertFalse(repository.exists(n2.getId()));
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
		assertTrue(repository.count() == 4);
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
