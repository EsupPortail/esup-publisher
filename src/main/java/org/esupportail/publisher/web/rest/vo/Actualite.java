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
package org.esupportail.publisher.web.rest.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "actualites")
@XmlAccessorType(XmlAccessType.FIELD)
public class Actualite implements Serializable {

    @XmlElementWrapper(name = "rubriques")
    @XmlElement(name = "rubrique")
    private List<RubriqueVO> rubriques = new ArrayList<>();

    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    private List<ItemVO> items = new ArrayList<>();
}
