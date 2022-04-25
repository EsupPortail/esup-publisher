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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.LinkedFileItem;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.Redactor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)//@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Rollback
@Transactional
@Slf4j
public class LinkedFileItemRepositoryTest {

	@Inject
    private LinkedFileItemRepository repository;

    @Inject
    private OrganizationRepository orgRepo;

    @Inject
    private RedactorRepository redactorRepo;

    @Inject
    private ItemRepository<News> itemRepo;

    final static String INDICE_1 = "ind1";
	final static String FILE_PATH_DEFAULT = "view/file/20170802/éditer-255222555226.txt";
	final static String FILE_PATH_1 = "view/file/20170802/éditer-2.txt";

    private Organization org1;
    private Redactor redactor1;
    private News item1;

	@BeforeAll
	public void setUp() throws Exception {
		log.info("starting up {}", this.getClass().getName());

        org1 = orgRepo.saveAndFlush(ObjTest.newOrganization(INDICE_1));
        redactor1 = redactorRepo.saveAndFlush(ObjTest.newRedactor(INDICE_1));

        item1 = itemRepo.saveAndFlush(ObjTest
            .newNews(INDICE_1, org1, redactor1));
        itemRepo.saveAndFlush(item1);

		LinkedFileItem f = new LinkedFileItem(FILE_PATH_DEFAULT, item1);
		log.info("Before insert : {}", f);
		repository.saveAndFlush(f);
	}

    @Test(expected = DataIntegrityViolationException.class)
    public void testInsertDuplicate() {
        AbstractItem item = itemRepo.findAll().get(0);

        LinkedFileItem f = new LinkedFileItem(FILE_PATH_DEFAULT, item);
        log.info("Before insert : {}", f);
        repository.saveAndFlush(f);

    }

	@Test()
	public void testInsert() {
		AbstractItem item = itemRepo.findAll().get(0);

        LinkedFileItem f = new LinkedFileItem(FILE_PATH_1, item);
		log.info("Before insert : {}", f);
		repository.saveAndFlush(f);

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findAll()}.
	 */
	@Test
	public void testFindAll() {
        AbstractItem item = itemRepo.findAll().get(0);
		repository
				.saveAndFlush(new LinkedFileItem(FILE_PATH_1, item));
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
		assertThat(repository.count(), equalTo(1L));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#delete(Object)}
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