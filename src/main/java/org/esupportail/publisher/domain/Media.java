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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald 18 juin 2014
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonTypeName("MEDIA")
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Media extends AbstractItem implements Serializable {

	/** */
	private static final long serialVersionUID = -7122103813913396444L;

	/**
	 * Empty constructor of Media.
	 */
	public Media() {
		super();
	}

	/**
	 * Constructor of Media.
	 *
	 * @param title
	 * @param enclosure
	 * @param startDate
	 * @param endDate
	 * @param validatedDate
	 * @param validatedBy
	 * @param status
	 * @param summary
	 * @param organization
	 * @param redactor
	 */
	public Media(final String title, final String enclosure,
			final LocalDate startDate, final LocalDate endDate,
			final DateTime validatedDate, final User validatedBy,
			final ItemStatus status, final String summary,
			final Organization organization, final Redactor redactor) {
		super(title, enclosure, startDate, endDate, validatedDate, validatedBy,
				status, summary, organization, redactor);
	}

	// The media is set in enclosure.
	// TODO options to set the item link as a publisher page showing the media
	// or only direct link to the media ?

}
