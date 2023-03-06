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
package org.esupportail.publisher.config.bean;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Validated
public class CKEditorProperties {

    @NotNull
    private CKEditor ckeditor = new CKEditor();

    @Data
    @Validated
    public static class CKEditor {

        @NotNull
        private MediaEmbed mediaEmbed = new MediaEmbed();

        @Data
        @Validated
        public static class MediaEmbed {

            @NotBlank
            private String mediaUrlPattern;

            @Override
            public String toString() {
                return "{\n\"MediaEmbed\":{"
                        + "\n \"mediaUrlPattern\":\"" + mediaUrlPattern
                        + "\n}\n}";
            }
        }

        @Override
        public String toString() {
            return "{\n\"CKEditor\":{"
                    + "\n \"mediaEmbed\":\"" + mediaEmbed
                    + "\n}\n}";
        }
    }

    @Override
    public String toString() {
        return "{\n\"CKEditorProperties\":{"
                + "\n \"ckeditor\":" + ckeditor
                + "\n}\n}";
    }

}
