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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.esupportail.publisher.domain.util.CustomEnumSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author GIP RECIA - Julien Gribonvald 1 juil. 2014
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonTypeName("PERMONCTX")
@Entity
@DiscriminatorValue("OnContext")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PermissionOnContext extends AbstractPermission {

	/** */
	private static final long serialVersionUID = -8852407910405767L;

    /** The role. */
    @NotNull
    @Column(length = 50, nullable=false, name="perm")
    //@Convert(converter = PermissionTypeConverter.class)
    @Enumerated(EnumType.STRING)
    //@JsonDeserialize(using = PermissionTypeDeserializer.class)
    //@JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = CustomEnumSerializer.class)
    private PermissionType role;

	/**
	 * Constructor
	 */
	public PermissionOnContext() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param context
	 * @param abstractEvaluator
	 * @param role
	 */
	public PermissionOnContext(final ContextKey context, final PermissionType role,
			final AbstractEvaluator abstractEvaluator) {
		super(context, abstractEvaluator);
        this.role = role;

	}

}
