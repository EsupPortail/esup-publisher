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
package org.esupportail.publisher.web.rest.dto;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.domain.util.CstPropertiesLength;

@NoArgsConstructor
@ToString(callSuper=true)
public class PublisherDTO extends AbstractIdDTO<Long> implements IAbstractDTO<Long>, Serializable {

    @Getter
    @Setter
    @NotNull
    private OrganizationDTO organization;
    @Getter
    @Setter
    @NotNull
    private ReaderDTO reader;
    @Getter
    @Setter
    @NotNull
    private RedactorDTO redactor;

    @Getter
    @Setter
    @NotNull
    @Size(min=3, max = CstPropertiesLength.DISPLAYNAME)
    private String displayName;

    /**
     * A boolean parameter to tell if the organization use this publication
     * context.
     */
    @Getter
    @Setter
    private boolean used;

    @Getter
    @Setter
    @Max(9)
    private int displayOrder = 0;

    @Getter
    @Setter
    @NotNull
    @Size(max=50)
    private PermissionClass permissionType;

    @Getter
    @Setter
    @NotNull
    @Size(max=50)
    private DisplayOrderType defaultDisplayOrder = DisplayOrderType.NAME;

    @Getter
    @Setter
    @NotNull
    @Size(max = 50)
    private DisplayOrderType defaultItemsDisplayOrder = DisplayOrderType.START_DATE;

    @Getter
    @Setter
    private boolean hasSubPermsManagement;

    @Getter
    @Setter
    private boolean doHighlight;

    public PublisherDTO(@NotNull final Long modelId, @NotNull final OrganizationDTO organization, @NotNull final ReaderDTO reader, @NotNull final RedactorDTO redactor,
                        @NotNull final String displayName, final boolean used, final int displayOrder, @NotNull final PermissionClass permissionType,
                        @NotNull final DisplayOrderType defaultDisplayOrder, final DisplayOrderType defaultItemsDisplayOrder, final boolean hasSubPermsManagement, final boolean doHighlight) {
        super(modelId);
        this.organization = organization;
        this.reader = reader;
        this.redactor = redactor;
        this.displayName = displayName;
        this.used = used;
        this.displayOrder = displayOrder;
        this.permissionType = permissionType;
        this.defaultDisplayOrder = defaultDisplayOrder;
        this.defaultItemsDisplayOrder = defaultItemsDisplayOrder;
        this.hasSubPermsManagement = hasSubPermsManagement;
        this.doHighlight = doHighlight;
    }
}
