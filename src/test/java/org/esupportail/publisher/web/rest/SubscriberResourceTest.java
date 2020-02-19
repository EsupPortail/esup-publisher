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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.DatatypeConverter;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.SubjectContextKey;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.SubjectKeyExtended;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.enums.SubscribeType;
import org.esupportail.publisher.repository.ObjTest;
import org.esupportail.publisher.repository.SubscriberRepository;
import org.esupportail.publisher.service.factories.CompositeKeyDTOFactory;
import org.esupportail.publisher.service.factories.CompositeKeyExtendedDTOFactory;
import org.esupportail.publisher.service.factories.SubscriberResolvedDTOFactory;
import org.esupportail.publisher.web.rest.dto.ContextKeyDTO;
import org.esupportail.publisher.web.rest.dto.SubjectContextKeyDTO;
import org.esupportail.publisher.web.rest.dto.SubjectKeyExtendedDTO;
import org.esupportail.publisher.web.rest.dto.SubscriberResolvedDTO;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * Test class for the SubscriberResource REST controller.
 *
 * @see SubscriberResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Slf4j
public class SubscriberResourceTest {

	private static final SubscribeType DEFAULT_SUBSCRIBE_TYPE = SubscribeType.FORCED;
	private static final SubscribeType UPDATED_SUBSCRIBE_TYPE = SubscribeType.FREE;

	//private static final SubjectKey DEFAULT_SUBJECT = new SubjectKey("admin", SubjectType.PERSON);
	private static final SubjectKeyExtended DEFAULT_SUBJECT = new SubjectKeyExtended(new SubjectKey(
            "grouper.coll:collectivite:region_centre_val_de_loire:applications:gestion_et_reservation_de_ressources:" +
                    "administrateurs_des_ressources:test_de_longueur_totale_superieur_a_deux_cent_cinquante_six_characters:" +
                    "et_pourquoi_pas_vu_le_nombre_de_noeuds_dans_l_arborescence_des_groupes", SubjectType.GROUP));

	private static final ContextKey DEFAULT_CTX = new ContextKey(1L, ContextType.ORGANIZATION);

	private static final SubjectContextKey DEFAULT_SUBSCRIBE_KEY = new SubjectContextKey(
			DEFAULT_SUBJECT, DEFAULT_CTX);

	@Inject
	private SubscriberRepository subscriberRepository;

    @Inject
    private SubscriberResolvedDTOFactory subscriberResolvedDTOFactory;

    @Inject
    private transient CompositeKeyExtendedDTOFactory<SubjectKeyExtendedDTO, SubjectKeyExtended, String, String, SubjectType> subjectKeyExtendedConverter;
    @Inject
    @Named("contextKeyDTOFactoryImpl")
    private transient CompositeKeyDTOFactory<ContextKeyDTO, ContextKey, Long, ContextType> contextConverter;

	private MockMvc restSubscriberMockMvc;

	private Subscriber subscriber;

	@PostConstruct
	public void setup() {
		MockitoAnnotations.initMocks(this);
		SubscriberResource subscriberResource = new SubscriberResource();
		ReflectionTestUtils.setField(subscriberResource,
            "subscriberRepository", subscriberRepository);
        ReflectionTestUtils.setField(subscriberResource,
            "subscriberResolvedDTOFactory", subscriberResolvedDTOFactory);
        ReflectionTestUtils.setField(subscriberResource, "subjectKeyExtendedConverter", subjectKeyExtendedConverter);
        ReflectionTestUtils.setField(subscriberResource, "contextConverter", contextConverter);
		this.restSubscriberMockMvc = MockMvcBuilders.standaloneSetup(
				subscriberResource).build();
	}

	@Before
	public void initTest() {
		subscriber = new Subscriber();
		subscriber.setSubjectCtxId(DEFAULT_SUBSCRIBE_KEY);
		subscriber.setSubscribeType(DEFAULT_SUBSCRIBE_TYPE);
	}

	@Test
	@Transactional
	public void createSubscriber() throws Exception {
		// Validate the database is empty
		assertThat(subscriberRepository.findAll()).hasSize(0);

		// Create the Subscriber
        restSubscriberMockMvc.perform(
                post("/api/subscribers").contentType(
                        TestUtil.APPLICATION_JSON_UTF8).content(
                        TestUtil.convertObjectToJsonBytes(subscriber)))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/api/subscribers/" +
                    DatatypeConverter.printBase64Binary(
                        subscriber.getSubjectCtxId().getSubject().getKeyValue().getBytes(StandardCharsets.UTF_8)) + "/" +
                    subscriber.getSubjectCtxId().getSubject().getKeyType().getId() + "/" +
                    subscriber.getSubjectCtxId().getSubject().getKeyAttribute() + "/" +
                    subscriber.getSubjectCtxId().getContext().getKeyId() + "/" +
                    subscriber.getSubjectCtxId().getContext().getKeyType().name()));
                //.andDo(print());

		// Validate the Subscriber in the database
		List<Subscriber> subscribers = subscriberRepository.findAll();
		assertThat(subscribers).hasSize(1);
		Subscriber testSubscriber = subscribers.iterator().next();
		assertThat(testSubscriber.getSubscribeType()).isEqualTo(
				DEFAULT_SUBSCRIBE_TYPE);
		assertThat(testSubscriber.getSubjectCtxId()).isEqualTo(
				DEFAULT_SUBSCRIBE_KEY);
	}

	@Test
	@Transactional
	public void getAllSubscribers() throws Exception {
		// Initialize the database
		subscriberRepository.saveAndFlush(subscriber);

		// Get all the subscribers
		restSubscriberMockMvc
				.perform(get("/api/subscribers"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(
						jsonPath("$.[0].subjectCtxId.subject.keyValue").value(
                            DEFAULT_SUBSCRIBE_KEY.getSubject().getKeyValue()))
				.andExpect(
						jsonPath("$.[0].subjectCtxId.subject.keyType").value(
								DEFAULT_SUBSCRIBE_KEY.getSubject().getKeyType().getCode()))
                .andExpect(
                    jsonPath("$.[0].subjectCtxId.subject.keyAttribute").value(
                        DEFAULT_SUBSCRIBE_KEY.getSubject().getKeyAttribute()))
				.andExpect(
                    jsonPath("$.[0].subjectCtxId.context.keyId").value(
                        DEFAULT_SUBSCRIBE_KEY.getContext().getKeyId()
                            .intValue()))
				.andExpect(
						jsonPath("$.[0].subjectCtxId.context.keyType").value(
								DEFAULT_SUBSCRIBE_KEY.getContext().getKeyType().name()))
				.andExpect(
						jsonPath("$.[0].subscribeType").value(
								DEFAULT_SUBSCRIBE_TYPE.getName()));
	}

	@Test
	@Transactional
	public void getSubscriber() throws Exception {
		// Initialize the database
		subscriberRepository.saveAndFlush(subscriber);

        final SubjectContextKeyDTO dtoObject = new SubjectContextKeyDTO(
            subjectKeyExtendedConverter.convertToDTOKey(subscriber.getSubjectCtxId().getSubject()),
            contextConverter.convertToDTOKey(subscriber.getSubjectCtxId().getContext()));

		// Get the subscriber
		restSubscriberMockMvc
				.perform(
                    get("/api/subscribers/{subject_id}/{subject_type}/{subject_attribute}/{ctx_id}/{ctx_type}",
                        DatatypeConverter.printBase64Binary(
                            subscriber.getSubjectCtxId().getSubject().getKeyValue().getBytes(StandardCharsets.UTF_8)),
                        subscriber.getSubjectCtxId().getSubject().getKeyType().getId(),
                        subscriber.getSubjectCtxId().getSubject().getKeyAttribute(),
                        subscriber.getSubjectCtxId().getContext().getKeyId(),
                        subscriber.getSubjectCtxId().getContext().getKeyType().name()
                        ))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(
						jsonPath("$.subjectCtxId.subject.keyValue").value(
                            subscriber.getId().getSubject().getKeyValue()))
				.andExpect(
						jsonPath("$.subjectCtxId.subject.keyType").value(
								subscriber.getId().getSubject().getKeyType().getCode()))
                .andExpect(
                    jsonPath("$.subjectCtxId.subject.keyAttribute").value(
                        subscriber.getId().getSubject().getKeyAttribute()))
				.andExpect(
                    jsonPath("$.subjectCtxId.context.keyId").value(
                        subscriber.getId().getContext().getKeyId()
                            .intValue()))
				.andExpect(
						jsonPath("$.subjectCtxId.context.keyType").value(
								subscriber.getId().getContext().getKeyType().name()))
            .andExpect(
                jsonPath("$.subscribeType").value(
                    DEFAULT_SUBSCRIBE_TYPE.getName()));
	}

    @Test
    @Transactional
    public void getSubscribersOfCtx() throws Exception {
        // Initialize the database
        Subscriber s1 = ObjTest.newSubscriberGroup(DEFAULT_CTX);
        Subscriber s2 = ObjTest.newSubscriberPerson(DEFAULT_CTX);
        Subscriber s3 = ObjTest.newSubscriberPersonFromAttr(DEFAULT_CTX);
        List<Subscriber> subscribers = Lists.newArrayList(s1, s2, s3);
        subscriberRepository.saveAll(subscribers);
        subscriberRepository.flush();

        final SubscriberResolvedDTO subDTO1 = subscriberResolvedDTOFactory.from(s1);
        final SubscriberResolvedDTO subDTO2 = subscriberResolvedDTOFactory.from(s2);
        final SubscriberResolvedDTO subDTO3 = subscriberResolvedDTOFactory.from(s3);

        // Get the subscriber
        restSubscriberMockMvc
            .perform(
                get("/api/subscribers/{ctx_type}/{ctx_id}",
                    DEFAULT_CTX.getKeyType().name(),
                    DEFAULT_CTX.getKeyId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", Matchers.hasSize(subscribers.size())))
            .andExpect(jsonPath("$.[*].subjectKeyExtendedDTO.keyValue",
                Matchers.contains(subDTO3.getSubjectKeyExtendedDTO().getKeyValue())))
            .andExpect(jsonPath("$.[*].subjectKeyExtendedDTO.keyAttribute",
                Matchers.contains(subDTO3.getSubjectKeyExtendedDTO().getKeyAttribute())))
            .andExpect(jsonPath("$.[*].subjectKeyExtendedDTO.keyType",
                Matchers.contains(subDTO3.getSubjectKeyExtendedDTO().getKeyType().getCode())))
            .andExpect(jsonPath("$.[*].subjectDTO.modelId.keyId",
                Matchers.containsInAnyOrder(subDTO1.getSubjectDTO().getModelId().getKeyId(), subDTO2.getSubjectDTO().getModelId().getKeyId())))
            .andExpect(jsonPath("$.[*].subjectDTO.modelId.keyType",
                Matchers.containsInAnyOrder(subDTO1.getSubjectDTO().getModelId().getKeyType().getCode(), subDTO2.getSubjectDTO().getModelId().getKeyType().getCode())))
            .andExpect(jsonPath("$.[*].subscribeType",
                Matchers.containsInAnyOrder(subDTO1.getSubscribeType().getName(), subDTO2.getSubscribeType().getName(), subDTO3.getSubscribeType().getName())))
            .andExpect(jsonPath("$.[*].contextKeyDTO.keyId", Matchers.containsInAnyOrder(s1.getSubjectCtxId().getContext().getKeyId().intValue(),
                s2.getSubjectCtxId().getContext().getKeyId().intValue(), s3.getSubjectCtxId().getContext().getKeyId().intValue())))
            .andExpect(jsonPath("$.[*].contextKeyDTO.keyType", Matchers.containsInAnyOrder(s1.getSubjectCtxId().getContext().getKeyType().name(),
                s2.getSubjectCtxId().getContext().getKeyType().name(), s3.getSubjectCtxId().getContext().getKeyType().name())));
    }

    @Test
    @Transactional
	public void getNonExistingSubscriber() throws Exception {
        // Get the subscriber
        restSubscriberMockMvc
            .perform(
                get("/api/subscribers/{subject_id}/{subject_type}/{subject_attribute}/{ctx_id}/{ctx_type}",
                    DatatypeConverter.printBase64Binary(
                        subscriber.getSubjectCtxId().getSubject().getKeyValue().getBytes(StandardCharsets.UTF_8)),
                    subscriber.getSubjectCtxId().getSubject().getKeyType().getId(),
                    subscriber.getSubjectCtxId().getSubject().getKeyAttribute(),
                    subscriber.getSubjectCtxId().getContext().getKeyId(),
                    subscriber.getSubjectCtxId().getContext().getKeyType().name()))
            .andExpect(status().isNotFound());
	}

    @Test
    @Transactional
    public void deleteSubscriberOld() throws Exception {
        // Initialize the database
        subscriberRepository.saveAndFlush(subscriber);

        // Get the subscriber
        restSubscriberMockMvc
            .perform(
                delete("/api/subscribers/{subject_id}/{subject_type}/{subject_attribute}/{ctx_id}/{ctx_type}",
                    DatatypeConverter.printBase64Binary(
                        subscriber.getSubjectCtxId().getSubject().getKeyValue().getBytes(StandardCharsets.UTF_8)),
                    subscriber.getSubjectCtxId().getSubject().getKeyType().getId(),
                    subscriber.getSubjectCtxId().getSubject().getKeyAttribute(),
                    subscriber.getSubjectCtxId().getContext().getKeyId(),
                    subscriber.getSubjectCtxId().getContext().getKeyType().name()))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Subscriber> subscribers = subscriberRepository.findAll();
        assertThat(subscribers).hasSize(0);
    }

	@Test
	@Transactional
	public void deleteSubscriber() throws Exception {
		// Initialize the database
		subscriberRepository.saveAndFlush(subscriber);

        final SubjectContextKeyDTO dtoObject = new SubjectContextKeyDTO(
            subjectKeyExtendedConverter.convertToDTOKey(subscriber.getSubjectCtxId().getSubject()),
            contextConverter.convertToDTOKey(subscriber.getSubjectCtxId().getContext()));

		// Get the subscriber
        restSubscriberMockMvc
            .perform(
                delete("/api/subscribers/").contentType(
                    TestUtil.APPLICATION_JSON_UTF8).content(
                    TestUtil.convertObjectToJsonBytes(dtoObject)))
            .andExpect(status().isOk());

		// Validate the database is empty
		List<Subscriber> subscribers = subscriberRepository.findAll();
		assertThat(subscribers).hasSize(0);
	}
}
