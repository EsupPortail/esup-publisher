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
/**
 *
 */
package org.esupportail.publisher.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 27 juin 2014
 */
@Data
@AllArgsConstructor
public class SubjectPermKeyDTO implements Serializable {

    /** */
    private static final long serialVersionUID = 7484366629825380189L;

    @NotNull
    private SubjectKeyDTO subjectKey;

    private boolean needValidation;


//    public static class SubjectPermComparatorOnSubject implements Comparator<SubjectPermKey> {
//
//        @Override
//        public int compare(SubjectPermKey o1, SubjectPermKey o2) {
//            int typeCompare = o1.subjectType.compareTo(o2.subjectType);
//            if (typeCompare != 0) {
//                return typeCompare;
//            }
//            return o1.subjectId.compareTo(o2.subjectId);
//
//        }
//
//    }

    //public static final SubjectPermComparatorOnSubject SUBJECTPERM_ONSUBJECT_COMPARATOR = new SubjectPermComparatorOnSubject();

}
