package org.esupportail.publisher.repository.externals.ldap;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.externals.*;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.util.Assert;

import javax.naming.NamingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author GIP RECIA - Julien Gribonvald 3 juin. 2015
 */
@Slf4j
public class LdapGroupContextMapper implements ContextMapper<IExternalGroup> {

	private ExternalGroupHelper externalGroupHelper;

    private IExternalGroupDisplayNameFormatter groupDisplayNameFormatter;

	/**
	 * @param externalGroupHelper
	 */
	public LdapGroupContextMapper(ExternalGroupHelper externalGroupHelper, IExternalGroupDisplayNameFormatter groupDisplayNameFormatter) {
		super();
		this.externalGroupHelper = externalGroupHelper;
        this.groupDisplayNameFormatter = groupDisplayNameFormatter;
	}

	@Override
	public ExternalGroup mapFromContext(Object ctx) throws NamingException {
		Assert.notNull(externalGroupHelper);
		DirContextAdapter context = (DirContextAdapter) ctx;
		Map<String, List<String>> attrs = new HashMap<String, List<String>>();
		ExternalGroup group = new ExternalGroup();

		group.setId(context.getStringAttribute(externalGroupHelper.getGroupIdAttribute()));
		attrs.put(externalGroupHelper.getGroupIdAttribute(),Lists.newArrayList(group.getId()));

        group.setDisplayName(context.getStringAttribute(externalGroupHelper.getGroupDisplayNameAttribute()));
        attrs.put(externalGroupHelper.getGroupDisplayNameAttribute(),
            Lists.newArrayList(context.getStringAttribute(externalGroupHelper.getGroupDisplayNameAttribute())));

        List<String> members = Lists.newArrayList(context.getStringAttributes(externalGroupHelper.getGroupMembersAttribute()));
        List<String> filteredMembers = Lists.newArrayList();
        if ((externalGroupHelper.isExtractGroupMembers() || externalGroupHelper.isExtractUserMembers()) && !members.isEmpty()) {
            for (String mbr : members) {
                Matcher mgroup = externalGroupHelper.getGroupKeyMemberRegex().matcher(mbr);
                Matcher muser = externalGroupHelper.getUserKeyMemberRegex().matcher(mbr);
                if (externalGroupHelper.isExtractGroupMembers() && mgroup.find()) {
                    String id = mgroup.group(externalGroupHelper.getGroupKeyMemberIndex());
                    log.debug("Matcher found group id, value is : {}", id);
                    group.getGroupMembers().add(id);
                    filteredMembers.add(mbr);
                } else if (mgroup.matches()) {
                    log.debug("Matcher match Group, value is : {}", mbr);
                    group.getGroupMembers().add(mbr);
                    filteredMembers.add(mbr);
                } else if (externalGroupHelper.isGroupResolveUserMember() && externalGroupHelper.isExtractUserMembers() && muser.find()) {
                    String id = muser.group(externalGroupHelper.getUserKeyMemberIndex());
                    log.debug("Matcher found user id, value is : {}", id);
                    group.getUserMembers().add(id);
                    filteredMembers.add(mbr);
                } else if (externalGroupHelper.isGroupResolveUserMember() && muser.matches()) {
                    log.debug("Matcher match Group, value is : {}", mbr);
                    group.getUserMembers().add(mbr);
                    filteredMembers.add(mbr);
                } else if (!externalGroupHelper.isGroupResolveUserMember()) {
                    log.debug("Configuration is set to doesn't resolve user members !");
                } else {
                    log.error("This value {} doesn't match patterns defined to identify user or group member", mbr);
                }
            }
        }
        attrs.put(externalGroupHelper.getGroupMembersAttribute(), filteredMembers);

		if (externalGroupHelper.getOtherGroupDisplayedAttributes() != null
				&& !externalGroupHelper.getOtherGroupDisplayedAttributes().isEmpty()) {
			for (String attr : externalGroupHelper.getOtherGroupDisplayedAttributes()) {
				if (context.attributeExists(attr)) {
					attrs.put(attr.toLowerCase(), Arrays.asList(context.getStringAttributes(attr)));
				}
			}
		}
		group.setAttributes(attrs);

        group = groupDisplayNameFormatter.format(group);

		return group;
	}

	public ExternalGroupHelper getLdapGroupHelper() {
		return externalGroupHelper;
	}

	public void setLdapGroupHelper(ExternalGroupHelper externalGroupHelper) {
		this.externalGroupHelper = externalGroupHelper;
	}
}
