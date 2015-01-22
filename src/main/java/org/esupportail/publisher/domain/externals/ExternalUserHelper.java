package org.esupportail.publisher.domain.externals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

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
