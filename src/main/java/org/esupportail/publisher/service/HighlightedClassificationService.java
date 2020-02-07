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

import javax.annotation.PostConstruct;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.service.bean.HighlightedClassification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Created by jgribonvald on 12/04/17.
 */
@Data
@Service
@Slf4j
public class HighlightedClassificationService {

    @Value("${app.service.highlightClassification.name}")
    private String name;

    @Value("${app.service.highlightClassification.description}")
    private String description;

    @Value("${app.service.highlightClassification.color}")
    private String color;

    private HighlightedClassification classification;

    @PostConstruct
    public void init() {
        Assert.notNull(name, "The highlighted classification name property `app.service.highlightClassification.name` is not defined");
        Assert.notNull(description, "The highlighted classification description property `app.service.highlightClassification.description` is not defined");
        Assert.notNull(color, "The highlighted classification color property `app.service.highlightClassification.color` is not defined");
        classification = new HighlightedClassification(name, description, color);
        log.debug("Contructed {}", classification);

        Assert.notNull(classification, "The constructed Highlighted Classification should not be null!");
    }

}
