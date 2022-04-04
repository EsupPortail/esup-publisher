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
package org.esupportail.publisher.web.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.enums.OperatorType;
import org.esupportail.publisher.domain.enums.StringEvaluationMode;
import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.esupportail.publisher.domain.evaluators.OperatorEvaluator;
import org.esupportail.publisher.domain.evaluators.UserMultivaluedAttributesEvaluator;
import org.esupportail.publisher.repository.EvaluatorRepository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test class for the EvaluatorResource REST controller.
 *
 * @see EvaluatorResource
 */
@ExtendWith(SpringExtension.class)//@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class EvaluatorResourceTest {

	private static final String DEFAULT_ATTRIBUTE = "SAMPLE_TEXT";
	private static final String UPDATED_ATTRIBUTE = "UPDATED_TEXT";

	private static final String DEFAULT_VALUE = "SAMPLE_TEXT";
	private static final String UPDATED_VALUE = "UPDATED_TEXT";

	private static final StringEvaluationMode DEFAULT_MODE = StringEvaluationMode.EQUALS;
	private static final StringEvaluationMode UPDATED_MODE = StringEvaluationMode.STARTS_WITH;

	private static final OperatorType DEFAULT_TYPE = OperatorType.AND;
	private static final OperatorType UPDATED_TYPE = OperatorType.OR;
	// private static final String DEFAULT_GROUP_NAME = "SAMPLE_TEXT";
	// private static final String UPDATED_GROUP_NAME = "UPDATED_TEXT";

	@Inject
	private EvaluatorRepository<AbstractEvaluator> evaluatorRepository;

	private MockMvc restEvaluatorMockMvc;

	private AbstractEvaluator evaluator;

	@PostConstruct
	public void setup() {
		MockitoAnnotations.openMocks(this);
		EvaluatorResource evaluatorResource = new EvaluatorResource();
		ReflectionTestUtils.setField(evaluatorResource, "evaluatorRepository",
				evaluatorRepository);
		this.restEvaluatorMockMvc = MockMvcBuilders.standaloneSetup(
				evaluatorResource).build();
	}

	@BeforeAll
	public void initTest() {
		UserMultivaluedAttributesEvaluator ev = new UserMultivaluedAttributesEvaluator();
		ev.setAttribute(DEFAULT_ATTRIBUTE);
		ev.setValue(DEFAULT_VALUE);
		ev.setMode(DEFAULT_MODE);
		Set<AbstractEvaluator> set = new HashSet<>();
		set.add(ev);
		evaluator = new OperatorEvaluator(DEFAULT_TYPE, set);
		// evaluator.setType(DEFAULT_TYPE);
		// evaluator.setGroupName(DEFAULT_GROUP_NAME);
	}

	@Test
	@Transactional
	public void createEvaluator() throws Exception {
		// Validate the database is empty
		assertThat(evaluatorRepository.findAll(), hasSize(0));

		// Create the Evaluator
		restEvaluatorMockMvc.perform(
                post("/api/evaluators").contentType(
                        TestUtil.APPLICATION_JSON_UTF8).content(
                        TestUtil.convertObjectToJsonBytes(evaluator)))
				.andExpect(status().isCreated());

		// Validate the Evaluator in the database
		List<AbstractEvaluator> evaluators = evaluatorRepository.findAll();
		assertThat(evaluators, hasSize(2));
		AbstractEvaluator testEvaluator = evaluators.iterator().next();
		if (testEvaluator instanceof UserMultivaluedAttributesEvaluator) {
			testEvaluator = evaluators.iterator().next();
		}
		assertThat(((OperatorEvaluator) testEvaluator).getType(), equalTo(DEFAULT_TYPE));
		UserMultivaluedAttributesEvaluator umae = (UserMultivaluedAttributesEvaluator) ((OperatorEvaluator) testEvaluator)
				.getEvaluators().iterator().next();
		assertThat(umae.getAttribute(), equalTo(DEFAULT_ATTRIBUTE));
		assertThat(umae.getValue(), equalTo(DEFAULT_VALUE));
		assertThat(umae.getMode(), equalTo(DEFAULT_MODE));
		// assertThat(testEvaluator.getType(), equalTo(DEFAULT_TYPE));
		// assertThat(testEvaluator.getGroupName(), equalTo(DEFAULT_GROUP_NAME));
	}

	@Test
	@Transactional
	public void getAllEvaluators() throws Exception {
		// Initialize the database
		evaluatorRepository.saveAndFlush(evaluator);

		// Get all the evaluators
		restEvaluatorMockMvc
				.perform(get("/api/evaluators"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(
						jsonPath("$.[0].id").value(evaluator.getId().intValue()))
				.andExpect(
						jsonPath("$.[0].type").value(DEFAULT_TYPE.toString()))
				.andExpect(
						jsonPath("$.[0].evaluators.[0].attribute").value(DEFAULT_ATTRIBUTE))
				.andExpect(
						jsonPath("$.[0].evaluators.[0].value").value(DEFAULT_VALUE))
				.andExpect(
						jsonPath("$.[0].evaluators.[0].mode").value(DEFAULT_MODE.toString()));
		// .andExpect(
		// jsonPath("$.[0].type").value(DEFAULT_TYPE.toString()))
		// .andExpect(
		// jsonPath("$.[0].groupName").value(
		// DEFAULT_GROUP_NAME.toString()));
	}

	@Test
	@Transactional
	public void getEvaluator() throws Exception {
		// Initialize the database
		evaluatorRepository.saveAndFlush(evaluator);

		// Get the evaluator
		restEvaluatorMockMvc
				.perform(get("/api/evaluators/{id}", evaluator.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(evaluator.getId().intValue()))
				.andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
				.andExpect(jsonPath("$.evaluators.[0].attribute").value(DEFAULT_ATTRIBUTE))
				.andExpect(jsonPath("$.evaluators.[0].value").value(DEFAULT_VALUE))
				.andExpect(jsonPath("$.evaluators.[0].mode").value(DEFAULT_MODE.toString()));
		// .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
		// .andExpect(
		// jsonPath("$.groupName").value(
		// DEFAULT_GROUP_NAME.toString()));
	}

	@Test
	@Transactional
	public void getNonExistingEvaluator() throws Exception {
		// Get the evaluator
		restEvaluatorMockMvc.perform(get("/api/evaluators/{id}", 1L))
				.andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void updateEvaluator() throws Exception {
		// Initialize the database
		evaluatorRepository.saveAndFlush(evaluator);

		// Update the evaluator

		((OperatorEvaluator) evaluator).setType(UPDATED_TYPE);
		UserMultivaluedAttributesEvaluator umae = (UserMultivaluedAttributesEvaluator) ((OperatorEvaluator) evaluator)
				.getEvaluators().iterator().next();
		umae.setAttribute(UPDATED_ATTRIBUTE);
		umae.setValue(UPDATED_VALUE);
		umae.setMode(UPDATED_MODE);
		// evaluator.setType(UPDATED_TYPE);
		// evaluator.setGroupName(UPDATED_GROUP_NAME);
		restEvaluatorMockMvc.perform(
				put("/api/evaluators").contentType(
						TestUtil.APPLICATION_JSON_UTF8).content(
						TestUtil.convertObjectToJsonBytes(evaluator)))
				.andExpect(status().isOk());

		// Validate the Evaluator in the database
		List<AbstractEvaluator> evaluators = evaluatorRepository.findAll();
		assertThat(evaluators, hasSize(2));
		AbstractEvaluator testEvaluator = evaluators.iterator().next();
		assertThat(((OperatorEvaluator) testEvaluator).getType(), equalTo(UPDATED_TYPE));
		UserMultivaluedAttributesEvaluator umaeu = (UserMultivaluedAttributesEvaluator) ((OperatorEvaluator) testEvaluator)
				.getEvaluators().iterator().next();
		assertThat(umaeu.getAttribute(), equalTo(UPDATED_ATTRIBUTE));
		assertThat(umaeu.getValue(), equalTo(UPDATED_VALUE));
		assertThat(umaeu.getMode(), equalTo(UPDATED_MODE));
		// assertThat(testEvaluator.getType(), equalTo(UPDATED_TYPE));
		// assertThat(testEvaluator.getGroupName(), equalTo(UPDATED_GROUP_NAME));
	}

	@Test
	@Transactional
	public void deleteEvaluator() throws Exception {
		// Initialize the database
		evaluatorRepository.saveAndFlush(evaluator);

		// Get the evaluator
		restEvaluatorMockMvc.perform(
				delete("/api/evaluators/{id}", evaluator.getId()).accept(
						TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());

		// Validate the database is empty
		List<AbstractEvaluator> evaluators = evaluatorRepository.findAll();
		assertThat(evaluators, hasSize(0));
	}
}