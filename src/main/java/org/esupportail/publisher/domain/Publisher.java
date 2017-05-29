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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.domain.util.CstPropertiesLength;
import org.esupportail.publisher.domain.util.CustomEnumSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false, of = {"context"})
@Entity
@Table(name = "T_PUBLISHER", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"entity_id", "reader_id", "redactor_id" }) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Publisher extends AbstractAuditingEntity implements IContext,
		Serializable {

	/** */
	private static final long serialVersionUID = 8112297117990645225L;

	@Embedded
	private OrganizationReaderRedactorKey context;

    /** This field corresponds to the database column displayName. */
    @NotNull
    @NonNull
    @Size(min = 3, max = CstPropertiesLength.DISPLAYNAME)
    @Column(name = "display_name", nullable = false, length = CstPropertiesLength.DISPLAYNAME)
    private String displayName;

	/**
	 * A boolean parameter to tell if the organization use this publication
	 * context.
	 */
	@Column(length = 1, nullable = false)
	private boolean used;

	/** This field corresponds to the database column display_order. */
	@Max(9)
	@Column(length = 2, name = "display_order", nullable = false)
	private int displayOrder = 0;

    @NotNull
    @Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "permission_class", length=50)
    //@JsonDeserialize(using = PermissionClassDeserializer.class)
    @JsonSerialize(using = CustomEnumSerializer.class)
	private PermissionClass permissionType;

	/** This field corresponds to the database column default_display_order. */
	@NotNull
	@Column(length = 50, name = "default_display_order", nullable = false)
    @Enumerated(EnumType.STRING)
	//@Convert(converter = DisplayOrderTypeConverter.class)
    //@JsonDeserialize(using = DisplayOrderTypeDeserializer.class)
    @JsonSerialize(using = CustomEnumSerializer.class)
	private DisplayOrderType defaultDisplayOrder = DisplayOrderType.START_DATE;

    /** This field corresponds to the database column has_sub_perms_management. */
    @Column(length = 1, nullable = false, name = "has_sub_perms_management")
    private boolean hasSubPermsManagement = true;

    /** This field corresponds to the database column has_sub_perms_management. */
    @Column(length = 1, nullable = false, name = "do_highlight")
    private boolean doHighlight = true;

	public Publisher() {
		super();
	}

	public Publisher(final Organization organization, final Reader reader,
			final Redactor redactor, final String displayName,
			final PermissionClass permissionType,
			final boolean used, final boolean hasSubPermsManagement,
                     final boolean doHighlight) {
		super();
		this.context = new OrganizationReaderRedactorKey(organization, reader,
				redactor);
        this.displayName = displayName;
		this.permissionType = permissionType;
		this.used = used;
        this.hasSubPermsManagement = hasSubPermsManagement;
        this.doHighlight = doHighlight;
	}

	@Override
	public ContextKey getContextKey() {
        if (getId() == null) return null;
        return new ContextKey(this.getId(), ContextType.PUBLISHER);
	}
}
