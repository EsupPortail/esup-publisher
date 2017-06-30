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
package org.esupportail.publisher.service.exceptions;

import lombok.Getter;
import org.springframework.web.multipart.MultipartException;

/**
 * Exception to manage a specific error on unauthorized file ContentType when uploading.
 *
 * Created by jgribonvald on 06/04/17.
 */
public class UnsupportedMimeTypeException extends MultipartException {

    @Getter
    private String unsupportedMimeType;

    public UnsupportedMimeTypeException(final String unsupportedMimeType) {
        super("The content type '" + unsupportedMimeType + "' isn't supported !");
        this.unsupportedMimeType = unsupportedMimeType;
    }

    public UnsupportedMimeTypeException(final String message, final Throwable cause, final String unsupportedMimeType) {
        super(message, cause);
        this.unsupportedMimeType = unsupportedMimeType;
    }
}
