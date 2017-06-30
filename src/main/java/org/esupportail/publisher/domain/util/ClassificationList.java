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
package org.esupportail.publisher.domain.util;

import org.esupportail.publisher.domain.AbstractClassification;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by jgribonvald on 30/03/15.
 * Usefull to avoid Java Type Erasure on JsonSerialization of List< AbstractClassification >
 *     see https://github.com/FasterXML/jackson-databind/issues/699
 */
public class ClassificationList extends ArrayList<AbstractClassification> {

    public ClassificationList(Collection<? extends AbstractClassification> c) {
        super(c);
    }
}
