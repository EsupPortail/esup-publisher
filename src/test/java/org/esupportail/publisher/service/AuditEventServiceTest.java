package org.esupportail.publisher.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.config.audit.AuditEventConverter;
import org.esupportail.publisher.domain.PersistentAuditEvent;
import org.esupportail.publisher.repository.PersistenceAuditEventRepository;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(PowerMockRunner.class)
@SpringBootTest(classes = Application.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@WebAppConfiguration
public class AuditEventServiceTest {
	
	@Mock
	private PersistenceAuditEventRepository persistenceAuditEventRepository;
	
	@Mock
	private AuditEventConverter auditEventConverter;
	
	@InjectMocks
	private AuditEventService auditEventService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
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
		assertThat(resultList.size()).isEqualTo(3);
	}

	@Test
	public void findByDates_ShoudBeReturn2AuditEvent() {
		//GIVEN
		List<PersistentAuditEvent> persistentAuditList = new ArrayList<>();
		LocalDateTime fromDate = new LocalDateTime(2018, 03, 12, 20, 30);
		LocalDateTime toDate = new LocalDateTime(2018, 10, 30, 10, 12);
		
		List<AuditEvent> auditEventList = new ArrayList<>();
		
		Map<String, Object> dataAuditEvent = new HashMap<>();
		dataAuditEvent.put("remoteAddress", "0:0:0:0:0:0:0:1");
		dataAuditEvent.put("sessionId", "2CE9E14FA24CEEB2BED4AF60D13B342B");
		
		Map<String, Object> dataAuditEvent1 = new HashMap<>();
		dataAuditEvent1.put("remoteAddress", "0:0:0:0:0:0:0:1");
		dataAuditEvent1.put("sessionId", "08200478A2A327F89204B6CD3BF2CC7C");

		
		AuditEvent auditEvent = new AuditEvent(new LocalDateTime(2018, 04, 30, 10, 00).toDate().toInstant(), "F1800nmj",
				"AUTHENTICATION_SUCCESS",dataAuditEvent);
		AuditEvent auditEvent1 = new AuditEvent(new LocalDateTime(2018, 06, 02, 14, 50).toDate().toInstant(), "F1800nmj",
				"AUTHENTICATION_SUCCESS",dataAuditEvent1);
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
		assertThat(resultList.size()).isEqualTo(2);
	}

}
