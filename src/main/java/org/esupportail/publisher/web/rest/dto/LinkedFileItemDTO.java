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

import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by jgribonvald on 11/04/17.
 */
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class LinkedFileItemDTO implements Serializable {

    @NotNull
    @Getter
    private String uri;

    @Getter
    private String filename;

    @Getter
    private boolean inBody = true;

    @Getter
    private String contentType;

    public LinkedFileItemDTO(@NotNull final String uri, final String filename, final boolean inBody, final String contentType) {
        this.uri = uri;
        this.filename = filename;
        this.inBody = inBody;
        this.contentType = contentType;
    }
}
