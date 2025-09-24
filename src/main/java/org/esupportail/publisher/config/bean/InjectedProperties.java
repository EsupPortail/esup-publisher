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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Validated
public class InjectedProperties {

    private List<WebComponents> webComponents;

    @Data
    @Validated
    public static class WebComponents {

        @NotNull
        private String name;

        @NotNull
        private String selector;

        @NotNull
        private ComponentData data = new ComponentData();

        @Data
        @Validated
        public static class ComponentData {

            @NotBlank
            private String componentPath;

            private Map<String, String> props;

            @Override
            public String toString() {
                return "{"
                    + "\n   \"componentPath\": \"" + componentPath + "\","
                    + "\n   \"props\": " + props.entrySet().stream()
                    .map((prop) -> "\"" + prop.getKey() + "\": \"" + prop.getValue() + "\"")
                    .collect(Collectors.joining(",\n      ", "{\n      ", "\n    }"))
                    + "\n  }";
            }
        }

        @Override
        public String toString() {
            return "{"
                + "\n \"name\": \"" + name + "\","
                + "\n \"selector\": \"" + selector + "\","
                + "\n \"data\": " + data
                + "\n}";
        }
    }

    @Override
    public String toString() {
        return "{\n\"InjectedProperties\":[\n"
            + webComponents.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(",\n"))
            + "\n]\n}";
    }
}
