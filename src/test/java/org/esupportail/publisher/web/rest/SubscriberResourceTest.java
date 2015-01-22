package org.esupportail.publisher.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.SubjectContextKey;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.enums.SubscribeType;
import org.esupportail.publisher.repository.SubscriberRepository;
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
 * Test class for the SubscriberResource REST controller.
 *
 * @see SubscriberResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@Slf4j
public class SubscriberResourceTest {

	private static final SubscribeType DEFAULT_SUBSCRIBE_TYPE = SubscribeType.FORCED;
	private static final SubscribeType UPDATED_SUBSCRIBE_TYPE = SubscribeType.FREE;

	//private static final SubjectKey DEFAULT_SUBJECT = new SubjectKey("admin", SubjectType.PERSON);
	private static final SubjectKey DEFAULT_SUBJECT = new SubjectKey(
            "grouper.coll:collectivite:region_centre_val_de_loire:applications:gestion_et_reservation_de_ressources:" +
                    "administrateurs_des_ressources:test_de_longueur_totale_superieur_a_deux_cent_cinquante_six_characters:" +
                    "et_pourquoi_pas_vu_le_nombre_de_noeuds_dans_l_arborescence_des_groupes", SubjectType.GROUP);

	private static final ContextKey DEFAULT_CTX = new ContextKey(1L, ContextType.ORGANIZATION);

	private static final SubjectContextKey DEFAULT_SUBSCRIBE_KEY = new SubjectContextKey(
			DEFAULT_SUBJECT, DEFAULT_CTX);

	@Inject
	private SubscriberRepository subscriberRepository;

	private MockMvc restSubscriberMockMvc;

	private Subscriber subscriber;

	@PostConstruct
	public void setup() {
		MockitoAnnotations.initMocks(this);
		SubscriberResource subscriberResource = new SubscriberResource();
		ReflectionTestUtils.setField(subscriberResource,
				"subscriberRepository", subscriberRepository);
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
                        subscriber.getSubjectCtxId().getSubject().getKeyId() + "/" +
                        subscriber.getSubjectCtxId().getSubject().getKeyType().getId() + "/" +
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
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(
						jsonPath("$.[0].subjectCtxId.subject.keyId").value(
								DEFAULT_SUBSCRIBE_KEY.getSubject().getKeyId()))
				.andExpect(
						jsonPath("$.[0].subjectCtxId.subject.keyType").value(
								DEFAULT_SUBSCRIBE_KEY.getSubject().getKeyType().getCode()))
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

		// Get the subscriber
		restSubscriberMockMvc
				.perform(
						get("/api/subscribers/{subject_id}/{subject_type}/{ctx_id}/{ctx_type}",
								subscriber.getSubjectCtxId().getSubject()
										.getKeyId(), subscriber
										.getSubjectCtxId().getSubject()
										.getKeyType().getId(), subscriber
										.getSubjectCtxId().getContext()
										.getKeyId(), subscriber
										.getSubjectCtxId().getContext()
										.getKeyType().name()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(
						jsonPath("$.subjectCtxId.subject.keyId").value(
								subscriber.getId().getSubject().getKeyId()))
				.andExpect(
						jsonPath("$.subjectCtxId.subject.keyType").value(
								subscriber.getId().getSubject().getKeyType().getCode()))
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
	public void getNonExistingSubscriber() throws Exception {
		// Get the subscriber
		restSubscriberMockMvc
				.perform(
						get("/api/subscribers/{subject_id}/{subject_type}/{ctx_id}/{ctx_type}",
								subscriber.getSubjectCtxId().getSubject()
										.getKeyId(), subscriber
										.getSubjectCtxId().getSubject()
										.getKeyType().getId(), subscriber
										.getSubjectCtxId().getContext()
										.getKeyId(), subscriber
										.getSubjectCtxId().getContext()
										.getKeyType().name())).andExpect(
						status().isNotFound());
	}

	/*@Test
	@Transactional
	public void updateSubscriber() throws Exception {
		// Initialize the database
		subscriberRepository.saveAndFlush(subscriber);

		// Update the subscriber
		subscriber.setSubscribeType(UPDATED_SUBSCRIBE_TYPE);
		restSubscriberMockMvc.perform(
				put("/api/subscribers").contentType(
						TestUtil.APPLICATION_JSON_UTF8).content(
						TestUtil.convertObjectToJsonBytes(subscriber)))
				.andExpect(status().isOk());

		// Validate the Subscriber in the database
		List<Subscriber> subscribers = subscriberRepository.findAll();
		assertThat(subscribers).hasSize(1);
		Subscriber testSubscriber = subscribers.iterator().next();
		assertThat(testSubscriber.getSubscribeType()).isEqualTo(
				UPDATED_SUBSCRIBE_TYPE);
		assertThat(testSubscriber.getSubjectCtxId()).isEqualTo(
				DEFAULT_SUBSCRIBE_KEY);
		;
	}*/

	@Test
	@Transactional
	public void deleteSubscriber() throws Exception {
		// Initialize the database
		subscriberRepository.saveAndFlush(subscriber);

		// Get the subscriber
		restSubscriberMockMvc
				.perform(
						delete("/api/subscribers/{subject_id}/{subject_type}/{ctx_id}/{ctx_type}",
								subscriber.getSubjectCtxId().getSubject()
										.getKeyId(), subscriber
										.getSubjectCtxId().getSubject()
										.getKeyType().getId(), subscriber
										.getSubjectCtxId().getContext()
										.getKeyId(), subscriber
										.getSubjectCtxId().getContext()
										.getKeyType().name())).andExpect(
						status().isOk());

		// Validate the database is empty
		List<Subscriber> subscribers = subscriberRepository.findAll();
		assertThat(subscribers).hasSize(0);
	}
}
