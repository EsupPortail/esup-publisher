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
package org.esupportail.publisher.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.config.audit.AuditEventConverter;
import org.esupportail.publisher.domain.PersistentAuditEvent;
import org.esupportail.publisher.repository.PersistenceAuditEventRepository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

//@ExtendWith(SpringExtension.class)//@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
//@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@WebAppConfiguration
public class AuditEventServiceTest {

	@Mock
	private PersistenceAuditEventRepository persistenceAuditEventRepository;

	@Mock
	private AuditEventConverter auditEventConverter;

	@InjectMocks
	private AuditEventService auditEventService;

    @Autowired
    private PersistenceAuditEventRepository persistenceAuditEventRepositoryTransactional;

    @Autowired
    private AuditEventService auditEventServiceTransactional;

    private PersistentAuditEvent auditEventOld;

    private PersistentAuditEvent auditEventWithinRetention;

    private PersistentAuditEvent auditEventNew;

    private final int eventRetentionDays = 30;

	@PostConstruct
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@BeforeAll
    public void init() {
        auditEventOld = new PersistentAuditEvent();
        auditEventOld.setAuditEventDate(Instant.now().minus(eventRetentionDays + 1, ChronoUnit.DAYS));
        auditEventOld.setPrincipal("test-user-old");
        auditEventOld.setAuditEventType("test-type");

        auditEventWithinRetention = new PersistentAuditEvent();
        auditEventWithinRetention.setAuditEventDate(Instant.now().minus(eventRetentionDays - 1, ChronoUnit.DAYS));
        auditEventWithinRetention.setPrincipal("test-user-retention");
        auditEventWithinRetention.setAuditEventType("test-type");

        auditEventNew = new PersistentAuditEvent();
        auditEventNew.setAuditEventDate(Instant.now());
        auditEventNew.setPrincipal("test-user-new");
        auditEventNew.setAuditEventType("test-type");
    }

	@Test
	public void findAll_ShouldBeReturnAllAuditEvent() {
		//GIVEN
		List<PersistentAuditEvent> persistentAuditList = new ArrayList<>();
		List<AuditEvent> auditEventList = new ArrayList<>();

		Map<String, Object> dataAuditEvent = new HashMap<>();
		dataAuditEvent.put("remoteAddress", "0:0:0:0:0:0:0:1");
		dataAuditEvent.put("sessionId", "2CE9E14FA24CEEB2BED4AF60D13B342B");

		Map<String, Object> dataAuditEvent1 = new HashMap<>();
		dataAuditEvent1.put("remoteAddress", "0:0:0:0:0:0:0:1");
		dataAuditEvent1.put("sessionId", "08200478A2A327F89204B6CD3BF2CC7C");

		Map<String, Object> dataAuditEvent2 = new HashMap<>();
		dataAuditEvent2.put("remoteAddress", "0:0:0:0:0:0:0:1");
		dataAuditEvent2.put("sessionId", "9DEF13CA85D3836A4980BBF1C48C3F0E");

		AuditEvent auditEvent = new AuditEvent(Instant.now(), "F1800nmj",
				"AUTHENTICATION_SUCCESS",dataAuditEvent);
		AuditEvent auditEvent1 = new AuditEvent(Instant.now(), "F1800nmj",
				"AUTHENTICATION_SUCCESS",dataAuditEvent1);
		AuditEvent auditEvent2 = new AuditEvent(Instant.now(), "F1800nmj",
				"AUTHENTICATION_SUCCESS",dataAuditEvent2);
		auditEventList.add(auditEvent);
		auditEventList.add(auditEvent1);
		auditEventList.add(auditEvent2);

		//GIVEN SERVICE
		when(persistenceAuditEventRepository.findAll()).thenReturn(persistentAuditList);
		when(auditEventConverter.convertToAuditEvent(persistentAuditList)).thenReturn(auditEventList);

		//WHEN
		final List<AuditEvent> resultList = auditEventService.findAll();

		//THEN
		verify(persistenceAuditEventRepository).findAll();
		verify(auditEventConverter).convertToAuditEvent(persistentAuditList);
		assertThat(resultList.size(), equalTo(3));
	}

	@Test
	public void findByDates_ShoudBeReturn2AuditEvent() {
		//GIVEN
		List<PersistentAuditEvent> persistentAuditList = new ArrayList<>();
		Instant fromDate = LocalDateTime.of(2018, 3, 12, 20, 30, 0).toInstant(ZoneOffset.UTC);
        Instant toDate = LocalDateTime.of(2018, 10, 30, 10, 12, 0).toInstant(ZoneOffset.UTC);

		List<AuditEvent> auditEventList = new ArrayList<>();

		Map<String, Object> dataAuditEvent = new HashMap<>();
		dataAuditEvent.put("remoteAddress", "0:0:0:0:0:0:0:1");
		dataAuditEvent.put("sessionId", "2CE9E14FA24CEEB2BED4AF60D13B342B");

		Map<String, Object> dataAuditEvent1 = new HashMap<>();
		dataAuditEvent1.put("remoteAddress", "0:0:0:0:0:0:0:1");
		dataAuditEvent1.put("sessionId", "08200478A2A327F89204B6CD3BF2CC7C");


		AuditEvent auditEvent = new AuditEvent(LocalDateTime.of(2018, 4, 30, 10, 0, 0)
            .toInstant(ZoneOffset.UTC), "F1800nmj","AUTHENTICATION_SUCCESS",dataAuditEvent);
		AuditEvent auditEvent1 = new AuditEvent(LocalDateTime.of(2018, 6, 2, 14, 50, 0)
            .toInstant(ZoneOffset.UTC), "F1800nmj","AUTHENTICATION_SUCCESS",dataAuditEvent1);
		auditEventList.add(auditEvent);
		auditEventList.add(auditEvent1);

		//GIVEN SERVICE
		when(persistenceAuditEventRepository.findAllByAuditEventDateBetween(fromDate, toDate)).thenReturn(persistentAuditList);
		when(auditEventConverter.convertToAuditEvent(persistentAuditList)).thenReturn(auditEventList);

		//WHEN
		final List<AuditEvent> resultList = auditEventService.findByDates(fromDate, toDate);

		//THEN
		verify(persistenceAuditEventRepository).findAllByAuditEventDateBetween(fromDate, toDate);
		verify(auditEventConverter).convertToAuditEvent(persistentAuditList);
		assertThat(resultList.size(), equalTo(2));
	}

    @Test
    @Transactional
    public void verifyOldAuditEventsAreDeleted() {
        persistenceAuditEventRepositoryTransactional.deleteAll();
        persistenceAuditEventRepositoryTransactional.save(auditEventOld);
        persistenceAuditEventRepositoryTransactional.save(auditEventWithinRetention);
        persistenceAuditEventRepositoryTransactional.save(auditEventNew);

        persistenceAuditEventRepositoryTransactional.flush();

        assertThat(persistenceAuditEventRepositoryTransactional.findAll().size(), equalTo(3));

        auditEventServiceTransactional.removeOldAuditEvents();

        persistenceAuditEventRepositoryTransactional.flush();

        assertThat(persistenceAuditEventRepositoryTransactional.findAll().size(), equalTo(2));
        assertThat(persistenceAuditEventRepositoryTransactional.findByPrincipal("test-user-old"), is(empty()));
        assertThat(persistenceAuditEventRepositoryTransactional.findByPrincipal("test-user-retention"), is(not(empty())));
        assertThat(persistenceAuditEventRepositoryTransactional.findByPrincipal("test-user-new"), is(not(empty())));
    }

}