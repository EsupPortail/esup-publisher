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
package org.esupportail.publisher.config.bean;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.naming.directory.SearchControls;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class CustomLdapProperties {

    @NotNull
    private ContextSourceProperties contextSource = new ContextSourceProperties();
    @NotNull
    private LdapTemplateProperties ldapTemplate = new LdapTemplateProperties();
    @NotNull
    private BranchProperties userBranch = new BranchProperties();
    @Nullable
    private GroupBranchProperties groupBranch = new GroupBranchProperties();

    @Data
    @Validated
    public static class ContextSourceProperties {
        @NotEmpty
        private String[] urls;
        @NotBlank
        private String base;
        @NotBlank
        private String username;
        @NotBlank
        private String password;
        private boolean anonymousReadOnly = false;
        private boolean nativePooling = false;

        @Override
        public String toString() {
            return "{\n\"ContextSourceProperties\":{"
                    + "\n \" urls\":" + Arrays.stream(urls)
                    .map(String::valueOf)
                    .collect(Collectors.joining("\",\"", "[\"", "\"]"))
                    + ",\n \"base\":\"" + base + "\""
                    + ",\n \"username\":\"" + username + "\""
                    + ",\n \"password\":\"*******\""
                    + ",\n \"anonymousReadOnly\":\"" + anonymousReadOnly + "\""
                    + ",\n \"nativePooling\":\"" + nativePooling + "\""
                    + "\n}\n}";
        }
    }

    @Data
    public static class LdapTemplateProperties {
        private boolean ignorePartialResultException = false;
        private boolean ignoreNameNotFoundException = false;
        private boolean ignoreSizeLimitExceededException = true;
        private int searchScope = SearchControls.SUBTREE_SCOPE;
        private int timeLimit = 0;
        private int countLimit = 0;

        @Override
        public String toString() {
            return "{\n\"LdapTemplateProperties\":{"
                    + "\n \"ignorePartialResultException\":\"" + ignorePartialResultException + "\""
                    + ",\n \"ignoreNameNotFoundException\":\"" + ignoreNameNotFoundException + "\""
                    + ",\n \"ignoreSizeLimitExceededException\":\"" + ignoreSizeLimitExceededException + "\""
                    + ",\n \"searchScope\":\"" + searchScope + "\""
                    + ",\n \"timeLimit\":\"" + timeLimit + "\""
                    + ",\n \"countLimit\":\"" + countLimit + "\""
                    + "\n}\n}";
        }
    }

    @Data
    @Validated
    public static class BranchProperties {
        @NotBlank
        private String baseDN = "ou=people";
        @NotBlank
        private String idAttribute = "uid";
        @NotBlank
        private String displayNameAttribute = "displayName";
        @NotBlank
        private String mailAttribute = "mail";
        @NotBlank
        private String searchAttribute = "cn";
        @NotBlank
        private String groupAttribute = "isMemberOf";
        @NotNull
        private Set<String> otherDisplayedAttributes = new HashSet<>();
        @NotNull
        private Set<String> otherBackendAttributes = new HashSet<>();

        @Override
        public String toString() {
            return "{\n\"branchProperties\":{"
                    + "\n \"baseDN\":\"" + baseDN + "\""
                    + ",\n \"idAttribute\":\"" + idAttribute + "\""
                    + ",\n \"displayNameAttribute\":\"" + displayNameAttribute + "\""
                    + ",\n \"mailAttribute\":\"" + mailAttribute + "\""
                    + ",\n \"searchAttribute\":\"" + searchAttribute + "\""
                    + ",\n \"groupAttribute\":\"" + groupAttribute + "\""
                    + ",\n \"otherDisplayedAttributes\":" + (otherDisplayedAttributes != null ? otherDisplayedAttributes.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining("\",\"", "[\"", "\"]")) : null)
                    + ",\n \"otherBackendAttributes\":" + (otherBackendAttributes != null ? otherBackendAttributes.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining("\",\"", "[\"", "\"]")) : null)
                    + "\n}\n}";
        }
    }

    @Getter
    @Setter
    @Validated
    public static class GroupBranchProperties extends BranchProperties {

        @NotNull
        private Pattern groupMemberKeyPattern;
        private int groupMemberKeyPatternIndex = 0;
        @NotNull
        private Pattern userMemberKeyPattern;
        private int userMemberKeyPatternIndex = 0;
        private Pattern groupDisplayNamePattern;
        private boolean DNContainsDisplayName = false;
        private boolean resolveUserMembers = false;
        private boolean resolveUserMembersByUserAttributes = true;

        private List<GroupDesignerProperties> designers;

        private List<GroupRegexProperties> nameFormatters;

        private Pattern dontResolveMembersWithGroupPattern;

        public GroupBranchProperties() {
            this.setBaseDN("ou=groups");
            this.setGroupAttribute("member");
            this.setIdAttribute("cn");
            this.setDisplayNameAttribute("cn");
        }

        @Override
        public String toString() {
            return "{\n\"GroupBranchProperties\":"
                    + super.toString()
                    + ",\n \"groupMemberKeyPattern\":\"" + groupMemberKeyPattern + "\""
                    + ",\n \"groupMemberKeyPatternIndex\":\"" + groupMemberKeyPatternIndex + "\""
                    + ",\n \"userMemberKeyPattern\":\"" + userMemberKeyPattern + "\""
                    + ",\n \"userMemberKeyPatternIndex\":\"" + userMemberKeyPatternIndex + "\""
                    + ",\n \"groupDisplayNamePattern\":\"" + groupDisplayNamePattern + "\""
                    + ",\n \"DNContainsDisplayName\":\"" + DNContainsDisplayName + "\""
                    + ",\n \"resolveUserMembers\":\"" + resolveUserMembers + "\""
                    + ",\n \"resolveUserMembersByUserAttributes\":\"" + resolveUserMembersByUserAttributes + "\""
                    + ",\n \"designers\":" + designers.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(",", "[", "]"))
                    + ",\n \"nameFormatters\":" + nameFormatters.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(",", "[", "]"))
                    + ",\n \"dontResolveMembersWithGroupPattern\":\"" + dontResolveMembersWithGroupPattern + "\""
                    + "\n}";
        }
    }

    @Override
    public String toString() {
        return "{\n\"CustomLdapProperties\":{"
                + "\n \"contextSource\":" + contextSource
                + ",\n \"ldapTemplate\":" + ldapTemplate
                + ",\n \"userBranch\":" + userBranch
                + ",\n \"groupBranch\":" + groupBranch
                + "\n}\n}";
    }
}