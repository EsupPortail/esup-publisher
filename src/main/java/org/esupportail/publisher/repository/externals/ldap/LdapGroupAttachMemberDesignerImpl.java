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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.repository.externals.IExternalGroupDao;
import org.esupportail.publisher.repository.externals.IGroupMemberDesigner;

/**
 * Created by jgribonvald on 11/06/15.
 * Conf that permit to attach a group (tree) not provided by root ldap search on tree group the specified group and his members
 */
@ToString
@Slf4j
public class LdapGroupAttachMemberDesignerImpl implements IGroupMemberDesigner {

    @NotNull
    private ExternalGroupHelper externalGroupHelper;
    @NotNull
    private String groupAttachEndMatch;
    @NotEmpty
    private List<String> groupToAttachEndPattern;

    private Pattern patternGroupIntoAttach;

    public LdapGroupAttachMemberDesignerImpl(@NotNull final ExternalGroupHelper externalGroupHelper, @NotNull final String groupRootPattern,
                                             @NotNull final String groupAttachEndMatch, @NotEmpty final List<String> groupToAttachEndPattern) {
        this.externalGroupHelper = externalGroupHelper;
        this.groupAttachEndMatch = groupAttachEndMatch;
        this.groupToAttachEndPattern = groupToAttachEndPattern;
        this.patternGroupIntoAttach = Pattern.compile(groupRootPattern + this.groupAttachEndMatch);
    }

    public IExternalGroup designe(IExternalGroup group, final IExternalGroupDao externalGroupDao) {
        if (group == null) return null;

        Matcher profMatcher = patternGroupIntoAttach.matcher(group.getId());
        log.debug("Design for group id {} with matcher {}, and is matching {}", group.getId(), profMatcher.toString(), profMatcher.matches());
        if (profMatcher.matches()) {
            String filter = "(|";
            for (String endPattern: groupToAttachEndPattern) {
                filter += "(" + externalGroupHelper.getGroupSearchAttribute() + "=" + group.getId().replace(groupAttachEndMatch, endPattern) + ")";
            }
            filter += ")";
            log.debug(" ldap filter that will be used : {}", filter);
            final List<IExternalGroup> members = externalGroupDao.getGroupsWithFilter(filter, null, false);
            if (members!=null) {
                for (IExternalGroup externalGroup : members) {
                    log.debug("Designer adding to {} the member {}", group.getId(),externalGroup.getId());
                    group.getGroupMembers().add(externalGroup.getId());
                }
            }
        }
        return group;
    }

    @Override
    public boolean isDesignerMatchGroup(String groupId) {
        if (groupId != null && !groupId.isEmpty()) {
           return patternGroupIntoAttach.matcher(groupId).matches();
        }
        return false;
    }

    @PostConstruct
    public void debug() {
        log.debug("Configuration du bean: {}", this.toString());
    }
}
