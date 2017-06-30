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
package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.web.rest.dto.ContentDTO;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald 22 avril. 2015.
 */
public interface ContentDTOFactory {

    ContentDTO from (@NotNull final Long id) throws ObjectNotFoundException;

    //ContentDTO from (@NotNull final AbstractItem model);

    Set<ContentDTO> asDTOSet (final Collection<AbstractItem> models);

    List<ContentDTO> asDTOList (final Collection<AbstractItem> models);
}
