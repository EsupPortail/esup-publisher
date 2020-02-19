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
package org.esupportail.publisher.web.rest.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.esupportail.publisher.domain.SubjectKeyExtended;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.util.CustomEnumSerializer;

import org.hibernate.validator.constraints.ScriptAssert;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 12 oct. 2017
 */
@Data
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@ScriptAssert.List({
    @ScriptAssert(lang = "javascript", script = "org.esupportail.publisher.domain.SubjectKeyExtended.ValidateSubjectKeyType(_this.keyAttribute, _this.keyType)",
        message = "Not valid SubjectKeyExtended : the keyType should depend of the use of the keyAttribute"),
    @ScriptAssert(lang = "javascript", script = "org.esupportail.publisher.domain.SubjectKeyExtended.ValidateSubjectKeyREGEX(_this.keyValue, _this.keyType)",
        message = "Not valid SubjectKeyExtended : the Regex Pattern doesn't compile, check the syntax")
})
public class SubjectKeyExtendedDTO implements ICompositeExtendedKey<String, String, SubjectType>, Serializable {

	@NotNull
	@NonNull
	private String keyValue;

	@NotNull
	@NonNull
	private String keyAttribute;

	@NotNull
	@NonNull
	@JsonSerialize(using = CustomEnumSerializer.class)
	private SubjectType keyType;

	/**
	 * @param keyValue
	 * @param keyType
	 */
	public SubjectKeyExtendedDTO(@NonNull final String keyValue, @NonNull final SubjectType keyType) {
		this.keyValue = keyValue;
		this.keyType = keyType;
		this.keyAttribute = SubjectKeyExtended.defaultAttributeName;
	}

}
