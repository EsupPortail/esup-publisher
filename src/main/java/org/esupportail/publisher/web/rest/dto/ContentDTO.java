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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Attachment;
import org.esupportail.publisher.domain.ContextKey;
import org.hibernate.validator.constraints.ScriptAssert;

/**
 * Created by jgribonvald on 22/04/15.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ScriptAssert(lang = "javascript", message = "Linked files should not be emtpy", script = "org.esupportail.publisher.web.rest.dto.ContentDTO.isValid(_this.item, _this.linkedFiles")
public class ContentDTO {

    //private Publisher publisher;

    private Set<ContextKey> classifications = new HashSet<>();

    private AbstractItem item;

    private List<SubscriberFormDTO> targets = new ArrayList<>();

    private Set<LinkedFileItemDTO> linkedFiles = new HashSet<>();


    public static boolean isValid(final AbstractItem item, final Set<LinkedFileItemDTO> linkedFiles) {
        return !(item instanceof Attachment && (linkedFiles == null || linkedFiles.isEmpty()));
    }

}
