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
package org.esupportail.publisher.repository.externals;

import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalUser;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 11 juil. 2014
 */
public interface IExternalGroupDao {

    IExternalGroup getGroupById(@NotNull final String id, final boolean withMembers);

    List<IExternalGroup> getGroupsById(@NotNull final Collection<String> id, final boolean withMembers);

    List<IExternalGroup> getGroupsByIdStartWith(@NotNull final Collection<String> ids, final boolean withMembers);

    List<IExternalGroup> getDirectGroupMembers(@NotNull final String id, final boolean withMembers);

    List<IExternalGroup> getGroupsWithFilter(@NotNull final String stringFilter, final String token, final boolean withMembers);

    List<IExternalUser> getDirectUserMembers(@NotNull final String id);

    boolean isGroupMemberOfGroup(@NotNull final String member, @NotNull final String parent);

    boolean isGroupMemberOfGroupFilter(@NotNull String stringFilter, @NotNull final String member);

    boolean isGroupMemberOfAtLeastOneGroup(@NotNull final String member, @NotNull final Iterable<String> parents);

    boolean isUserMemberOfGroup(@NotNull final String uid, @NotNull final String group);

    boolean isUserMemberOfAtLeastOneGroup(@NotNull final String uid, @NotNull final Iterable<String> groups);

}
