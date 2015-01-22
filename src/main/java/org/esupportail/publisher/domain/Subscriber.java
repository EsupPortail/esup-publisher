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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.SubscribeType;
import org.esupportail.publisher.domain.util.CustomEnumSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald 18 Juin 2014
 */
@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "T_SUBSCRIBER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Subscriber extends AbstractEntity<SubjectContextKey> implements IEntity<SubjectContextKey>, Serializable {

	/** Serial ID. */
	private static final long serialVersionUID = 4132614458906338947L;

	/** Composite key of the subject and context. */
	@EmbeddedId
	private SubjectContextKey subjectCtxId;

	/** This field corresponds to the database column news_subscribers.sub_type. */
	@NotNull
	@Column(length = 25, nullable = false, name = "subscribe_type")
	//@Convert(converter = SubscribeTypeConverter.class)
    @Enumerated(EnumType.STRING)
    //@JsonDeserialize(using = SubscribeTypeDeserializer.class)
    @JsonSerialize(using = CustomEnumSerializer.class)
	private SubscribeType subscribeType;

	public Subscriber() {
		super();
	}

	public Subscriber(@NotNull final SubjectKey subject,
			@NotNull final ContextKey context,
			@NotNull final SubscribeType subscribeType) {
		super();
		this.subjectCtxId = new SubjectContextKey(subject, context);
		this.subscribeType = subscribeType;
	}

	@JsonIgnore
	@Override
	public SubjectContextKey getId() {
		return this.getSubjectCtxId();
	}

}
