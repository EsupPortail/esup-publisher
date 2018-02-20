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

import java.util.regex.Matcher;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.externals.ExternalGroup;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
import org.esupportail.publisher.domain.externals.IExternalGroupDisplayNameFormatter;
import org.springframework.util.Assert;

/**
 * Created by jgribonvald on 04/06/15.
 */
@ToString(of = {})
@Slf4j
public class LdapGroupRegexpDisplayNameFormatter implements IExternalGroupDisplayNameFormatter {

    private ExternalGroupHelper externalGroupHelper;

    public LdapGroupRegexpDisplayNameFormatter(final ExternalGroupHelper externalGroupHelper) {
        this.externalGroupHelper = externalGroupHelper;
        Assert.isTrue(externalGroupHelper.getGroupDisplayNameRegex() != null, "You should provide a Pattern to format Groups Name !");
    }

    @Override
    public ExternalGroup format(final ExternalGroup input) {
        ExternalGroup group = input;
        if (externalGroupHelper.isFormattingDisplayName() && externalGroupHelper.isGroupDNContainsDisplayName()) {
            // setting as displayName the id group formatted
            group.setDisplayName(format(input.getId()));
        } else if (externalGroupHelper.isFormattingDisplayName()) {
            // setting as new displayName the origin displayName formatted
            group.setDisplayName(format(input.getDisplayName()));
        }
        log.debug("DisplayNameFormatter renamed {} to {}", input.getDisplayName(), group.getDisplayName());
        return group;
    }

    private String format(String input) {
        if (input != null && !input.isEmpty()) {
            Matcher dnMatcher = externalGroupHelper.getGroupDisplayNameRegex().matcher(input);
            if (dnMatcher.find()) {
                final String displayName = dnMatcher.group(1);
                log.debug("Matcher found group displayName, value is : {}", displayName);
                return displayName;
            }
            log.debug("No displayname formatting will be done as pattern isn't matching !");
        }
        return input;
    }
}
