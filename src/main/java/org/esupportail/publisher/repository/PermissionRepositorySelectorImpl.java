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
package org.esupportail.publisher.repository;

import org.esupportail.publisher.domain.AbstractPermission;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by jgribonvald on 04/11/15.
 */
@Component
public class PermissionRepositorySelectorImpl implements IPermissionRepositorySelector {

    @Inject
    @Named("permissionRepository")
    private PermissionRepository<AbstractPermission> permissionDao;

    @Inject
    private PermissionOnContextRepository permissionOnCtxDao;

    @Inject
    private PermOnClassifWithSubjectsRepository permissionOnCtxWSubjDao;

    @Inject
    private PermissionOnSubjectsRepository permissionOnSubj;

    @Inject
    private PermOnSubjectsWithClassifsRepository permissionOnSubjWClassifsDao;


    public PermissionRepositorySelectorImpl() {
    }

    @Override
    public PermissionRepository getPermissionDao(final PermissionClass type) {
        if (type == null) return permissionDao;
        switch (type) {
            case CONTEXT: return permissionOnCtxDao;
            case CONTEXT_WITH_SUBJECTS: return permissionOnCtxWSubjDao;
            case SUBJECT: return permissionOnSubj;
            case SUBJECT_WITH_CONTEXT: return permissionOnSubjWClassifsDao;
        }
        return permissionDao;
    }
}
