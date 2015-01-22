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
/**
 *
 */
package org.esupportail.publisher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald 20 juin 2014
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SubjectContextKey implements Serializable {

	/** */
	private static final long serialVersionUID = 9102031861913258172L;
	/** The Subject. */
	@Basic
	// @JsonSerialize(using = SubjectKeySerializer.class)
	// @JsonDeserialize(using = SubjectKeyDeserializer.class)
	private SubjectKey subject;
	/** The context. */
	@Basic
	// @JsonSerialize(using = ContextKeySerializer.class)
	// @JsonDeserialize(using = ContextKeyDeserializer.class)
	private ContextKey context;

}
