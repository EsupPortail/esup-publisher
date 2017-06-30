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
package org.esupportail.publisher.repository.externals.ws;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.externals.ExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalGroupDisplayNameFormatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jgribonvald on 04/06/15.
 */
@Slf4j
public class WSExternalGroupDisplayNameFormatter implements IExternalGroupDisplayNameFormatter {

    private static final String dnPattern = ".*:([^:]*)";

    private Pattern pattern = Pattern.compile(dnPattern);

    public WSExternalGroupDisplayNameFormatter() {
        super();
    }

    @Override
    public ExternalGroup format(ExternalGroup input) {
            // setting as new displayName the origin displayName formatted
         input.setDisplayName(format(input.getDisplayName()));

        return input;
    }

    private String format(String input) {
        if (input != null && !input.isEmpty()) {
            Matcher m = pattern.matcher(input);
            if (m.find()) {
                final String displayName = m.group(1);
                log.debug("Matcher found group displayName, value is : {}", displayName);
                return displayName.replaceAll("_", " ");
            }
            log.warn("No displayname formatting will be done as pattern isn't matching !");
        }
        return input;
    }
}
