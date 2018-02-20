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
package org.esupportail.publisher.repository.externals.ldap;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.externals.ExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalGroupDisplayNameFormatter;

/**
 * Created by jgribonvald on 04/06/15.
 */
@ToString(of = {})
@Slf4j
public class LdapGroupRegexpDisplayNameFormatterESCOReplace implements IExternalGroupDisplayNameFormatter {


    public LdapGroupRegexpDisplayNameFormatterESCOReplace() { }

    @Override
    public ExternalGroup format(final ExternalGroup input) {
        ExternalGroup group = input;
        // setting as new displayName the origin displayName formatted
        group.setDisplayName(format(input.getDisplayName()));
        log.debug("DisplayNameFormatter renamed {} to {}", input.getDisplayName(), group.getDisplayName());
        return group;
    }

    private String format(String input) {
        if (input != null && !input.isEmpty()) {
                return input.replaceFirst("Tous_","").replaceAll("_", " ");
        }
        return input;
    }
}
