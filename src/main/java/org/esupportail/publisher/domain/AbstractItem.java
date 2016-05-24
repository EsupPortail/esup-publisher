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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.util.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.ScriptAssert;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald 24 Juin 2014
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = News.class, name = "NEWS"),
    @JsonSubTypes.Type(value = Media.class, name = "MEDIA"),
    @JsonSubTypes.Type(value = Resource.class, name = "RESOURCE"),
    @JsonSubTypes.Type(value = Flash.class, name = "FLASH")
})
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "T_ITEM")
@DiscriminatorColumn(name = "type")
@ScriptAssert(lang = "javascript", script = "_this.startDate.isBefore(_this.endDate)")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public abstract class AbstractItem extends AbstractAuditingEntity implements
    IContext, Serializable {

	/** */
	private static final long serialVersionUID = -5070583652062682913L;

	/** This field corresponds to the database column title. */
	@NotNull
	@Size(max = CstPropertiesLength.ITEM_TITLE)
	@Column(nullable = false, length = CstPropertiesLength.ITEM_TITLE)
	private String title;

	/** This field corresponds to the database column enclosure. */
	@Size(max = CstPropertiesLength.URL)
	@Column(length = CstPropertiesLength.URL)
	private String enclosure;

	@NotNull
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;

	@NotNull
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@ManyToOne
	@JoinColumn(name = "validated_by")
	private User validatedBy;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	@Column(name = "validated_date")
	private DateTime validatedDate;

	/** This field corresponds to the database column status. */
	@NotNull
	@Column(length = 50, nullable = false)
	//@Convert(converter = ItemStatusConverter.class)
    @Enumerated(EnumType.STRING)
    //@JsonFormat(shape = JsonFormat.Shape.STRING)
    //@JsonDeserialize(using = ItemStatusDeserializer.class)
    @JsonSerialize(using = CustomEnumSerializer.class)
	private ItemStatus status;

	/** This field corresponds to the database column summary. */
	@NotNull
	@Size(min = 5, max = CstPropertiesLength.SUMMARY)
	@Column(nullable = false, length = CstPropertiesLength.SUMMARY)
	private String summary;

    /** This field corresponds to the database column rss_allowed. */
    @Column(length = 1, nullable = false, name = "rss_allowed")
    private boolean rssAllowed = false;

	/** This field corresponds to the database column entity_id. */
	@NotNull
	@ManyToOne
	@JoinColumn(name = "entity_id", nullable = false)
	private Organization organization;
	/** This field corresponds to the database column publisher_id. */
	@NotNull
	@ManyToOne
	@JoinColumn(name = "redactor_id", nullable = false)
	private Redactor redactor;

	/**
	 * Empty constructor of the object Item.java.
	 */
	public AbstractItem() {
		super();
	}

	/**
	 * Constructor of the object Item.java.
	 *
	 * @param title
	 * @param enclosure
	 * @param startDate
	 * @param endDate
	 * @param validatedDate
	 * @param validatedBy
	 * @param status
	 * @param summary
     * @param rssAllowed
	 * @param organization
	 * @param redactor
	 */
	public AbstractItem(final String title, final String enclosure,
			final LocalDate startDate, final LocalDate endDate,
			final DateTime validatedDate, final User validatedBy,
			final ItemStatus status, final String summary, final boolean rssAllowed,
			final Organization organization, final Redactor redactor) {
		super();
		this.title = title;
		this.enclosure = enclosure;
		this.startDate = startDate;
		this.endDate = endDate;
		this.validatedDate = validatedDate;
		this.validatedBy = validatedBy;
		this.status = status;
		this.summary = summary;
        this.rssAllowed = rssAllowed;
		this.organization = organization;
		this.redactor = redactor;
	}

	public ItemStatus getStatus() {
		switch (status) {
		case PUBLISHED:
			if (this.startDate.isAfter(LocalDate.now())) {
				status = ItemStatus.SCHEDULED;
				return ItemStatus.SCHEDULED;
			} else if (this.endDate.isBefore(LocalDate.now())) {
				status = ItemStatus.ARCHIVED;
				return ItemStatus.ARCHIVED;
			}
			break;
		default:
			break;
		}
		return status;
	}

	@Override
	public ContextKey getContextKey() {
        if (getId() == null) return null;
		return new ContextKey(this.getId(), ContextType.ITEM);
	}

    @Override
    public String getDisplayName() {
        return this.title;
    }
}
