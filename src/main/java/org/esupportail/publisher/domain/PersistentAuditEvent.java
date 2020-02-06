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
package org.esupportail.publisher.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Persist AuditEvent managed by the Spring Boot actuator
 * @see org.springframework.boot.actuate.audit.AuditEvent
 */

@Entity
@Table(name = "T_PERSISTENT_AUDIT_EVENT")
public class PersistentAuditEvent {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_id")
	private Long id;

	@NotNull
	@Column(nullable = false)
	private String principal;

	@Column(name = "event_date")
	private Instant auditEventDate;

	@Column(name = "event_type")
	private String auditEventType;

	@ElementCollection
	@MapKeyColumn(name = "name")
	@Column(name = "value")
	@CollectionTable(name = "T_PERSISTENT_AUDIT_EVENT_DATA", joinColumns = @JoinColumn(name = "event_id"))
	private Map<String, String> data = new HashMap<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public Instant getAuditEventDate() {
		return auditEventDate;
	}

	public void setAuditEventDate(Instant auditEventDate) {
		this.auditEventDate = auditEventDate;
	}

	public String getAuditEventType() {
		return auditEventType;
	}

	public void setAuditEventType(String auditEventType) {
		this.auditEventType = auditEventType;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}
}
