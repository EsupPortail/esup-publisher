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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
//@JacksonXmlRootElement(localName = "visibility")
@XmlRootElement(name = "visibility")
@XmlAccessorType(XmlAccessType.FIELD)
public class Visibility implements Serializable {

    @XmlElementWrapper(name="allowed", required = true)
    @XmlElements({
            @XmlElement(name="group",type=VisibilityGroup.class),
            @XmlElement(name="regex",type=VisibilityRegex.class),
            @XmlElement(name="regular",type=VisibilityRegular.class),
    })
    private List<VisibilityAbstract> allowed = new ArrayList<>();
    @XmlElementWrapper(name="autoSubscribed", required = true)
    @XmlElements({
            @XmlElement(name="group",type=VisibilityGroup.class),
            @XmlElement(name="regex",type=VisibilityRegex.class),
            @XmlElement(name="regular",type=VisibilityRegular.class),
    })
    private List<VisibilityAbstract> autoSubscribed = new ArrayList<>();
    @XmlElementWrapper(name="obliged", required = true)
    @XmlElements({
            @XmlElement(name="group",type=VisibilityGroup.class),
            @XmlElement(name="regex",type=VisibilityRegex.class),
            @XmlElement(name="regular",type=VisibilityRegular.class),
    })
    private List<VisibilityAbstract> obliged = new ArrayList<>();

}
