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
package org.esupportail.publisher.web.rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.domain.enums.ClassificationDecorType;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.FilterType;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.enums.ItemType;
import org.esupportail.publisher.domain.enums.OperatorType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.enums.StringEvaluationMode;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.enums.SubscribeType;
import org.esupportail.publisher.domain.enums.WritingFormat;
import org.esupportail.publisher.domain.enums.WritingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Evaluator.
 */
@RestController
@RequestMapping("/api/enums")
public class EnumsResource {

	private final Logger log = LoggerFactory.getLogger(EnumsResource.class);

    protected enum enumList {
        AccessType, ContextType, ClassificationDecorType, DisplayOrderType, FilterType, ItemStatus, ItemType, OperatorType, PermissionClass, PermissionType,
        StringEvaluationMode, SubjectType, SubscribeType, WritingMode, WritingFormat
    }

    @RequestMapping(value = "/all",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<enumList, List<? extends Enum<?>>>> getAllEnums() {
        Map<enumList, List<? extends Enum<?>>> enums = new HashMap<>(15);
        enums.put(enumList.AccessType, Arrays.asList(AccessType.values()));
        enums.put(enumList.ContextType, Arrays.asList(DisplayOrderType.values()));
        enums.put(enumList.ClassificationDecorType, Arrays.asList(ClassificationDecorType.values()));
        enums.put(enumList.DisplayOrderType, Arrays.asList(DisplayOrderType.values()));
        enums.put(enumList.FilterType, Arrays.asList(FilterType.values()));
        enums.put(enumList.ItemStatus, Arrays.asList(ItemStatus.values()));
        enums.put(enumList.ItemType, Arrays.asList(ItemType.values()));
        enums.put(enumList.OperatorType, Arrays.asList(OperatorType.values()));
        enums.put(enumList.PermissionClass, Lists.newArrayList(PermissionClass.CONTEXT, PermissionClass.CONTEXT_WITH_SUBJECTS));
        enums.put(enumList.PermissionType, Arrays.asList(PermissionType.values()));
        enums.put(enumList.StringEvaluationMode, Arrays.asList(StringEvaluationMode.values()));
        enums.put(enumList.SubjectType, Arrays.asList(SubjectType.values()));
        enums.put(enumList.SubscribeType, Arrays.asList(SubscribeType.values()));
        enums.put(enumList.WritingMode, Arrays.asList(WritingMode.values()));
        enums.put(enumList.WritingFormat, Arrays.asList(WritingFormat.values()));

        return new ResponseEntity<Map<enumList, List<? extends Enum<?>>>>(enums, HttpStatus.OK);
    }

}