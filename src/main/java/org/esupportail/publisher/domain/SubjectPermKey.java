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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.util.CstPropertiesLength;
import org.esupportail.publisher.domain.util.CustomEnumSerializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Comparator;

/**
 * @author GIP RECIA - Julien Gribonvald 27 juin 2014
 */
@Data
@NoArgsConstructor
@ToString
@Embeddable
public class SubjectPermKey implements Serializable {

	/** */
	private static final long serialVersionUID = 2437073637197115754L;

	/*
	 * Can't work an embeddable with embedded key ! ERROR : Unique index or
	 * primary key violation: "PRIMARY_KEY_1 ON
	 * PUBLIC.PERMISSION_ON_SUBJECTS_ROLES_ON_SUBJECTS (PERMISSION_ON_SUBJECTS,
	 * NEED_VALIDATION) VALUES ( 1, '0', XXXXX , 1)"; SQL statement:" see on
	 * JIRA https://hibernate.atlassian.net/browse/HHH-1152
	 */
	/** This field corresponds to the database column subject_id. */
	@NonNull
	@NotNull
	@Basic
	@Column(length = CstPropertiesLength.SUBJECTID, name = "subject_id", nullable = false)
	private String subjectId;

	/** This field corresponds to the database column subject_type. */
	@NonNull
	@NotNull
	@Column(length = 25, name = "subject_type", nullable = false)
	//@Convert(converter = SubjectTypeConverter.class)
    @Enumerated(EnumType.STRING)
    //@JsonDeserialize(using = SubjectTypeDeserializer.class)
    @JsonSerialize(using = CustomEnumSerializer.class)
	private SubjectType subjectType;

	/** If the subject can publish directly without validation. */
	@Basic
	@Column(length = 1, nullable = false, name = "validation")
	private boolean needValidation;

	public SubjectPermKey(final SubjectKey subject, final boolean needValidation) {
		this.subjectId = subject.getKeyId();
		this.subjectType = subject.getKeyType();
		this.needValidation = needValidation;
	}

	public SubjectKey getSubjectKey() {
		return this.getSubjectKey();
	}

	public static class SubjectPermComparatorOnSubject implements
			Comparator<SubjectPermKey> {

		@Override
		public int compare(SubjectPermKey o1, SubjectPermKey o2) {
			int typeCompare = o1.getSubjectType()
					.compareTo(o2.getSubjectType());
			if (typeCompare != 0) {
				return typeCompare;
			}
			return o1.getSubjectId().compareTo(o2.getSubjectId());

		}

		// @Override
		// public int compare(SubjectPermKey o1, SubjectPermKey o2) {
		// int typeCompare = o1.subjectKey.getKeyType().compareTo(
		// o2.subjectKey.getKeyType());
		// if (typeCompare != 0) {
		// return typeCompare;
		// }
		// return o1.subjectKey.getKeyId().compareTo(o2.subjectKey.getKeyId());
		//
		// }

	}

	public static final SubjectPermComparatorOnSubject SUBJECTPERM_ONSUBJECT_COMPARATOR = new SubjectPermComparatorOnSubject();

}
