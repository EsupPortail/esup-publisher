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
package org.esupportail.publisher.domain.externals;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalGroupHelper {

    /**
     * The uid ldap attribute name.
     */
    private String groupIdAttribute;
    /**
     * The display name ldap attribute name.
     */
    private String groupDisplayNameAttribute;
    /**
     * The user search attribute name.
     */
    private String groupSearchAttribute;
    private String groupMembersAttribute;
    private Pattern groupKeyMemberRegex;
    private int groupKeyMemberIndex;
    private Pattern userKeyMemberRegex;
    private int userKeyMemberIndex;
    private Pattern groupDisplayNameRegex;
    private boolean groupDNContainsDisplayName;
    private boolean groupResolveUserMember;
    private boolean groupResolveUserMemberByUserAttributes;
    private Pattern groupsPatternWithoutMembersResolving;
    /**
     * The other attributes desired to show, facultative
     */
    private Set<String> otherGroupDisplayedAttributes;
    private String groupDNSubPath;

    public Set<String> getAttributes() {
        Set<String> set = new HashSet<>(otherGroupDisplayedAttributes);
        set.add(groupIdAttribute);
        set.add(groupDisplayNameAttribute);
        set.add(groupSearchAttribute);
        set.add(groupMembersAttribute);
        return set;
    }

    // used to tell if use match or get group of pattern
    public boolean isFormattingDisplayName() {
        return groupDisplayNameRegex != null;
    }

    // used to tell if use match or get group of pattern
    public boolean isExtractGroupMembers() {
        return groupKeyMemberIndex > 0;
    }

    // used to tell if use match or get group of pattern
    public boolean isExtractUserMembers() {
        return userKeyMemberIndex > 0;
    }
}