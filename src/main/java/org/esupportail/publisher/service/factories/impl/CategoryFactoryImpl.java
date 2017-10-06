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

import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractFeed;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.service.factories.CategoryFactory;
import org.esupportail.publisher.service.factories.VisibilityFactory;
import org.esupportail.publisher.web.FeedController;
import org.esupportail.publisher.web.rest.vo.Category;
import org.esupportail.publisher.web.rest.vo.SourceProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by jgribonvald on 06/06/16.
 */
@Component
public class CategoryFactoryImpl implements CategoryFactory {

    @Value("${app.service.defaultTTL:3600}")
    private int defaultTTL;

    @Value("${app.service.defaultTimeout:5000}")
    private int defaultTimeout;

    @Inject
    private VisibilityFactory visibilityFactory;

    @Override
    public Category fromCategoriesClassifs(final Publisher publisher, final String baseURL, final List<Subscriber> subscribers,
                                           final Map<AbstractClassification, List<Subscriber>> categoriesSubscribers) {
        if (publisher != null) {
            Category category = new Category();
            category.setName(publisher.getContext().getOrganization().getDisplayName());
            category.setTtl(defaultTTL);
            category.setVisibility(visibilityFactory.from(subscribers));
            for (Map.Entry<AbstractClassification, List<Subscriber>> entry: categoriesSubscribers.entrySet()){
                SourceProfile sp = new SourceProfile();
                sp.setId(entry.getKey().getId());
                sp.setName(entry.getKey().getDisplayName());
                sp.setSpecificUserContent(false);
                String url;
                if (baseURL.endsWith("/") && FeedController.PRIVATE_RSS_FEED_URL_PATH.startsWith("/")) {
                   url = baseURL + FeedController.PRIVATE_RSS_FEED_URL_PATH.replaceFirst("/", "") + sp.getId();
                } else {
                   url = baseURL + FeedController.PRIVATE_RSS_FEED_URL_PATH + sp.getId();
                }
                sp.setUrl(url);
                sp.setAccess(entry.getKey().getAccessView());
                sp.setTtl((entry.getKey().getTtl() > 0) ? entry.getKey().getTtl() : defaultTTL);
                sp.setVisibility(visibilityFactory.from(entry.getValue()));
                category.getSourceProfiles().add(sp);
            }
            return category;
        }
        return new Category();
    }

    @Override
    public Category fromAbstractFeeds(final org.esupportail.publisher.domain.Category classif, final String baseURL, final boolean withOrganizationName,
                                      final List<Subscriber> subscribers, final Map<AbstractFeed, List<Subscriber>> feedsSubscribers) {
         if (classif != null) {
             Category category = new Category();
             if (withOrganizationName) {
                 category.setName(classif.getPublisher().getContext().getOrganization().getDisplayName());
             } else {
                 category.setName(classif.getDisplayName());
             }
             category.setDescription(classif.getDescription());
             category.setTtl((classif.getTtl() > 0) ? classif.getTtl() : defaultTTL);
             category.setVisibility(visibilityFactory.from(subscribers));
             for (Map.Entry<AbstractFeed, List<Subscriber>> entry: feedsSubscribers.entrySet()){
                 SourceProfile sp = new SourceProfile();
                 sp.setId(entry.getKey().getId());
                 sp.setName(entry.getKey().getDisplayName());
                 sp.setSpecificUserContent(false);
                 String url;
                 if (baseURL.endsWith("/") && FeedController.PRIVATE_RSS_FEED_URL_PATH.startsWith("/")) {
                     url = baseURL + FeedController.PRIVATE_RSS_FEED_URL_PATH.replaceFirst("/", "") + sp.getId();
                 } else {
                     url = baseURL + FeedController.PRIVATE_RSS_FEED_URL_PATH + sp.getId();
                 }
                 sp.setUrl(url);
                 sp.setAccess(entry.getKey().getAccessView());
                 sp.setTtl((entry.getKey().getTtl() > 0) ? entry.getKey().getTtl() : defaultTTL);
                 sp.setVisibility(visibilityFactory.from(entry.getValue()));
                 category.getSourceProfiles().add(sp);
             }
             return category;
         }
        return new Category();
    }
}
