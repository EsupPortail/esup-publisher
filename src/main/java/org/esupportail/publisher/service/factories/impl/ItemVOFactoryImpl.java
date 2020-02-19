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

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import lombok.SneakyThrows;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.LinkedFileItem;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.service.HighlightedClassificationService;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.esupportail.publisher.service.factories.ItemVOFactory;
import org.esupportail.publisher.service.factories.VisibilityFactory;
import org.esupportail.publisher.web.rest.util.ISO8601LocalDateTimeXmlAdapter;
import org.esupportail.publisher.web.rest.vo.ItemVO;
import org.esupportail.publisher.web.rest.vo.LinkedFileVO;
import org.esupportail.publisher.web.rest.vo.Visibility;
import org.esupportail.publisher.web.rest.vo.ns.ArticleVO;
import org.springframework.stereotype.Component;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Component
public class ItemVOFactoryImpl implements ItemVOFactory {

    @Inject
    private ServiceUrlHelper urlHelper;

    @Inject
    private VisibilityFactory visibilityFactory;

    @Inject
    private HighlightedClassificationService highlightedClassificationService;

    private XmlAdapter<String, Instant> ISO8601Adapter = new ISO8601LocalDateTimeXmlAdapter();

    @SneakyThrows
    public ItemVO from(final AbstractItem item, final List<AbstractClassification> classifications, final List<Subscriber> subscribers,
                       final List<LinkedFileItem> linkedFiles, final HttpServletRequest request) {
        if (item != null) {
            ItemVO vo = new ItemVO();
            vo.setRubriques(new ArrayList<Long>());
            vo.setType(item.getClass().getSimpleName());
            ArticleVO article = new ArticleVO();
            article.setTitle(item.getTitle());
            article.setLink(urlHelper.getContextPath() + urlHelper.getItemUri() + item.getId());
            if (item.getEnclosure() != null) {
                if (item.getEnclosure().matches("^https?://.*$")) {
                    article.setEnclosure(item.getEnclosure());
                } else {
                    article.setEnclosure(urlHelper.getRootAppUrl(request) + item.getEnclosure());
                }
            }
            article.setDescription(item.getSummary());
            article.setPubDate(item.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            article.setGuid(item.getId().hashCode());
            article.setCategories(new ArrayList<String>());
            for (AbstractClassification classif: classifications) {
                article.getCategories().add(classif.getDisplayName());
                vo.getRubriques().add(classif.getId());
            }
            if (item.isHighlight()) {
                article.getCategories().add(highlightedClassificationService.getClassification().getName());
                vo.getRubriques().add(highlightedClassificationService.getClassification().getId());
            }
            article.setCreator(item.getCreatedBy().getDisplayName());
            article.setDate(item.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            article.setFiles(new ArrayList<LinkedFileVO>());
            for (LinkedFileItem linkedFile: linkedFiles) {
                if (linkedFile.getUri() != null && !linkedFile.getUri().isEmpty()) {
                    LinkedFileVO fileVO = new LinkedFileVO();
                    fileVO.setUri(urlHelper.getContextPath() + "/" + linkedFile.getUri());
                    fileVO.setFileName(linkedFile.getFilename());
                    fileVO.setContentType(linkedFile.getContentType());
                    article.getFiles().add(fileVO);
                }
            }
            vo.setArticle(article);
            vo.setCreator(item.getCreatedBy().getId().getKeyId());
            vo.setPubDate(ISO8601Adapter.marshal(item.getStartDate().atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().toInstant()));
            vo.setCreatedDate(ISO8601Adapter.marshal(item.getCreatedDate()));
            if (item.getLastModifiedDate() != null)
                vo.setModifiedDate(ISO8601Adapter.marshal(item.getLastModifiedDate().truncatedTo(ChronoUnit.MILLIS)));
            vo.setUuid(item.getId().toString());
            if (subscribers != null && !subscribers.isEmpty()) {
                vo.setVisibility(visibilityFactory.from(subscribers));
            } else {
                vo.setVisibility(new Visibility());
            }

            return vo;
        }
        return null;
    }

}
