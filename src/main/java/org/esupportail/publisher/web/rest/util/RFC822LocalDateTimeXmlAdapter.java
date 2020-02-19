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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by jgribonvald on 09/06/16.
 */
public class RFC822LocalDateTimeXmlAdapter extends XmlAdapter<String, Instant> {


    // We need to set local to US for this RFC, as only few timezone are defined in this RFC
    //public static final String DATE_RFC822_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";
    //public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_RFC822_FORMAT, Locale.US).withZone(ZoneId.systemDefault());
    public static DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.systemDefault());

    @Override
    public Instant unmarshal(String v) {
        return LocalDateTime.parse(v, formatter).atZone(ZoneId.systemDefault()).toInstant();
    }

    @Override
    public String marshal(Instant v) {
        return formatter.format(v);
    }


}
