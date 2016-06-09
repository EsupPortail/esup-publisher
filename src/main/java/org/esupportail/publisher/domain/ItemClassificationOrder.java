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

import com.mysema.query.annotations.QueryInit;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esupportail.publisher.domain.util.CstPropertiesLength;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald 25 juin 2014
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "T_CLASSIFICATION_ITEM")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ItemClassificationOrder implements Serializable,
		IEntity<ItemClassificationKey> {
	/** */
	private static final long serialVersionUID = 1987929502768792376L;

	@NotNull
    @QueryInit({"abstractClassification.*", "abstractItem.*"})
	@EmbeddedId
	private ItemClassificationKey itemClassificationId;

	@Max(999)
	@Column(name = "display_order", nullable = false, length = CstPropertiesLength.ORDER)
	private int displayOrder = 0;

	public ItemClassificationOrder(@NotNull final AbstractItem abstractItem,
			@NotNull final AbstractClassification abstractClassification,
			@NotNull final int displayOrder) {
		this.itemClassificationId = new ItemClassificationKey(abstractClassification,
				abstractItem);
		this.displayOrder = displayOrder;
	}

	@Override
	public ItemClassificationKey getId() {
		return this.getItemClassificationId();
	}

}
