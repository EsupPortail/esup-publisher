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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.util.CstPropertiesLength;
import org.esupportail.publisher.domain.util.CustomEnumSerializer;
import org.esupportail.publisher.web.rest.dto.ICompositeKey;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald 14 juin 2014
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Embeddable
public class SubjectKey implements ICompositeKey<String, SubjectType>,Serializable {

	/** Serial Version id. */
	private static final long serialVersionUID = 687644117634464074L;

	/** This field corresponds to the database column subject_id. */
	@NonNull
	@NotNull
	@Basic
	@Column(length = CstPropertiesLength.SUBJECTID, name = "subject_id", nullable = false)
	private String keyId;

	/** This field corresponds to the database column subject_type. */
	@NonNull
	@NotNull
	@Column(length = 25, name = "subject_type", nullable = false)
	//@Convert(converter = SubjectTypeConverter.class)
    @Enumerated(EnumType.STRING)
    @JsonSerialize(using = CustomEnumSerializer.class)
    //@JsonDeserialize(using = SubjectTypeDeserializer.class)
	private SubjectType keyType;

}
