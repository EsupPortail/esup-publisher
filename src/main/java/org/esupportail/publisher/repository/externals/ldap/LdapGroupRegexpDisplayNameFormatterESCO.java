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
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.externals.ExternalGroup;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
import org.esupportail.publisher.domain.externals.IExternalGroupDisplayNameFormatter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Created by jgribonvald on 04/06/15.
 */
@Slf4j
public class LdapGroupRegexpDisplayNameFormatterESCO implements IExternalGroupDisplayNameFormatter {

    private static Pattern grpPattern;
    private static Pattern grpNameRegex;
    private static int[] grpNameIndex;
    private static String grpSeparator = "";

    private ExternalGroupHelper externalGroupHelper;

    public LdapGroupRegexpDisplayNameFormatterESCO(final ExternalGroupHelper externalGroupHelper, final String groupNamePattern,
                                                   final String groupNameRegex, final String strIndex, final String strSeparator) {
        Assert.hasText(groupNamePattern, "You should provide a Pattern to format Groups Name (bean alternative should be used if you doesn't want to use this one) !");
        Assert.hasText(groupNameRegex, "You should provide a Group Matcher to format Groups Name (bean alternative should be used if you doesn't want to use this one) !");
        Assert.hasText(strIndex, "You should provide a Group Index to format Groups Name (bean alternative should be used if you doesn't want to use this one) !");
        this.externalGroupHelper = externalGroupHelper;
        LdapGroupRegexpDisplayNameFormatterESCO.grpPattern = Pattern.compile(groupNamePattern);
        LdapGroupRegexpDisplayNameFormatterESCO.grpNameRegex = Pattern.compile(groupNameRegex);
        String[] indexes = strIndex.split(",");
        LdapGroupRegexpDisplayNameFormatterESCO.grpNameIndex = new int[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            LdapGroupRegexpDisplayNameFormatterESCO.grpNameIndex[i] = Integer.valueOf(indexes[i]);
        }
        Assert.isTrue(LdapGroupRegexpDisplayNameFormatterESCO.grpNameIndex.length > 0,
            "You should provide a Group Index to format Groups Name (bean alternative should be used if you doesn't want to use this one) !");
        if (StringUtils.hasLength(strSeparator)) {
            LdapGroupRegexpDisplayNameFormatterESCO.grpSeparator = strSeparator;
        }
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
            Matcher matcher = LdapGroupRegexpDisplayNameFormatterESCO.grpPattern.matcher(input);
            if (matcher.matches()){
                Matcher group = LdapGroupRegexpDisplayNameFormatterESCO.grpNameRegex.matcher(input);
                if (group.find()) {
                    String displayName = "";
                    for (int i = 0; i < LdapGroupRegexpDisplayNameFormatterESCO.grpNameIndex.length; i++) {
                        if (i > 0) {
                            displayName += LdapGroupRegexpDisplayNameFormatterESCO.grpSeparator
                                + group.group(LdapGroupRegexpDisplayNameFormatterESCO.grpNameIndex[i]);
                        } else {
                            displayName += group.group(LdapGroupRegexpDisplayNameFormatterESCO.grpNameIndex[i]);
                        }
                    }
                    log.debug("Matcher found group displayName, value is : {}", displayName);
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
