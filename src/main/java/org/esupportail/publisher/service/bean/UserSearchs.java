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
/**
 *
 */
package org.esupportail.publisher.service.bean;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author GIP RECIA - Julien Gribonvald
 *
 *         Should be used as session bean
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class UserSearchs {

    private List<IExternalUser> lastSearchOnUsers;
    private List<UserDTO> lastConvertedSearchOnUsers;
    private List<SubjectKey> lastUserKeySearchOnUsers;

    private List<IExternalGroup> lastSearchOnGroups;
    //private List<GroupDTO> lastConvertedSearchOnGroups;
    private List<SubjectKey> lastGroupKeySearchOnUsers;

}
