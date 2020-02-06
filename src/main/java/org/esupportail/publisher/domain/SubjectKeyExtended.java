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

import java.io.Serializable;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.util.CstPropertiesLength;
import org.esupportail.publisher.domain.util.CustomEnumSerializer;
import org.esupportail.publisher.web.rest.dto.ICompositeExtendedKey;
import org.hibernate.validator.constraints.ScriptAssert;


/**
 * @author GIP RECIA - Julien Gribonvald 12 octobre 2017
 * TODO: when upgrading hibernate version into upper of 5.0.4 we will be able to make an extends on SubjectKey, until we have 2 separated class
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Embeddable
@ScriptAssert.List({
    @ScriptAssert(lang = "javascript", script = "org.esupportail.publisher.domain.SubjectKeyExtended.ValidateSubjectKeyType(_this.keyAttribute, _this.keyType)",
        message = "Not valid SubjectKeyExtended : the keyType should depend of the use of the keyAttribute"),
    @ScriptAssert(lang = "javascript", script = "org.esupportail.publisher.domain.SubjectKeyExtended.ValidateSubjectKeyREGEX(_this.keyValue, _this.keyType)",
    message = "Not valid SubjectKeyExtended : the Regex Pattern doesn't compile, check the syntax")
})
@Slf4j
public class SubjectKeyExtended implements ICompositeExtendedKey<String, String, SubjectType>, Serializable {

	/** Serial Version id. */
	private static final long serialVersionUID = 687644117634464074L;

	public static final String defaultAttributeName = "ID";

	/** This field corresponds to the database column subject_id. */
	@NonNull
	@NotNull
	@Basic
	@Column(length = CstPropertiesLength.SUBJECTID, name = "subject_attribute_value", nullable = false)
	private String keyValue;

	/** This field corresponds to the database column attribute_name. */
	@NonNull
	@NotNull
	@Basic
	@Column(name = "subject_attribute_name", length = 64, nullable = false)
	private String keyAttribute = defaultAttributeName;

	/** This field corresponds to the database column subject_type. */
	@NonNull
	@NotNull
	@Column(length = 25, name = "subject_type", nullable = false)
	//@Convert(converter = SubjectTypeConverter.class)
	@Enumerated(EnumType.STRING)
	@JsonSerialize(using = CustomEnumSerializer.class)
	//@JsonDeserialize(using = SubjectTypeDeserializer.class)
	private SubjectType keyType;

	/**
	 * @param subjectkey
	 */
	public SubjectKeyExtended(final SubjectKey subjectkey) {
		super();
		this.keyValue = subjectkey.getKeyId();
		this.keyType = subjectkey.getKeyType();
	}

	public static boolean ValidateSubjectKeyType(final String keyAttribute, final SubjectType keyType) {
		return keyAttribute.equals(defaultAttributeName)
				&& (keyType.getId() == SubjectType.GROUP.getId() || keyType.getId() == SubjectType.PERSON.getId())
                || keyType.getId() == SubjectType.PERSON_ATTR.getId()
				|| keyType.getId() == SubjectType.PERSON_ATTR_REGEX.getId();
	}

    public static boolean ValidateSubjectKeyREGEX(final String keyValue, final SubjectType keyType) {
        if (SubjectType.PERSON_ATTR_REGEX.getId() == keyType.getId()) {
            try {
                Pattern.compile(keyValue);
            } catch (PatternSyntaxException pse) {
                return false;
            }
        }
        return true;
    }

}
