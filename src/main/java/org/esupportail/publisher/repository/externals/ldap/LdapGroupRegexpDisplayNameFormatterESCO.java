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

import org.esupportail.publisher.domain.externals.ExternalGroup;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
import org.esupportail.publisher.domain.externals.IExternalGroupDisplayNameFormatter;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Created by jgribonvald on 04/06/15.
 */
@ToString(of = {"grpPattern","grpNameRegex","grpNameIndex","grpSeparator"})
@Slf4j
public class LdapGroupRegexpDisplayNameFormatterESCO implements IExternalGroupDisplayNameFormatter {

    private final Pattern grpPattern;
    private final Pattern grpNameRegex;
    private final int[] grpNameIndex;
    private String grpSeparator = "";
    private String grpSuffixe = "";

    private final ExternalGroupHelper externalGroupHelper;

    public LdapGroupRegexpDisplayNameFormatterESCO(final ExternalGroupHelper externalGroupHelper, final String groupNamePattern,
                                                   final String groupNameRegex, final String strIndex, final String strSeparator, final String suffixAppend) {
        Assert.hasText(groupNamePattern, "You should provide a Pattern to format Groups Name (bean alternative should be used if you doesn't want to use this one) !");
        Assert.hasText(groupNameRegex, "You should provide a Group Matcher to format Groups Name (bean alternative should be used if you doesn't want to use this one) !");
        Assert.hasText(strIndex, "You should provide a Group Index to format Groups Name (bean alternative should be used if you doesn't want to use this one) !");
        this.externalGroupHelper = externalGroupHelper;
        this.grpPattern = Pattern.compile(groupNamePattern);
        this.grpNameRegex = Pattern.compile(groupNameRegex);
        String[] indexes = strIndex.split(",");
        this.grpNameIndex = new int[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            this.grpNameIndex[i] = Integer.parseInt(indexes[i]);
        }
        Assert.isTrue(this.grpNameIndex.length > 0,
            "You should provide a Group Index to format Groups Name (bean alternative should be used if you doesn't want to use this one) !");
        if (StringUtils.hasLength(strSeparator)) {
            this.grpSeparator = strSeparator;
        }
        if (StringUtils.hasLength(suffixAppend)) {
            this.grpSuffixe = suffixAppend;
        }
    }

    @Override
    public ExternalGroup format(final ExternalGroup input) {
        ExternalGroup group = input;
        String formatted = null;
        if (externalGroupHelper.isFormattingDisplayName() && externalGroupHelper.isGroupDNContainsDisplayName()) {
            // setting as displayName the id group formatted
            formatted = format(input.getId());
        } else if (externalGroupHelper.isFormattingDisplayName()) {
            // setting as new displayName the origin displayName formatted
            formatted = format(input.getDisplayName());
        }

        if (formatted != null) {
            group.setDisplayName(formatted);
        }
        log.debug("DisplayNameFormatter renamed {} to {}", input.getDisplayName(), group.getDisplayName());
        return group;
    }

    private String format(String input) {
        if (input != null && !input.isEmpty()) {
            Matcher matcher = this.grpPattern.matcher(input);
            if (matcher.matches()){
                Matcher group = this.grpNameRegex.matcher(input);
                if (group.find()) {
                    StringBuilder displayName = new StringBuilder();
                    for (int i = 0; i < this.grpNameIndex.length; i++) {
                        if (i > 0) {
                            displayName.append(this.grpSeparator).append(group.group(this.grpNameIndex[i]));
                        } else {
                            displayName.append(group.group(this.grpNameIndex[i]));
                        }
                    }
                    displayName.append(this.grpSuffixe);
                    log.debug("Matcher found group displayName, value is : {}", displayName);
                    return displayName.toString();
                }
            }
            log.debug("No displayname formatting will be done as pattern isn't matching !");
        }
        return null;
    }
}