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

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.esupportail.publisher.domain.util.Views;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName(value = "rubrique")
@XmlRootElement(name = "rubrique")
@XmlAccessorType(XmlAccessType.FIELD)
public class RubriqueVO implements Serializable {

    @NonNull
    private String uuid;

    @NonNull
    @JsonView(Views.Flash.class)
    private String name;

    @JsonView(Views.Flash.class)
    private String color;

    private String mediaUrl;

    private boolean highlight;

    private boolean hiddenIfEmpty;
}
