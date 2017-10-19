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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalUserHelper {

    /**
     * The uid ldap attribute name.
     */
    //@Value("${ldap.userId:uid}")
    private String userIdAttribute;

    /**
     * The display name ldap attribute name.
     */
    //@Value("${ldap.userDisplayNameAttribute:displayName}")
    private String userDisplayNameAttribute;

    /**
     * The email ldap attribute name.
     */
    //@Value("${ldap.userEmailAttribute:mail}")
    private String userEmailAttribute;

    /**
     * The user search attribute name.
     */
    //@Value("${ldap.userSearchAttribute:cn}")
    private String userSearchAttribute;

    //@Value("${ldap.userGroupAttribute:isMemberOf}")
    private String userGroupAttribute;

    /**
     * The other attributes desired for fonctional use, facultative
     */
    private Set<String> otherUserAttributes;

    /**
     * The other attributes desired to show, facultative
     */
    //@Value("${ldap.otherUserDisplayedAttributes:#{null}}")
    private Set<String> otherUserDisplayedAttributes;

    //@Value("${ldap.DNsubpath.people:ou=people}")
    private String userDNSubPath;

    public Set<String> getAttributes() {
        Set<String> set = new HashSet<String>();
        set.addAll(otherUserDisplayedAttributes);
        set.add(userIdAttribute);
        set.add(userDisplayNameAttribute);
        set.add(userEmailAttribute);
        set.add(userSearchAttribute);
        set.add(userGroupAttribute);
        set.addAll(otherUserAttributes);
        return set;
    }

}
