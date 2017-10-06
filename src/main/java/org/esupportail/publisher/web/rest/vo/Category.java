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

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by jgribonvald on 06/06/16.
 */
@Data
@NoArgsConstructor
@JsonTypeName("category")
@XmlRootElement(name = "category")
@XmlAccessorType(XmlAccessType.FIELD)
public class Category implements Serializable {

    //@JacksonXmlProperty(isAttribute = true)
    @NotNull
    @XmlAttribute
    private String name;

    @XmlAttribute
    private String edit = "all";

    //@JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private int ttl = 3600;

    private String description;

    private Visibility visibility;

    //@JacksonXmlProperty(localName = "sourceProfile")
    //@JacksonXmlElementWrapper(useWrapping = false)
    @XmlElementWrapper(name = "sourceProfiles")
    @XmlElement(name = "sourceProfile")
    private List<SourceProfile> sourceProfiles = new ArrayList<>();
}
