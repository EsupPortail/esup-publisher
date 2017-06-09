package org.esupportail.publisher.service.factories.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.LinkedFileItem;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.service.HighlightedClassificationService;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.esupportail.publisher.service.factories.ItemVOFactory;
import org.esupportail.publisher.service.factories.VisibilityFactory;
import org.esupportail.publisher.web.rest.vo.ItemVO;
import org.esupportail.publisher.web.rest.vo.Visibility;
import org.esupportail.publisher.web.rest.vo.ns.ArticleVO;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
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
            article.setPubDate(item.getStartDate().toDateTime(LocalTime.MIDNIGHT, DateTimeZone.getDefault()));
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
            article.setDate(item.getStartDate().toDateTime(LocalTime.MIDNIGHT, DateTimeZone.getDefault()));
            article.setFiles(new ArrayList<String>());
            for (LinkedFileItem linkedFile: linkedFiles) {
                if (linkedFile.getUri() != null && !linkedFile.getUri().isEmpty()) {
                    article.getFiles().add(urlHelper.getRootAppUrl(request) + linkedFile.getUri());
                }
            }
            vo.setArticle(article);
            vo.setCreator(item.getCreatedBy().getId().getKeyId());
            vo.setPubDate(item.getStartDate().toDateTime(LocalTime.MIDNIGHT, DateTimeZone.getDefault()).toString());
            vo.setCreatedDate(item.getCreatedDate().toDateTime(DateTimeZone.getDefault()).toString());
            if (item.getLastModifiedDate() != null)
                vo.setModifiedDate(item.getLastModifiedDate().toDateTime(DateTimeZone.getDefault()).toString());
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
