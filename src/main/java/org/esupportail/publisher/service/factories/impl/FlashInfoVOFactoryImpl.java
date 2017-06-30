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

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Lists;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.Flash;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.esupportail.publisher.service.factories.FlashInfoVOFactory;
import org.esupportail.publisher.service.factories.RubriqueVOFactory;
import org.esupportail.publisher.web.rest.vo.FlashInfoVO;
import org.springframework.stereotype.Service;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Service
public class FlashInfoVOFactoryImpl implements FlashInfoVOFactory {

    @Inject
    private RubriqueVOFactory rubriqueVOFactory;

    @Inject
    private ServiceUrlHelper urlHelper;

    @Override
    public FlashInfoVO from(final Flash model, List<AbstractClassification> tags, final HttpServletRequest request ) {
        if (model != null) {
            FlashInfoVO flashInfoVO = new FlashInfoVO();
            flashInfoVO.setTitle(model.getTitle());
            flashInfoVO.setSummary(model.getSummary());
            if (model.getEnclosure() != null) {
                if (model.getEnclosure().matches("^https?://.*$")) {
                    flashInfoVO.setMediaUrl(model.getEnclosure());
                } else {
                    flashInfoVO.setMediaUrl(urlHelper.getRootAppUrl(request) + model.getEnclosure());
                }
            }
            flashInfoVO.setLink(urlHelper.getContextPath() + urlHelper.getItemUri() + model.getId());
            if (tags != null && !tags.isEmpty()) {
                flashInfoVO.setRubriques(rubriqueVOFactory.asVOList(tags));
            }

            return flashInfoVO;
        }
        return null;
    }

    @Override
    public List<FlashInfoVO> asVOList(final Map<Flash, List<AbstractClassification>> models, final HttpServletRequest request ) {
        final List<FlashInfoVO> tvo = Lists.newArrayList();
        if ((models != null) && !models.isEmpty()) {
            for (final Map.Entry<Flash, List<AbstractClassification>> model : models.entrySet()) {
                tvo.add(from(model.getKey(), model.getValue(), request));
            }
        }
        return tvo;
    }
}
