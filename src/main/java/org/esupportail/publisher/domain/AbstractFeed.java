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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;

@Getter
@Setter
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, exclude = "parent")
public abstract class AbstractFeed extends AbstractClassification implements
    Serializable {

    /** */
    private static final long serialVersionUID = -4460812621868603330L;

    /** This field corresponds to the database column parent_id. */
    @NotNull
    @NonNull
    @ManyToOne
    @JoinColumn(name = "parent_id")
    protected Category parent;

    public AbstractFeed() {
        super();
    }

    public AbstractFeed(final boolean rssAllowed, final String name,
                        final String iconUrl, final String lang, final int ttl,
                        final int displayOrder, final AccessType accessView,
                        final String description,
                        final DisplayOrderType defaultDisplayOrder, final String color,
                        final Publisher publisher, final Category parent) {
        super(rssAllowed, name, iconUrl, lang, ttl, displayOrder, accessView,
            description, defaultDisplayOrder, color, publisher);
        this.parent = parent;
    }

}
