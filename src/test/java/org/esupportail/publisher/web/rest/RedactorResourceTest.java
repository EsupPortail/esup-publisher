package org.esupportail.publisher.web.rest;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.enums.WritingFormat;
import org.esupportail.publisher.domain.enums.WritingMode;
import org.esupportail.publisher.repository.RedactorRepository;
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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the RedactorResource REST controller.
 *
 * @see RedactorResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class RedactorResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final String DEFAULT_DISPLAY_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_DISPLAY_NAME = "UPDATED_TEXT";

    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    private static final WritingFormat DEFAULT_WRITING_FORMAT = WritingFormat.HTML;
    private static final WritingFormat UPDATED_WRITING_FORMAT = WritingFormat.HTML;

    private static final WritingMode DEFAULT_WRITING_MODE = WritingMode.STATIC;
    private static final WritingMode UPDATED_WRITING_MODE = WritingMode.TARGETS_ON_ITEM;

    private static final Integer DEFAULT_NB_LEVELS_CLASSIFICATION = 1;
    private static final Integer UPDATED_NB_LEVELS_CLASSIFICATION = 2;

    @Inject
    private RedactorRepository redactorRepository;

    private MockMvc restRedactorMockMvc;

    private Redactor redactor;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RedactorResource redactorResource = new RedactorResource();
        ReflectionTestUtils.setField(redactorResource, "redactorRepository",
            redactorRepository);
        this.restRedactorMockMvc = MockMvcBuilders.standaloneSetup(
            redactorResource).build();
    }

    @Before
    public void initTest() {
        redactor = new Redactor();
        redactor.setName(DEFAULT_NAME);
        redactor.setDisplayName(DEFAULT_DISPLAY_NAME);
        redactor.setDescription(DEFAULT_DESCRIPTION);
        redactor.setFormat(DEFAULT_WRITING_FORMAT);
        redactor.setWritingMode(DEFAULT_WRITING_MODE);
        redactor.setNbLevelsOfClassification(DEFAULT_NB_LEVELS_CLASSIFICATION);
    }

    @Test
    @Transactional
    public void createRedactor() throws Exception {
        // Validate the database is empty
        assertThat(redactorRepository.findAll()).hasSize(0);

        // Create the Redactor
        restRedactorMockMvc.perform(
            post("/api/redactors").contentType(
                TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(redactor)))
            .andExpect(status().isCreated());

        // Validate the Redactor in the database
        List<Redactor> redactors = redactorRepository.findAll();
        assertThat(redactors).hasSize(1);
        Redactor testRedactor = redactors.iterator().next();
        assertThat(testRedactor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRedactor.getDisplayName()).isEqualTo(
            DEFAULT_DISPLAY_NAME);
        assertThat(testRedactor.getDescription())
            .isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRedactor.getWritingMode()).isEqualTo(
            DEFAULT_WRITING_MODE);
        assertThat(testRedactor.getFormat()).isEqualTo(
            DEFAULT_WRITING_FORMAT);
        assertThat(testRedactor.getNbLevelsOfClassification()).isEqualTo(
            DEFAULT_NB_LEVELS_CLASSIFICATION);
        ;
    }

    @Test
    @Transactional
    public void getAllRedactors() throws Exception {
        // Initialize the database
        redactorRepository.saveAndFlush(redactor);

        // Get all the redactors
        restRedactorMockMvc
            .perform(get("/api/redactors"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                jsonPath("$.[0].id").value(redactor.getId().intValue()))
            .andExpect(
                jsonPath("$.[0].name").value(DEFAULT_NAME.toString()))
            .andExpect(
                jsonPath("$.[0].displayName").value(
                    DEFAULT_DISPLAY_NAME.toString()))
            .andExpect(
                jsonPath("$.[0].description").value(
                    DEFAULT_DESCRIPTION.toString()))
            .andExpect(
                jsonPath("$.[0].format").value(
                    DEFAULT_WRITING_FORMAT.name()))
            .andExpect(
                jsonPath("$.[0].writingMode").value(
                    DEFAULT_WRITING_MODE.name()))
            .andExpect(
                jsonPath("$.[0].nbLevelsOfClassification").value(
                    DEFAULT_NB_LEVELS_CLASSIFICATION));
    }

    @Test
    @Transactional
    public void getRedactor() throws Exception {
        // Initialize the database
        redactorRepository.saveAndFlush(redactor);

        // Get the redactor
        restRedactorMockMvc
            .perform(get("/api/redactors/{id}", redactor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(redactor.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(
                jsonPath("$.displayName").value(
                    DEFAULT_DISPLAY_NAME.toString()))
            .andExpect(
                jsonPath("$.description").value(
                    DEFAULT_DESCRIPTION.toString()))
            .andExpect(
                jsonPath("$.format").value(
                    DEFAULT_WRITING_FORMAT.name()))
            .andExpect(
                jsonPath("$.writingMode").value(
                    DEFAULT_WRITING_MODE.name()))
            .andExpect(
                jsonPath("$.nbLevelsOfClassification").value(
                    DEFAULT_NB_LEVELS_CLASSIFICATION));
    }

    @Test
    @Transactional
    public void getNonExistingRedactor() throws Exception {
        // Get the redactor
        restRedactorMockMvc.perform(get("/api/redactors/{id}", 1L))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRedactor() throws Exception {
        // Initialize the database
        redactorRepository.saveAndFlush(redactor);

        // Update the redactor
        redactor.setName(UPDATED_NAME);
        redactor.setDisplayName(UPDATED_DISPLAY_NAME);
        redactor.setDescription(UPDATED_DESCRIPTION);
        redactor.setFormat(UPDATED_WRITING_FORMAT);
        redactor.setWritingMode(UPDATED_WRITING_MODE);
        redactor.setNbLevelsOfClassification(UPDATED_NB_LEVELS_CLASSIFICATION);
        restRedactorMockMvc.perform(
            put("/api/redactors").contentType(
                TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(redactor)))
            .andExpect(status().isOk());

        // Validate the Redactor in the database
        List<Redactor> redactors = redactorRepository.findAll();
        assertThat(redactors).hasSize(1);
        Redactor testRedactor = redactors.iterator().next();
        assertThat(testRedactor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRedactor.getDisplayName()).isEqualTo(
            UPDATED_DISPLAY_NAME);
        assertThat(testRedactor.getDescription())
            .isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRedactor.getWritingMode()).isEqualTo(
            UPDATED_WRITING_MODE);
        assertThat(testRedactor.getFormat()).isEqualTo(
            UPDATED_WRITING_FORMAT);
        assertThat(testRedactor.getNbLevelsOfClassification()).isEqualTo(
            UPDATED_NB_LEVELS_CLASSIFICATION);
        ;
    }

    @Test
    @Transactional
    public void deleteRedactor() throws Exception {
        // Initialize the database
        redactorRepository.saveAndFlush(redactor);

        // Get the redactor
        restRedactorMockMvc.perform(
            delete("/api/redactors/{id}", redactor.getId()).accept(
                TestUtil.APPLICATION_JSON_UTF8)).andExpect(
            status().isOk());

        // Validate the database is empty
        List<Redactor> redactors = redactorRepository.findAll();
        assertThat(redactors).hasSize(0);
    }
}
