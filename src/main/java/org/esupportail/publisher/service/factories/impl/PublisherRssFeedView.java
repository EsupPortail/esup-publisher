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

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysema.commons.lang.Pair;
import com.rometools.rome.feed.rss.Category;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Enclosure;
import com.rometools.rome.feed.rss.Guid;
import com.rometools.rome.feed.rss.Image;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.feed.rss.Source;
import lombok.Setter;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.ItemClassificationOrder;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

/**
 * Created by jgribonvald on 28/06/17.
 */
@Component("publisherRssFeedView")
public class PublisherRssFeedView extends AbstractRssFeedView {

    public final static String ORG_PARAM = "organization";
    public final static String PUB_PARAM = "publisher";
    public final static String CLASSIF_PARAM = "classification";
    public final static String ITEMS_PARAM = "items";

    @Setter
    private String channelGenerator = "Publisher";
    @Setter
    private String docRssUrl = "http://www.rssboard.org/rss-specification";

    @Autowired
    private ServiceUrlHelper urlHelper;

    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Channel channel,
                                     HttpServletRequest request) {
        final Object orgObj = model.get(ORG_PARAM);
        final Object pubObj = model.get(PUB_PARAM);
        final Object clasObj = model.get(CLASSIF_PARAM);
        if (orgObj != null && orgObj instanceof Organization) {
            Organization org = (Organization) model.get(ORG_PARAM);
            channel.setTitle("Contenus publiés par l'établissement : " + org.getDisplayName());
            channel.setLink(urlHelper.getRootDomainUrl(request) + request.getRequestURI() + "?" + request.getQueryString());
            channel.setPubDate(new Date());
            channel.setGenerator(channelGenerator);
            channel.setDocs(docRssUrl);
            if (clasObj != null && clasObj instanceof AbstractClassification) {
                channel.setDescription("Contenus limités à la rubrique " + ((AbstractClassification) clasObj).getDisplayName() +
                    ": " + ((AbstractClassification) clasObj).getDescription());
                channel.setLanguage(((AbstractClassification) clasObj).getLang());
                if (((AbstractClassification) clasObj).getIconUrl() != null) {
                    Image image = new Image();
                    if (((AbstractClassification) clasObj).getIconUrl().matches("^https?://.*$")) {
                        image.setUrl(((AbstractClassification) clasObj).getIconUrl());
                    } else {
                        image.setUrl(urlHelper.getRootAppUrl(request) + ((AbstractClassification) clasObj).getIconUrl());
                    }
                    image.setTitle(((AbstractClassification) clasObj).getDisplayName());
                    image.setLink(urlHelper.getRootDomainUrl(request));
                    image.setDescription(((AbstractClassification) clasObj).getDescription());
                    channel.setImage(image);
                }
            } else if (pubObj != null && pubObj instanceof Publisher) {
                channel.setDescription("Contenus limités au contexte de publication : " + ((Publisher) pubObj).getDisplayName());
            } else {
                channel.setDescription("Flux RSS de l'outil de publication de contenus de l'ENT à partir des Actualités, informations et autres types de contenus" +
                    " publiés par l'établissement " + org.getDisplayName());
            }
        }
    }

    @Override
    protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Item> items = new ArrayList<>();

        final Object itemsObj = model.get(ITEMS_PARAM);
        if (itemsObj instanceof List) {
            Map<Long, Pair<AbstractItem, List<AbstractClassification>>> itemsMap = Maps.newLinkedHashMap();
            for(Object itemObj: (List<?>)itemsObj){
                if (itemObj instanceof ItemClassificationOrder) {
                    // get unique items associated to all his classifs
                    final AbstractClassification classif = ((ItemClassificationOrder)itemObj).getItemClassificationId().getAbstractClassification();
                    //categories.add(classif);
                    final Long itemId = ((ItemClassificationOrder)itemObj).getItemClassificationId().getAbstractItem().getId();
                    if (!itemsMap.containsKey(itemId)) {
                        itemsMap.put(itemId, new Pair<AbstractItem, List<AbstractClassification>>(((ItemClassificationOrder)itemObj).getItemClassificationId().getAbstractItem(), Lists.newArrayList(classif)));
                    } else {
                        itemsMap.get(itemId).getSecond().add(classif);
                    }
                }
            }

            for (Map.Entry<Long, Pair<AbstractItem, List<AbstractClassification>>> entry : itemsMap.entrySet()) {
                final AbstractItem publication = entry.getValue().getFirst();
                Item item = new Item();
                item.setTitle(publication.getTitle());
                item.setLink(urlHelper.getRootDomainUrl(request) + urlHelper.getContextPath() + urlHelper.getItemUri() + publication.getId());
                Content content = new Content();
                content.setValue(publication.getSummary());
                item.setContent(content);
                if (publication.getCreatedBy() != null)
                    item.setAuthor(publication.getCreatedBy().getDisplayName());
                if (publication.getStartDate() != null)
                    item.setPubDate(Date.from(publication.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                if (publication.getEndDate() != null)
                    item.setExpirationDate(Date.from(publication.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                if (publication.getId() != null) {
                    Guid guid = new Guid();
                    guid.setValue(publication.getId().toString());
                    guid.setPermaLink(true);
                    item.setGuid(guid);
                }
                List<Category> cats = Lists.newArrayList();
                for (AbstractClassification classif: entry.getValue().getSecond()) {
                    Category cat = new Category();
                    cat.setValue(classif.getDisplayName());
                    cats.add(cat);
                }
                item.setCategories(cats);
                if (publication.getEnclosure() != null) {
                    Enclosure enclosure = new Enclosure();
                    if (publication.getEnclosure().matches("^https?://.*$")) {
                        enclosure.setUrl(publication.getEnclosure());
                    } else {
                        enclosure.setUrl(urlHelper.getRootAppUrl(request) + publication.getEnclosure());
                    }
                    item.setEnclosures(Lists.newArrayList(enclosure));
                }
                Source source = new Source();
                source.setUrl(urlHelper.getRootAppUrl(request));
                source.setValue(channelGenerator);
                item.setSource(source);
                items.add(item);
            }
        }


        return items;
    }
}
