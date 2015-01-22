package org.esupportail.publisher.domain.externals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalGroupHelper {

    /**
     * The uid ldap attribute name.
     */
    //@Value("${ldap.groupId:cn}")
    private String groupIdAttribute;

    /**
     * The display name ldap attribute name.
     */
    //@Value("${ldap.groupDisplayNameAttribute:cn}")
    private String groupDisplayNameAttribute;

    /**
     * The user search attribute name.
     */
    //@Value("${ldap.groupSearchAttribute:cn}")
    private String groupSearchAttribute;

    //@Value("${ldap.groupMembersAttribute:member}")
    private String groupMembersAttribute;

    //@Value("${ldap.groupKeyMembersRegex:#{null}}")
    private Pattern groupKeyMemberRegex;
    //@Value("${ldap.groupKeyMemberIndex:0}")
    private int groupKeyMemberIndex;
    //@Value("${ldap.userKeyMemberRegex:#{null}}")
    private Pattern userKeyMemberRegex;
    //@Value("${ldap.userKeyMemberIndex:0}")
    private int userKeyMemberIndex;
    //@Value("${ldap.groupDisplayNameRegex:#{null}}")
    private Pattern groupDisplayNameRegex;
    //@Value("${ldap.groupDNContainsDisplayName:false}")
    private boolean groupDNContainsDisplayName;
    //@Value("${ldap.groupResolveUserMember:false}")
    private boolean groupResolveUserMember;
    //@Value("${ldap.groupResolveUserMemberByUserAttributes:true}")
    private boolean groupResolveUserMemberByUserAttributes;


    /**
     * The other attributes desired to show, facultative
     */
    //@Value("${ldap.otherUserDisplayedAttributes:#{null}}")
    private Set<String> otherGroupDisplayedAttributes;

    //@Value("${ldap.DNsubpath.group:ou=groups}")
    private String groupDNSubPath;

    public Set<String> getAttributes() {
        Set<String> set = new HashSet<String>();
        set.addAll(otherGroupDisplayedAttributes);
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
