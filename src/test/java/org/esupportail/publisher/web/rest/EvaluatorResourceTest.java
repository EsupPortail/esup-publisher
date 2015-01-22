package org.esupportail.publisher.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
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
	//
	// private static final String DEFAULT_GROUP_NAME = "SAMPLE_TEXT";
	// private static final String UPDATED_GROUP_NAME = "UPDATED_TEXT";

	@Inject
	private EvaluatorRepository<AbstractEvaluator> evaluatorRepository;

	private MockMvc restEvaluatorMockMvc;

	private AbstractEvaluator evaluator;

	@PostConstruct
	public void setup() {
		MockitoAnnotations.initMocks(this);
		EvaluatorResource evaluatorResource = new EvaluatorResource();
		ReflectionTestUtils.setField(evaluatorResource, "evaluatorRepository",
				evaluatorRepository);
		this.restEvaluatorMockMvc = MockMvcBuilders.standaloneSetup(
				evaluatorResource).build();
	}

	@Before
	public void initTest() {
		UserMultivaluedAttributesEvaluator ev = new UserMultivaluedAttributesEvaluator();
		ev.setAttribute(DEFAULT_ATTRIBUTE);
		ev.setValue(DEFAULT_VALUE);
		ev.setMode(DEFAULT_MODE);
		Set<AbstractEvaluator> set = new HashSet<AbstractEvaluator>();
		set.add(ev);
		evaluator = new OperatorEvaluator(DEFAULT_TYPE, set);
		// evaluator.setType(DEFAULT_TYPE);
		// evaluator.setGroupName(DEFAULT_GROUP_NAME);
	}

	@Test
	@Transactional
	public void createEvaluator() throws Exception {
		// Validate the database is empty
		assertThat(evaluatorRepository.findAll()).hasSize(0);

		// Create the Evaluator
		restEvaluatorMockMvc.perform(
                post("/api/evaluators").contentType(
                        TestUtil.APPLICATION_JSON_UTF8).content(
                        TestUtil.convertObjectToJsonBytes(evaluator)))
				.andExpect(status().isCreated());

		// Validate the Evaluator in the database
		List<AbstractEvaluator> evaluators = evaluatorRepository.findAll();
		assertThat(evaluators).hasSize(2);
		AbstractEvaluator testEvaluator = evaluators.iterator().next();
		if (testEvaluator instanceof UserMultivaluedAttributesEvaluator) {
			testEvaluator = evaluators.iterator().next();
		}
		assertThat(((OperatorEvaluator) testEvaluator).getType()).isEqualTo(
				DEFAULT_TYPE);
		UserMultivaluedAttributesEvaluator umae = (UserMultivaluedAttributesEvaluator) ((OperatorEvaluator) testEvaluator)
				.getEvaluators().iterator().next();
		assertThat(umae.getAttribute()).isEqualTo(DEFAULT_ATTRIBUTE);
		assertThat(umae.getValue()).isEqualTo(DEFAULT_VALUE);
		assertThat(umae.getMode()).isEqualTo(DEFAULT_MODE);
		// assertThat(testEvaluator.getType()).isEqualTo(DEFAULT_TYPE);
		// assertThat(testEvaluator.getGroupName()).isEqualTo(DEFAULT_GROUP_NAME);
		;
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
						jsonPath("$.[0].id")
								.value(evaluator.getId().intValue()))
				.andExpect(
						jsonPath("$.[0].type").value(DEFAULT_TYPE.toString()))
				.andExpect(
						jsonPath("$.[0].evaluators.[0].attribute").value(
								DEFAULT_ATTRIBUTE.toString()))
				.andExpect(
						jsonPath("$.[0].evaluators.[0].value").value(
								DEFAULT_VALUE.toString()))
				.andExpect(
						jsonPath("$.[0].evaluators.[0].mode").value(
								DEFAULT_MODE.toString()));
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
				.andExpect(
						jsonPath("$.evaluators.[0].attribute").value(
								DEFAULT_ATTRIBUTE.toString()))
				.andExpect(
						jsonPath("$.evaluators.[0].value").value(
								DEFAULT_VALUE.toString()))
				.andExpect(
						jsonPath("$.evaluators.[0].mode").value(
								DEFAULT_MODE.toString()));
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
		assertThat(evaluators).hasSize(2);
		AbstractEvaluator testEvaluator = evaluators.iterator().next();
		assertThat(((OperatorEvaluator) testEvaluator).getType()).isEqualTo(
				UPDATED_TYPE);
		UserMultivaluedAttributesEvaluator umaeu = (UserMultivaluedAttributesEvaluator) ((OperatorEvaluator) testEvaluator)
				.getEvaluators().iterator().next();
		assertThat(umaeu.getAttribute()).isEqualTo(UPDATED_ATTRIBUTE);
		assertThat(umaeu.getValue()).isEqualTo(UPDATED_VALUE);
		assertThat(umaeu.getMode()).isEqualTo(UPDATED_MODE);
		// assertThat(testEvaluator.getType()).isEqualTo(UPDATED_TYPE);
		// assertThat(testEvaluator.getGroupName()).isEqualTo(UPDATED_GROUP_NAME);
		;
	}

	@Test
	@Transactional
	public void deleteEvaluator() throws Exception {
		// Initialize the database
		evaluatorRepository.saveAndFlush(evaluator);

		// Get the evaluator
		restEvaluatorMockMvc.perform(
				delete("/api/evaluators/{id}", evaluator.getId()).accept(
						TestUtil.APPLICATION_JSON_UTF8)).andExpect(
				status().isOk());

		// Validate the database is empty
		List<AbstractEvaluator> evaluators = evaluatorRepository.findAll();
		assertThat(evaluators).hasSize(0);
	}
}
