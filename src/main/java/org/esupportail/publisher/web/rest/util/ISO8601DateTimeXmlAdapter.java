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
package org.esupportail.publisher.web.rest.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Created by jgribonvald on 09/06/16.
 */
@Slf4j
public class ISO8601DateTimeXmlAdapter extends XmlAdapter<String, DateTime> {

    //private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    @Override
    public DateTime unmarshal(String v) throws Exception {
        return ISODateTimeFormat.dateTimeParser().parseDateTime(v);
    }

    @Override
    public String marshal(DateTime v) throws Exception {
        return ISODateTimeFormat.dateTime().print(v);
    }
}
