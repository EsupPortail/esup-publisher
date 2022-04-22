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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.util.CstPropertiesLength;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * This object define a parameter's context for an organization/department
 * etc... The Organization is named like that because Entity if protected by jpa
 * annotation.
 *
 * @author GIP RECIA - Julien Gribonvald 14 Juin 2014
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, of = "name")
@ToString(callSuper = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "T_ENTITY", uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Organization extends AbstractAuditingEntity implements IContext, Serializable {

	/** Serial ID. */
	private static final long serialVersionUID = 2669917994587940798L;

	/** This field corresponds to the database column name. */
	@NotNull
	@Size(min = 5, max = CstPropertiesLength.ORG_NAME)
	@Column(nullable = false, length = CstPropertiesLength.ORG_NAME)
	private String name;

	/** This field corresponds to the database column display_name. */
	@Size(max = CstPropertiesLength.ORG_DISPLAYNAME)
	@Column(length = CstPropertiesLength.ORG_DISPLAYNAME, name = "display_name")
	private String displayName;

	/** This field corresponds to the database column description. */
	@NotNull
	@Size(min = 5, max = CstPropertiesLength.DESCRIPTION)
	@Column(nullable = false, length = CstPropertiesLength.DESCRIPTION)
	private String description;

	/** This field corresponds to the database column display_order. */
	@NotNull
	@Max(999)
	@Column(length = CstPropertiesLength.ORDER, name = "display_order", nullable = false)
	private int displayOrder = 0;

	@Column(length = 1, nullable = false, name = "allow_notifications")
	private boolean allowNotifications;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "T_ENTITY_IDENTIFIERS", uniqueConstraints = @UniqueConstraint(columnNames = "identifiers"))
    private Set<String> identifiers = new HashSet<>();

	@OneToMany(mappedBy = "context.organization", fetch = FetchType.LAZY)
	@JsonIgnore
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@ToString.Exclude
	private Set<Publisher> availablePublisherContexts = new HashSet<>();

	public Organization() {
		super();
	}

	public Organization(final String name, final String displayName,
			final String description, final Integer displayOrder, final Set<String> identifiers) {
		super();
		this.name = name;
        this.displayName = displayName;
        if (displayName == null || displayName.isEmpty()) {
            this.displayName = name;
        }
		this.displayOrder = displayOrder;
		this.description = description;
        this.identifiers = identifiers;
	}

	@Override
	public ContextKey getContextKey() {
        if (getId() == null) return null;
		return new ContextKey(this.getId(), ContextType.ORGANIZATION);
	}
}