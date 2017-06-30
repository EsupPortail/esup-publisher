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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jgribonvald on 09/06/16.
 */
public class SeparatorListXMLAdapter extends XmlAdapter<String, List<String>> {

    private static final String SEPARATOR = "/";

    @Override
    public List<String> unmarshal(final String string) {
        final List<String> strings = new ArrayList<>();

        for (final String s : string.split(SEPARATOR)) {
            final String trimmed = s.trim();

            if (trimmed.length() > 0) {
                strings.add(trimmed);
            }
        }

        return strings;
    }

    @Override
    public String marshal(final List<String> strings) {
        final StringBuilder sb = new StringBuilder();

        for (final String string : strings) {
            if (sb.length() > 0) {
                sb.append(SEPARATOR);
            }

            sb.append(string);
        }

        return sb.toString();
    }
}
