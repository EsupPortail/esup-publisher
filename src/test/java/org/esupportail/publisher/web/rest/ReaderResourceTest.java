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

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.Reader;
import org.esupportail.publisher.domain.enums.ClassificationDecorType;
import org.esupportail.publisher.domain.enums.ItemType;
import org.esupportail.publisher.repository.ReaderRepository;

import org.hamcrest.Matchers;
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
 * Test class for the ReaderResource REST controller.
 *
 * @see ReaderResource
 */
@ExtendWith(SpringExtension.class)//@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ReaderResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final String DEFAULT_DISPLAY_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_DISPLAY_NAME = "UPDATED_TEXT";

    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    private static final Set<ItemType> DEFAULT_ITEMTYPE = EnumSet.of(ItemType.NEWS, ItemType.MEDIA);
    private static final String[] DEFAULT_ITEMTYPE_ARRAY = {ItemType.NEWS.name(), ItemType.MEDIA.name()};
    //private static final Set<String> DEFAULT_ITEMTYPE_STRING = enumsToString(DEFAULT_ITEMTYPE);
    private static final Set<ItemType> UPDATED_ITEMTYPE = EnumSet.of(ItemType.NEWS);
    private static final Set<ClassificationDecorType> DEFAULT_DECORTYPE = EnumSet.of(ClassificationDecorType.COLOR);
    private static final String[] DEFAULT_DECORTYPE_ARRAY = {ClassificationDecorType.COLOR.name()};
    //private static final Set<String> DEFAULT_DECORTYPE_STRING = enumsToString(DEFAULT_DECORTYPE);
    private static final Set<ClassificationDecorType> UPDATED_DECORTYPE = EnumSet.of(ClassificationDecorType.ENCLOSURE);


    @Inject
    private ReaderRepository readerRepository;

    private MockMvc restReaderMockMvc;

    private Reader reader;

    /*private static Set<String> enumsToString(@NotNull Set<ItemType> types) {
        Set<String> result = Sets.newHashSet();
        for (ItemType type : types) {
            result.add(type.name());
        }
        return result;
    }*/

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReaderResource readerResource = new ReaderResource();
        ReflectionTestUtils.setField(readerResource, "readerRepository", readerRepository);
        this.restReaderMockMvc = MockMvcBuilders.standaloneSetup(readerResource).build();
    }

    @BeforeAll
    public void initTest() {
        reader = new Reader();
        reader.setName(DEFAULT_NAME);
        reader.setDisplayName(DEFAULT_DISPLAY_NAME);
        reader.setDescription(DEFAULT_DESCRIPTION);
        reader.setAuthorizedTypes(DEFAULT_ITEMTYPE);
        reader.setClassificationDecorations(DEFAULT_DECORTYPE);
    }

    @Test
    @Transactional
    public void createReader() throws Exception {
        // Validate the database is empty
        assertThat(readerRepository.findAll(), hasSize(0));

        // Create the Reader
        restReaderMockMvc.perform(post("/api/readers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(reader)))
            .andExpect(status().isCreated());

        // Validate the Reader in the database
        List<Reader> readers = readerRepository.findAll();
        assertThat(readers, hasSize(1));
        Reader testReader = readers.iterator().next();
        assertThat(testReader.getName(), equalTo(DEFAULT_NAME));
        assertThat(testReader.getDisplayName(), equalTo(DEFAULT_DISPLAY_NAME));
        assertThat(testReader.getDescription(), equalTo(DEFAULT_DESCRIPTION));
        assertThat(testReader.getAuthorizedTypes(), equalTo(DEFAULT_ITEMTYPE));
        assertThat(testReader.getClassificationDecorations(), equalTo(DEFAULT_DECORTYPE));
    }

    @Test
    @Transactional
    public void getAllReaders() throws Exception {
        // Initialize the database
        readerRepository.saveAndFlush(reader);

        // Get all the readers
        restReaderMockMvc.perform(get("/api/readers"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[0].id").value(reader.getId().intValue()))
            .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.[0].displayName").value(DEFAULT_DISPLAY_NAME))
            .andExpect(jsonPath("$.[0].description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.[0].authorizedTypes", Matchers.hasSize(DEFAULT_ITEMTYPE.size())))
            .andExpect(jsonPath("$.[0].authorizedTypes", Matchers.hasItems(DEFAULT_ITEMTYPE_ARRAY)))
            .andExpect(jsonPath("$.[0].classificationDecorations", Matchers.hasItems(DEFAULT_DECORTYPE_ARRAY)))
            .andExpect(jsonPath("$.[0].classificationDecorations", Matchers.hasSize(DEFAULT_DECORTYPE.size())));
    }

    @Test
    @Transactional
    public void getReader() throws Exception {
        // Initialize the database
        readerRepository.saveAndFlush(reader);


        // Get the reader
        restReaderMockMvc.perform(get("/api/readers/{id}", reader.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(reader.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.displayName").value(DEFAULT_DISPLAY_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.authorizedTypes", Matchers.hasSize(DEFAULT_ITEMTYPE.size())))
            .andExpect(jsonPath("$.authorizedTypes", Matchers.hasItems(DEFAULT_ITEMTYPE_ARRAY)))
            .andExpect(jsonPath("$.classificationDecorations", Matchers.hasItems(DEFAULT_DECORTYPE_ARRAY)))
            .andExpect(jsonPath("$.classificationDecorations", Matchers.hasSize(DEFAULT_DECORTYPE.size())));
    }

    @Test
    @Transactional
    public void getNonExistingReader() throws Exception {
        // Get the reader
        restReaderMockMvc.perform(get("/api/readers/{id}", 1L))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReader() throws Exception {
        // Initialize the database
        readerRepository.saveAndFlush(reader);

        // Update the reader
        reader.setName(UPDATED_NAME);
        reader.setDisplayName(UPDATED_DISPLAY_NAME);
        reader.setDescription(UPDATED_DESCRIPTION);
        reader.setAuthorizedTypes(UPDATED_ITEMTYPE);
        reader.setClassificationDecorations(UPDATED_DECORTYPE);
        restReaderMockMvc.perform(
            put("/api/readers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reader)))
            .andExpect(status().isOk());

        // Validate the Reader in the database
        List<Reader> readers = readerRepository.findAll();
        assertThat(readers, hasSize(1));
        Reader testReader = readers.iterator().next();
        assertThat(testReader.getName(), equalTo(UPDATED_NAME));
        assertThat(testReader.getDisplayName(), equalTo(UPDATED_DISPLAY_NAME));
        assertThat(testReader.getDescription(), equalTo(UPDATED_DESCRIPTION));
        assertThat(testReader.getAuthorizedTypes(), equalTo(UPDATED_ITEMTYPE));
        assertThat(testReader.getClassificationDecorations(), equalTo(UPDATED_DECORTYPE));
    }

    @Test
    @Transactional
    public void deleteReader() throws Exception {
        // Initialize the database
        readerRepository.saveAndFlush(reader);

        // Get the reader
        restReaderMockMvc.perform(delete("/api/readers/{id}", reader.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Reader> readers = readerRepository.findAll();
        assertThat(readers, hasSize(0));
    }
}