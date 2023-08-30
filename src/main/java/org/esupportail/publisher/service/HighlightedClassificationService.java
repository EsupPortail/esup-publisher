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
package org.esupportail.publisher.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.config.ESUPPublisherProperties;
import org.esupportail.publisher.service.bean.HighlightedClassification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * Created by jgribonvald on 12/04/17.
 */
@Service
@Slf4j
public class HighlightedClassificationService {

    private ESUPPublisherProperties esupPublisherProperties;

    @Getter
    private HighlightedClassification classification;

    public HighlightedClassificationService(ESUPPublisherProperties esupPublisherProperties) {
        this.esupPublisherProperties = esupPublisherProperties;
        this.classification = new HighlightedClassification(
                esupPublisherProperties.getService().getClassificationParams().getHighlightClassification().getName(),
                esupPublisherProperties.getService().getClassificationParams().getHighlightClassification().getColor(),
                esupPublisherProperties.getService().getClassificationParams().getHighlightClassification().getDescription(),
                esupPublisherProperties.getService().getClassificationParams().getHighlightClassification().isHiddenIfEmpty()
        );
    }

    @PostConstruct
    public void init() {
        Assert.notNull(classification.getName(), "The highlighted classification name property `app.service.highlight-classification.name` is not defined");
        Assert.notNull(classification.getDescription(), "The highlighted classification description property `app.service.highlight-classification.description` is not defined");
        Assert.notNull(classification.getColor(), "The highlighted classification color property `app.service.highlight-classification.color` is not defined");

        log.debug("Contructed {}", classification);

        Assert.notNull(classification, "The constructed Highlighted Classification should not be null!");
    }

}
