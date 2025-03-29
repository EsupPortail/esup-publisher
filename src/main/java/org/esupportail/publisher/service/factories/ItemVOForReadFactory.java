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

import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.LinkedFileItem;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.web.rest.vo.ItemVO;
import org.esupportail.publisher.web.rest.vo.ItemVOForRead;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by jgribonvald on 03/06/16.
 */
public interface ItemVOForReadFactory {

    ItemVOForRead from(final AbstractItem item, final List<AbstractClassification> classifications, final List<Subscriber> subscribers,
                       final List<LinkedFileItem> linkedFiles, final HttpServletRequest request);
}
