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
import org.esupportail.publisher.domain.util.CstPropertiesLength;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald 18 juin 2014
 */
@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonTypeName("EXTERNALFEED")
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ExternalFeed extends AbstractFeed implements Serializable {

	/** */
	private static final long serialVersionUID = -8867840101660141365L;

	@NotNull
	@Size(max = CstPropertiesLength.URL)
	@URL
	@Column(name = "rss_url")
	// @Column(nullable=false)
	private String rssUrl;

	public ExternalFeed() {
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
	public ExternalFeed(final boolean rssAllowed, final String name,
			final String iconUrl, final String lang, final int ttl,
			final int displayOrder, final AccessType accessView,
			final String description,
			final DisplayOrderType defaultDisplayOrder,
			final Publisher publisher, final Category parent,
			final String rssUrl) {
		super(rssAllowed, name, iconUrl, lang, ttl, displayOrder, accessView,
				description, defaultDisplayOrder, publisher, parent);
		this.rssUrl = rssUrl;
	}
}
