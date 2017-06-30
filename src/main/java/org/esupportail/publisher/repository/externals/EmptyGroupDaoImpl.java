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
 * Created by jgribonvald on 11/06/15.
 */
public class EmptyGroupDaoImpl implements IExternalGroupDao {
    @Override
    public IExternalGroup getGroupById(@NotNull String id, boolean withMembers) {
        return null;
    }

    @Override
    public List<IExternalGroup> getGroupsById(@NotNull Collection<String> id, boolean withMembers) {
        return null;
    }

    @Override
    public List<IExternalGroup> getGroupsByIdStartWith(@NotNull Collection<String> id, boolean withMembers) {
        return null;
    }

    @Override
    public List<IExternalGroup> getDirectGroupMembers(@NotNull String id, boolean withMembers) {
        return null;
    }

    @Override
    public List<IExternalGroup> getGroupsWithFilter(@NotNull String stringFilter, String token, boolean withMembers) {
        return null;
    }

    @Override
    public List<IExternalUser> getDirectUserMembers(@NotNull String id) {
        return null;
    }

    @Override
    public boolean isGroupMemberOfGroup(@NotNull String member, @NotNull String parent) {
        return false;
    }

    @Override
    public boolean isGroupMemberOfGroupFilter(@NotNull String stringFilter, @NotNull final String member) { return false;}

    @Override
    public boolean isGroupMemberOfAtLeastOneGroup(@NotNull String member, @NotNull Iterable<String> parents) {
        return false;
    }

    @Override
    public boolean isUserMemberOfGroup(@NotNull String uid, @NotNull String group) {
        return false;
    }

    @Override
    public boolean isUserMemberOfAtLeastOneGroup(@NotNull String uid, @NotNull Iterable<String> groups) {
        return false;
    }


}
