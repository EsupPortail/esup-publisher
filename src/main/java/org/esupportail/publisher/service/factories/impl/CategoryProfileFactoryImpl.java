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

import javax.annotation.PostConstruct;

import org.esupportail.publisher.config.ESUPPublisherProperties;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.service.factories.CategoryProfileFactory;
import org.esupportail.publisher.service.factories.VisibilityFactory;
import org.esupportail.publisher.web.rest.vo.CategoryProfile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Created by jgribonvald on 06/06/16.
 */
@Component
@Slf4j
public class CategoryProfileFactoryImpl implements CategoryProfileFactory {

    private int defaultTTL;
    private int defaultTimeout;

    @Autowired
    private ESUPPublisherProperties esupPublisherProperties;
    @Autowired
    private VisibilityFactory visibilityFactory;

    @PostConstruct
    public void init() {
        this.defaultTimeout = esupPublisherProperties.getService().getClassificationParams().getDefaultTimeout();
        this.defaultTTL = esupPublisherProperties.getService().getClassificationParams().getDefaultTTL();
        log.debug("Categories are defined with defaultTTL='{}' and defaultTimeout='{}'", this.defaultTTL, this.defaultTimeout);
    }

    @Override
    public CategoryProfile from(final Publisher publisher, final List<Subscriber> subscribers, final String urlActualites, final String urlCategory) {
        Assert.isTrue(urlActualites != null || urlCategory != null, "Error: a categoryProfile should have a not null urlActualites or urlCategory !");
        if (publisher != null) {
            CategoryProfile cp = new CategoryProfile();
            cp.setName(publisher.getContext().getOrganization().getDisplayName());
            cp.setId(publisher.getId());
            cp.setAccess(AccessType.PUBLIC);
            cp.setTimeout(defaultTimeout);
            cp.setTtl(defaultTTL);
            cp.setTrustCategory(true);
            cp.setVisibility(visibilityFactory.from(subscribers));
            cp.setUrlActualites(urlActualites);
            cp.setUrlCategory(urlCategory);
            return cp;
        }
        return new CategoryProfile();
    }

    @Override
    public CategoryProfile from(final Publisher publisher, final AbstractClassification classif, final List<Subscriber> subscribers, final String urlFeeds, final boolean withOrganizationName) {
        Assert.isTrue(urlFeeds != null, "Error: a categoryProfile should have a not null urlFeeds !");
        if (publisher != null && classif != null) {
            CategoryProfile cp = new CategoryProfile();
            if (withOrganizationName) {
                cp.setName(publisher.getContext().getOrganization().getDisplayName() + " - " + classif.getDisplayName());
            } else {
                cp.setName(classif.getDisplayName());
            }
            cp.setId(classif.getId());
            cp.setAccess(classif.getAccessView());
            cp.setTimeout(defaultTimeout);
            cp.setTtl((classif.getTtl() > 0) ? classif.getTtl() : defaultTTL);
            cp.setTrustCategory(true);
            cp.setVisibility(visibilityFactory.from(subscribers));
            cp.setUrlCategory(urlFeeds);
            return cp;
        }
        return new CategoryProfile();
    }
}