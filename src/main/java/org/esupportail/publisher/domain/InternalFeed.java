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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald 18 juin 2014
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonTypeName("INTERNALFEED")
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class InternalFeed extends AbstractFeed implements Serializable {

	/** */
	private static final long serialVersionUID = -546691544337564686L;

	/**
	 * Empty Constructor of InternalFeed
	 */
	public InternalFeed() {
		super();
	}

	/**
	 * Constructor of InternalFeed.
	 *
	 * @param rssAllowed
	 * @param name
	 * @param iconUrl
	 * @param lang
	 * @param ttl
	 * @param displayOrder
	 * @param accessView
	 * @param description
	 * @param defaultDisplayOrder
	 * @param publisher
	 * @param parent
	 */
	public InternalFeed(final boolean rssAllowed, final String name,
			final String iconUrl, final String lang, final int ttl,
			final int displayOrder, final AccessType accessView,
			final String description,
			final DisplayOrderType defaultDisplayOrder,
			final Publisher publisher, final Category parent) {
		super(rssAllowed, name, iconUrl, lang, ttl, displayOrder, accessView,
				description, defaultDisplayOrder, publisher, parent);
	}

}
