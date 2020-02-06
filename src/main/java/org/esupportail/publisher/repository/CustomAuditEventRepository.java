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

import org.esupportail.publisher.config.audit.AuditEventConverter;
import org.esupportail.publisher.domain.PersistentAuditEvent;
import java.time.Instant;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.List;

/**
 * Wraps an implementation of Spring Boot's AuditEventRepository.
 */
@Repository
public class CustomAuditEventRepository {

    @Inject
    private PersistenceAuditEventRepository persistenceAuditEventRepository;

    @Bean
    public AuditEventRepository auditEventRepository() {
        return new AuditEventRepository() {

            private static final String AUTHORIZATION_FAILURE = "AUTHORIZATION_FAILURE";

            private static final String ANONYMOUS_USER = "anonymousUser";

            @Inject
            private AuditEventConverter auditEventConverter;


            @Override
            public List<AuditEvent> find(String principal, Instant after, String type) {
                Iterable<PersistentAuditEvent> persistentAuditEvents;
                if (principal == null && after == null) {
                    persistentAuditEvents = persistenceAuditEventRepository.findAll();
                } else if (after == null) {
                    persistentAuditEvents = persistenceAuditEventRepository.findByPrincipal(principal);
                } else {
                    persistentAuditEvents =
                            persistenceAuditEventRepository.findByPrincipalAndAuditEventDateAfter(principal, after);
                }
                return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
            }

            @Override
            @Transactional(propagation = Propagation.REQUIRES_NEW)
            public void add(AuditEvent event) {
                if(!AUTHORIZATION_FAILURE.equals(event.getType()) &&
                    !ANONYMOUS_USER.equals(event.getPrincipal().toString())) {
                    PersistentAuditEvent persistentAuditEvent = new PersistentAuditEvent();
                    persistentAuditEvent.setPrincipal(event.getPrincipal());
                    persistentAuditEvent.setAuditEventType(event.getType());
                    persistentAuditEvent.setAuditEventDate(event.getTimestamp());
                    persistentAuditEvent.setData(auditEventConverter.convertDataToStrings(event.getData()));

                    persistenceAuditEventRepository.save(persistentAuditEvent);
                }
            }
        };
    }
}
