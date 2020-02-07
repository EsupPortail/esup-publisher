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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.naming.NamingException;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.externals.ExternalGroup;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalGroupDisplayNameFormatter;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author GIP RECIA - Julien Gribonvald 3 juin. 2015
 */
@Slf4j
public class LdapGroupContextMapper implements ContextMapper<IExternalGroup> {

	private ExternalGroupHelper externalGroupHelper;

    private List<IExternalGroupDisplayNameFormatter> groupDisplayNameFormatters;

	/**
	 * @param externalGroupHelper
	 */
	public LdapGroupContextMapper(ExternalGroupHelper externalGroupHelper, List<IExternalGroupDisplayNameFormatter> groupDisplayNameFormatters) {
		super();
		this.externalGroupHelper = externalGroupHelper;
        this.groupDisplayNameFormatters = groupDisplayNameFormatters;
	}

	@Override
	public ExternalGroup mapFromContext(Object ctx) throws NamingException {
		Assert.notNull(externalGroupHelper, "The externalGroupHelper should not be null !");
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
                if (StringUtils.hasText(mbr)) {
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
                        log.error("This value '{}' doesn't match patterns defined to identify user or group member of group '{}'", mbr, group.getId());
                    }
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

        for (IExternalGroupDisplayNameFormatter formatter: groupDisplayNameFormatters){
            group = formatter.format(group);
        }

		return group;
	}

	public ExternalGroupHelper getLdapGroupHelper() {
		return externalGroupHelper;
	}

	public void setLdapGroupHelper(ExternalGroupHelper externalGroupHelper) {
		this.externalGroupHelper = externalGroupHelper;
	}
}
