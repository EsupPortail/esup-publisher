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
package org.esupportail.publisher.service;

import com.google.common.collect.Lists;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.web.rest.dto.TreeJS;
import org.esupportail.publisher.web.rest.dto.UserDTO;

import java.util.List;

/**
 * Created by jgribonvald on 11/06/15.
 */
public class GroupServiceEmpty implements IGroupService {
    @Override
    public List<TreeJS> getRootNodes(ContextKey contextKey, List<ContextKey> subContextKeys) {
        return Lists.newArrayList();
    }

    @Override
    public List<TreeJS> getGroupMembers(String id) {
        return Lists.newArrayList();
    }

    @Override
    public List<UserDTO> getUserMembers(String id) {
        return Lists.newArrayList();
    }
}
