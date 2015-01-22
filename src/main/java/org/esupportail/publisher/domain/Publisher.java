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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.domain.util.CustomEnumSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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

	public Publisher() {
		super();
	}

	public Publisher(final Organization organization, final Reader reader,
			final Redactor redactor,
			final PermissionClass permissionType,
			final boolean used) {
		super();
		this.context = new OrganizationReaderRedactorKey(organization, reader,
				redactor);
		this.permissionType = permissionType;
		this.used = used;
	}

	@Override
	public ContextKey getContextKey() {
        if (getId() == null) return null;
        return new ContextKey(this.getId(), ContextType.PUBLISHER);
	}

    @Override
    public String getDisplayName() {
        return context.getReader().getDisplayName() + " - " + context.getRedactor().getDisplayName();
    }
}
