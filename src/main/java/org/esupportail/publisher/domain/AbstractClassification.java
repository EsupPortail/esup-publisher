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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.util.CstPropertiesLength;
import org.esupportail.publisher.domain.util.CustomEnumSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald 16 juin 2014
 */
@Data
@EqualsAndHashCode(callSuper = false, of = { "name", "publisher" })
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Category.class, name = "CATEGORY"),
    @JsonSubTypes.Type(value = InternalFeed.class, name = "INTERNALFEED"),
    @JsonSubTypes.Type(value = ExternalFeed.class, name = "EXTERNALFEED")
})
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "T_CLASSIFICATION", uniqueConstraints = @UniqueConstraint(columnNames = {
		"name", "publisher_id", "type" }))
@DiscriminatorColumn(name = "type")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public abstract class AbstractClassification extends AbstractAuditingEntity
		implements IContext, Serializable {

	/** */
	private static final long serialVersionUID = -131894558189162059L;

	/** This field corresponds to the database column rss_allowed. */
	@Column(length = 1, nullable = false, name = "rss_allowed")
	private boolean rssAllowed = true;
	/** This field corresponds to the database column name. */
	@NotNull
	@Size(min = 3, max = CstPropertiesLength.CLASSIF_NAME)
	@Column(length = CstPropertiesLength.CLASSIF_NAME, nullable = false)
	private String name;
	/** This field corresponds to the database column icon. */
	@Size(max = CstPropertiesLength.URL)
	@Column(name = "icon_url", length = CstPropertiesLength.URL)
	private String iconUrl;
	/** This field corresponds to the database column lang. */
	@Size(min = 2, max = CstPropertiesLength.LANG)
	@Column(length = CstPropertiesLength.LANG)
	private String lang = "fr";
	@Min(value = 900)
	@Max(value = 86400)
	private int ttl = 3600;
	/** This field corresponds to the database column display_order. */
	@Max(999)
	@Column(nullable = false, length = CstPropertiesLength.ORDER, name = "display_order")
	private int displayOrder = 0;
	/** This field corresponds to the database column public_view. */
	@NotNull
	@Column(length = 50, nullable = false, name = "access_view")
	//@Convert(converter = AccessTypeConverter.class)
    @Enumerated(EnumType.STRING)
    //@JsonDeserialize(using = AccessTypeDeserializer.class)
    @JsonSerialize(using = CustomEnumSerializer.class)
	private AccessType accessView = AccessType.PUBLIC;
	/** This field corresponds to the database column description. */
	@NotNull
	@Size(min = 5, max = CstPropertiesLength.DESCRIPTION)
	@Column(nullable = false, length = CstPropertiesLength.DESCRIPTION)
	private String description;
	/** This field corresponds to the database column default_display_order. */
	@NotNull
	@Column(nullable = false, length = 50, name = "default_display_order")
	//@Convert(converter = DisplayOrderTypeConverter.class)
    @Enumerated(EnumType.STRING)
    @JsonSerialize(using = CustomEnumSerializer.class)
    //@JsonDeserialize(using = DisplayOrderTypeDeserializer.class)
	private DisplayOrderType defaultDisplayOrder = DisplayOrderType.START_DATE;
    /** This field corresponds to the database column color. */
    @Size(max = CstPropertiesLength.COLOR)
	@Column(length = CstPropertiesLength.COLOR)
    private String color;

	// @JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER, cascade = {})
	@JoinColumn(name = "publisher_id", nullable = false)
	private Publisher publisher;

	public AbstractClassification() {
		super();
	}

	/**
	 * Constructor of Classification
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
	 */
	public AbstractClassification(final boolean rssAllowed, final String name,
			final String iconUrl, final String lang, final int ttl,
			final int displayOrder, final AccessType accessView,
			final String description,
			final DisplayOrderType defaultDisplayOrder,
			final Publisher publisher) {
		super();
		this.rssAllowed = rssAllowed;
		this.name = name;
		this.iconUrl = iconUrl;
		this.lang = lang;
		this.ttl = ttl;
		this.displayOrder = displayOrder;
		this.accessView = accessView;
		this.description = description;
		this.defaultDisplayOrder = defaultDisplayOrder;
		this.publisher = publisher;
	}

    @Override
    public ContextKey getContextKey() {
        if (getId() == null) return null;
        if (this instanceof Category) {
            return new ContextKey(getId(), ContextType.CATEGORY);
        } else return new ContextKey(getId(), ContextType.FEED);
    }

    @Override
    public String getDisplayName() {
        return this.name;
    }
}
