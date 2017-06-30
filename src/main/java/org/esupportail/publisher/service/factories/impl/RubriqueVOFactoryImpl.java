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
package org.esupportail.publisher.service.factories.impl;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.service.bean.HighlightedClassification;
import org.esupportail.publisher.service.factories.RubriqueVOFactory;
import org.esupportail.publisher.web.rest.vo.RubriqueVO;
import org.springframework.stereotype.Component;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Component
public class RubriqueVOFactoryImpl implements RubriqueVOFactory {

    public RubriqueVO from(final AbstractClassification model) {
        if (model != null) {
            RubriqueVO vo = new RubriqueVO(model.getId().toString(), model.getDisplayName(), model.getColor(), model.getIconUrl(), model.isHighlighted());
            vo.setUuid(model.getId().toString());
            return vo;
        }
        return null;
    }

    public RubriqueVO from(final HighlightedClassification model) {
        if (model != null) {
            RubriqueVO vo = new RubriqueVO(model.getId().toString(), model.getName(), model.getColor(), null, model.isHighlight());
            vo.setUuid(model.getId().toString());
            return vo;
        }
        return null;
    }

    public List<RubriqueVO> asVOList(final Collection<? extends AbstractClassification> models) {
        final List<RubriqueVO> tvo = Lists.newArrayList();
        if (models != null && !models.isEmpty()) {
            for (AbstractClassification model : models) {
                tvo.add(from(model));
            }
        }
        return tvo;
    }
}
