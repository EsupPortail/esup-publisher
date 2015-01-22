/**
 *
 */
package org.esupportail.publisher.domain.externals;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GIP RECIA - Julien Gribonvald 11 juil. 2014
 */
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ExternalUser implements IExternalUser {

	private String id;

	private String displayName;

	private String email;

	private Map<String, List<String>> attributes = new HashMap<String, List<String>>();

    public ExternalUser() {
    }

    @Override
	public List<String> getAttribute(final String attr) {
		if (attributes.containsKey(attr))
			return attributes.get(attr);
        else if (attributes.containsKey(attr.toLowerCase()))
            return attributes.get(attr.toLowerCase());
		return null;
	}

    public String getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getEmail() {
        return this.email;
    }

    public Map<String, List<String>> getAttributes() {
        return this.attributes;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ExternalUser)) return false;
        final ExternalUser other = (ExternalUser) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.id;
        final Object other$id = other.id;
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$displayName = this.displayName;
        final Object other$displayName = other.displayName;
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName))
            return false;
        final Object this$email = this.email;
        final Object other$email = other.email;
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
        final Object this$attributes = this.attributes;
        final Object other$attributes = other.attributes;
        if (this$attributes == null ? other$attributes != null : !this$attributes.equals(other$attributes))
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
        final Object $email = this.email;
        result = result * PRIME + ($email == null ? 0 : $email.hashCode());
        final Object $attributes = this.attributes;
        result = result * PRIME + ($attributes == null ? 0 : $attributes.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ExternalUser;
    }

    public String toString() {
        return "org.esupportail.publisher.domain.externals.ExternalUser(id=" + this.id + ", displayName=" + this.displayName + ", email=" + this.email + ", attributes=" + this.attributes + ")";
    }
}
