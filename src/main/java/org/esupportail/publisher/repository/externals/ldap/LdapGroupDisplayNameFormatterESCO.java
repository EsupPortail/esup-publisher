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

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.externals.ExternalGroup;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
import org.esupportail.publisher.domain.externals.IExternalGroupDisplayNameFormatter;
import org.springframework.util.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jgribonvald on 04/06/15.
 */
@Slf4j
public class LdapGroupDisplayNameFormatterESCO implements IExternalGroupDisplayNameFormatter {

    private static Pattern grpApplication;
    private static Pattern grpNameApplicationRegex;
    private static int[] grpNameApplicationIndex;

    private ExternalGroupHelper externalGroupHelper;

    public LdapGroupDisplayNameFormatterESCO(final ExternalGroupHelper externalGroupHelper, final String groupApplicationNamePattern, final String groupApplicationNameRegex, final String strIndex) {
        Assert.hasText(groupApplicationNamePattern, "You should provide an Application Pattern to format Groups Name (bean alternative should be used if you doesn't want to use this one) !");
        Assert.hasText(groupApplicationNameRegex, "You should provide an Application Group Matcher to format Groups Name (bean alternative should be used if you doesn't want to use this one) !");
        Assert.hasText(strIndex, "You should provide an Application Group Index to format Groups Name (bean alternative should be used if you doesn't want to use this one) !");
        this.externalGroupHelper = externalGroupHelper;
        LdapGroupDisplayNameFormatterESCO.grpApplication = Pattern.compile(groupApplicationNamePattern);
        LdapGroupDisplayNameFormatterESCO.grpNameApplicationRegex = Pattern.compile(groupApplicationNameRegex);
        String[] indexes = strIndex.split(",");
        LdapGroupDisplayNameFormatterESCO.grpNameApplicationIndex = new int[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            LdapGroupDisplayNameFormatterESCO.grpNameApplicationIndex[i] = Integer.valueOf(indexes[i]);
        }
        Assert.isTrue(LdapGroupDisplayNameFormatterESCO.grpNameApplicationIndex.length > 0,
            "You should provide an Application Group Index to format Groups Name (bean alternative should be used if you doesn't want to use this one) !");
    }

    @Override
    public ExternalGroup format(ExternalGroup input) {
        if (externalGroupHelper.isFormattingDisplayName() && externalGroupHelper.isGroupDNContainsDisplayName()) {
            // setting as displayName the id group formatted
            input.setDisplayName(format(input.getId()));
        } else if (externalGroupHelper.isFormattingDisplayName()) {
            // setting as new displayName the origin displayName formatted
            input.setDisplayName(format(input.getDisplayName()));
        }
        return input;
    }

    private String format(String input) {
        if (input != null && !input.isEmpty()) {
            Matcher appMatcher = LdapGroupDisplayNameFormatterESCO.grpApplication.matcher(input);
            if (appMatcher.matches()){
                Matcher appGroup = LdapGroupDisplayNameFormatterESCO.grpNameApplicationRegex.matcher(input);
                if (appGroup.find()) {
                    String displayName = "";
                    for (int i = 0; i < LdapGroupDisplayNameFormatterESCO.grpNameApplicationIndex.length; i++) {
                        if (i > 0) {
                            displayName += ":" + appGroup.group(LdapGroupDisplayNameFormatterESCO.grpNameApplicationIndex[i]);
                        } else {
                            displayName += appGroup.group(LdapGroupDisplayNameFormatterESCO.grpNameApplicationIndex[i]);
                        }
                    }
                    log.debug("Matcher Application found group displayName, value is : {}", displayName);
                    return displayName.replaceFirst("Tous_","").replaceAll("_", " ");
                }
            }

            Matcher dnMatcher = externalGroupHelper.getGroupDisplayNameRegex().matcher(input);
            if (dnMatcher.find()) {
                final String displayName = dnMatcher.group(1);
                log.debug("Matcher found group displayName, value is : {}", displayName);
                return displayName.replaceFirst("Tous_","").replaceAll("_", " ");
            }
            log.warn("No displayname formatting will be done as pattern isn't matching !");
        }
        return input;
    }
}
