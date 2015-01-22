/**
 *
 */
package org.esupportail.publisher.domain.externals;

import com.google.common.collect.Lists;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GIP RECIA - Julien Gribonvald 11 juil. 2014
 */
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ExternalGroup implements IExternalGroup {

    private String id;
    private String displayName;

    private Map<String, List<String>> attributes = new HashMap<String, List<String>>();

    private List<String> groupMembers = Lists.newArrayList();
    private List<String> userMembers = Lists.newArrayList();

    public ExternalGroup(String id, String displayName, Map<String, List<String>> attributes ) {
        this.id = id;
        this.displayName = displayName;
        this.attributes = attributes;
    }

    public ExternalGroup() {
        super();
    }

    @Override
    public List<String> getAttribute(final String attr) {
        if (attributes.containsKey(attr))
            return attributes.get(attr);
        else if (attributes.containsKey(attr.toLowerCase()))
            return attributes.get(attr.toLowerCase());
        return null;
    }


    public boolean hasMembers() {
        return !groupMembers.isEmpty() || !userMembers.isEmpty();
    }

    @Override
    public SubjectKey getSubject() {
        return new SubjectKey(this.getId(), SubjectType.GROUP);
    }

    public String getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Map<String, List<String>> getAttributes() {
        return this.attributes;
    }

    public List<String> getGroupMembers() {
        return this.groupMembers;
    }

    public List<String> getUserMembers() {
        return this.userMembers;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }

    public void setGroupMembers(List<String> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public void setUserMembers(List<String> userMembers) {
        this.userMembers = userMembers;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ExternalGroup)) return false;
        final ExternalGroup other = (ExternalGroup) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.id;
        final Object other$id = other.id;
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$displayName = this.displayName;
        final Object other$displayName = other.displayName;
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName))
            return false;
        final Object this$attributes = this.attributes;
        final Object other$attributes = other.attributes;
        if (this$attributes == null ? other$attributes != null : !this$attributes.equals(other$attributes))
            return false;
        final Object this$groupMembers = this.groupMembers;
        final Object other$groupMembers = other.groupMembers;
        if (this$groupMembers == null ? other$groupMembers != null : !this$groupMembers.equals(other$groupMembers))
            return false;
        final Object this$userMembers = this.userMembers;
        final Object other$userMembers = other.userMembers;
        if (this$userMembers == null ? other$userMembers != null : !this$userMembers.equals(other$userMembers))
            return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.id;
        result = result * PRIME + ($id == null ? 0 : $id.hashCode());
        final Object $displayName = this.displayName;
        result = result * PRIME + ($displayName == null ? 0 : $displayName.hashCode());
        final Object $attributes = this.attributes;
        result = result * PRIME + ($attributes == null ? 0 : $attributes.hashCode());
        final Object $groupMembers = this.groupMembers;
        result = result * PRIME + ($groupMembers == null ? 0 : $groupMembers.hashCode());
        final Object $userMembers = this.userMembers;
        result = result * PRIME + ($userMembers == null ? 0 : $userMembers.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ExternalGroup;
    }

    public String toString() {
        return "org.esupportail.publisher.domain.externals.ExternalGroup(id=" + this.id + ", displayName=" + this.displayName + ", attributes=" + this.attributes + ", groupMembers=" + this.groupMembers + ", userMembers=" + this.userMembers + ")";
    }
}
