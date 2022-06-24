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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class ServiceProperties {

    @NotNull
    private ClassificationParams classificationParams = new ClassificationParams();

    @Data
    @Validated
    public static class ClassificationParams{
        @Min(600)
        private int defaultTTL = 3600;
        @Min(3000)
        private int defaultTimeout= 15000;
        @NotNull
        private HighlightClassification highlightClassification = new HighlightClassification();

        @Data
        @Validated
        public static class HighlightClassification{

            @NotBlank
            private String name;
            @NotBlank
            private String color;
            @NotBlank
            private String description;

            @Override
            public String toString() {
                return "{\n\"HighlightClassification\":{"
                        + "\n \"name\":\"" + name + "\""
                        + ",\n \"color\":\"" + color + "\""
                        + ",\n \"description\":\"" + description + "\""
                        + "\n}\n}";
            }
        }

        @Override
        public String toString() {
            return "{\n\"ClassificationParams\":{"
                    + "\n \"defaultTTL\":\"" + defaultTTL + "\""
                    + ",\n \"defaultTimeout\":\"" + defaultTimeout + "\""
                    + ",\n \"highlightClassification\":" + highlightClassification
                    + "\n}\n}";
        }
    }

    @Override
    public String toString() {
        return "{\n\"ServiceProperties\":{"
                + "\n \"classificationParams\":" + classificationParams
                + "\n}\n}";
    }
}