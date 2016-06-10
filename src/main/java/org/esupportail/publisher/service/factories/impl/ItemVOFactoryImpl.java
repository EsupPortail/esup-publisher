package org.esupportail.publisher.service.factories.impl;

import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.esupportail.publisher.service.factories.ItemVOFactory;
import org.esupportail.publisher.service.factories.VisibilityFactory;
import org.esupportail.publisher.web.rest.vo.ItemVO;
import org.esupportail.publisher.web.rest.vo.Visibility;
import org.esupportail.publisher.web.rest.vo.ns.ArticleVO;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Component
public class ItemVOFactoryImpl implements ItemVOFactory {

    @Inject
    private ServiceUrlHelper urlHelper;

    @Inject
    private VisibilityFactory visibilityFactory;

    public ItemVO from(final AbstractItem item, final List<AbstractClassification> classifications, final List<Subscriber> subscribers) {
        if (item != null) {
            ItemVO vo = new ItemVO();
            vo.setRubriques(new ArrayList<Long>());
            ArticleVO article = new ArticleVO();
            article.setTitle(item.getTitle());
            article.setLink("/" + urlHelper.getContextPath().replaceFirst("/","") + urlHelper.getItemUri() + item.getId());
            if (item.getEnclosure() != null)
                article.setEnclosure(urlHelper.getRootAppUrl() + item.getEnclosure());
            article.setDescription(item.getSummary());
            article.setPubDate(item.getStartDate().toDateTime(LocalTime.MIDNIGHT, DateTimeZone.getDefault()));
            article.setGuid(item.getId().hashCode());
            article.setCategories(new ArrayList<String>());
            for (AbstractClassification classif: classifications) {
                article.getCategories().add(classif.getDisplayName());
                vo.getRubriques().add(classif.getId());
            }
            article.setCreator(item.getCreatedBy().getDisplayName());
            article.setDate(item.getStartDate().toDateTime(LocalTime.MIDNIGHT, DateTimeZone.getDefault()));
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
