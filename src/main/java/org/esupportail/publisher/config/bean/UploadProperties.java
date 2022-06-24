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

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.google.common.collect.Sets;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class UploadProperties {

    @NotBlank
    private String path;
    private Long imageMaxSize;
    @NotBlank
    private String protectedPath;
    @NotBlank
    private String filePathOfAuthorizedMimeTypes = "MimeTypes.txt";
    private Long maxFileSize;
    @NotEmpty
    private Set<String> unremovablePathPattern = Sets.newHashSet("default/welcome.*","test.*");

    @Override
    public String toString() {
        return "{\n\"UploadProperties\":{"
                + "\n \"path\":\"" + path + "\""
                + ",\n \"imageMaxSize\":\"" + imageMaxSize + "\""
                + ",\n \"protectedPath\":\"" + protectedPath + "\""
                + ",\n \"filePathOfAuthorizedMimeTypes\":\"" + filePathOfAuthorizedMimeTypes + "\""
                + ",\n \"maxFileSize\":\"" + maxFileSize + "\""
                + ",\n \"unremovablePathPattern\":" + unremovablePathPattern.stream()
                .map(String::valueOf)
                .collect(Collectors.joining("\",\"", "[\"", "\"]"))
                + "\n}\n}";
    }
}