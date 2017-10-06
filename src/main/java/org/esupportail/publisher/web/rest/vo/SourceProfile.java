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

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.web.rest.util.CustomLCEnumSerializer;

/**
 * Created by jgribonvald on 06/06/16.
 */
@Data
@NoArgsConstructor
@JsonTypeName("sourceProfile")
@XmlRootElement(name = "sourceProfile")
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceProfile implements Serializable {

    //@JacksonXmlProperty(isAttribute = true)
    @NotNull
    @XmlAttribute(required = true)
    private String name;

    //@JacksonXmlProperty(isAttribute = true)
    @NotNull
    @XmlAttribute(required = true)
    private long id;

    //@JacksonXmlProperty(isAttribute = true)
    @NotNull
    @XmlAttribute(required = true)
    private String url;

    //@JacksonXmlProperty(isAttribute = true)
    @XmlAttribute(required = true)
    private boolean specificUserContent;

    //@JacksonXmlProperty(isAttribute = true)
    @NotNull
    @XmlAttribute(required = true)
    @JsonSerialize(using = CustomLCEnumSerializer.class)
    private AccessType access;

    //@JacksonXmlProperty(isAttribute = true)
    @XmlAttribute(required = true)
    private int ttl = 3600;

    //@JacksonXmlProperty(isAttribute = true)
//    @XmlAttribute
//    private int timeout;
    private Visibility visibility;

}
