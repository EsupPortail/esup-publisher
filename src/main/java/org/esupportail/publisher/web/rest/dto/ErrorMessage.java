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
package org.esupportail.publisher.web.rest.dto;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Created by jgribonvald on 21/02/17.
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorMessage implements Serializable {
    @NonNull
    private String message;

    private Map<String, ? extends Object> params = Maps.newLinkedHashMap();
}
