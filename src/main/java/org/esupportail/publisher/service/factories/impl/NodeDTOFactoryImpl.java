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
package org.esupportail.publisher.service.factories.impl;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.IContext;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.factories.NodeDTOFactory;
import org.esupportail.publisher.web.rest.dto.NodeDTO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly=true)
@Slf4j
public class NodeDTOFactoryImpl implements NodeDTOFactory {

    @Inject
    private IPermissionService permissionService;

    @Override
    public NodeDTO from(@NotNull IContext model, PermissionType minPerm) {
        NodeDTO node = new NodeDTO();
        node.setContextObject(model);
        PermissionType perm = permissionService.getRoleOfUserInContext(SecurityContextHolder.getContext().getAuthentication(), model.getContextKey());
        if (perm == null) {
            log.warn("Perms on context {} are undefined !", model.getContextKey());
            return null;
        }
        node.setUserPerms(perm);
        if (permissionService.hasAuthorizedChilds(SecurityContextHolder.getContext().getAuthentication(), model.getContextKey())) {
            node.setHasChilds(true);
        } else {
            node.setHasChilds(false);
        }
        log.debug("{}", node);
        return node;
    }

    @Override
    public List<NodeDTO> asDTOList(@NotNull List<? extends IContext> models, PermissionType minPerm) {
        List<NodeDTO> nodes = new ArrayList<>();
        for (IContext ctx: models) {
            nodes.add(this.from(ctx, minPerm));
        }
        return nodes;
    }
}
