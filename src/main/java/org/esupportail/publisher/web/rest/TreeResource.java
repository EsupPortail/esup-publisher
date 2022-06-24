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
package org.esupportail.publisher.web.rest;

import java.util.List;

import javax.inject.Inject;

import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.security.SecurityConstants;
import org.esupportail.publisher.service.ContextService;
import org.esupportail.publisher.service.IGroupService;
import org.esupportail.publisher.web.propertyeditors.ContextKeyEditor;
import org.esupportail.publisher.web.rest.dto.SearchSubjectFormDTO;
import org.esupportail.publisher.web.rest.dto.TreeJS;
import org.esupportail.publisher.web.rest.dto.UserDTO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jgribonvald on 28/05/15.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class TreeResource {

    @Inject
    private ContextService contextService;
    @Inject
    private IGroupService groupService;


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, new ContextKeyEditor());
    }


    /**
     * GET /nodes -> get root nodes
     */
    @RequestMapping(value = "/contexts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TreeJS>> getContexts(@RequestParam("search") String id/*, @RequestParam(value="filtered", required = false) Boolean filtered*/) {
        log.debug("REST request to get tree of Context from id {}", id);
        List<TreeJS> nodes = null;
        if ("1".equals(id)) {
            nodes = contextService.getRootTree(PermissionType.LOOKOVER);
        } else {
            String[] spid = id.split(":");
            if (spid.length == 2) {
                Long ctxId = Long.valueOf(spid[0]);
                ContextType ctxType = ContextType.valueOf(spid[1]);
                if (ctxId != null && ctxType != null) {
                    ContextKey ctx = new ContextKey(ctxId, ctxType);
                    //nodes = contextService.getTreeChilds(ctx, PermissionType.LOOKOVER, filtered);
                    nodes = contextService.getTreeChilds(ctx, PermissionType.LOOKOVER, true);
                }
            }
        }
        if (nodes != null) return new ResponseEntity<>(nodes, HttpStatus.OK);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * GET /groups/{ctx_id}/{ctx_type} -> get root nodes
     */
//    @RequestMapping(value = "/groups/{ctx_id}/{ctx_type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
//        + " && hasPermission(#ctxId,  #ctxType, '" + SecurityConstants.PERM_LOOKOVER + "')")
//    public ResponseEntity<List<TreeJS>> getGroups(@PathVariable("ctx_id") Long ctxId, @PathVariable("ctx_type") ContextType ctxType,
//                                                  @RequestParam(value = "subctxs", required = false) List<ContextKey> ctxs, @RequestParam("id") String id) {
//        log.debug("REST request to get tree of groups from id {}, ctx Key {}, other ctx Keys {}", id, new ContextKey(ctxId, ctxType), ctxs);
//        List<TreeJS> nodes = null;
//        if ("1".equals(id)) {
//            nodes = groupService.getRootNodes(new ContextKey(ctxId, ctxType), ctxs);
//        } else {
//            nodes = groupService.getGroupMembers(id);
//        }
//        if (nodes != null) return new ResponseEntity<>(nodes, HttpStatus.OK);
//
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }

    /**
     * POST /groups/{ctx_id}/{ctx_type} -> get root nodes
     */
    @RequestMapping(value = "/groups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && hasPermission(#form.context.keyId, #form.context.keyType, '" + SecurityConstants.PERM_LOOKOVER + "')")
    public ResponseEntity<List<TreeJS>> getGroupsBis(@RequestBody SearchSubjectFormDTO form) {
        log.debug("REST request to get tree of groups from {}", form);
        List<TreeJS> nodes = null;
        if ("1".equals(form.getSearch())) {
            nodes = groupService.getRootNodes(form.getContext(), form.getSubContexts());
        } else {
            nodes = groupService.getGroupMembers(form.getSearch());
        }

        if (nodes != null) return new ResponseEntity<>(nodes, HttpStatus.OK);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * GET /groups/usermembers -> get user members of a group
     */
    @RequestMapping(value = "/groups/usermembers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER)
    //TODO checking rights of gettings members of the passed group
    public ResponseEntity<List<UserDTO>> getUserOfGroup(@RequestParam("id") String id) {
        log.debug("REST request to get user members of groups from id {}", id);
        return new ResponseEntity<>(groupService.getUserMembers(id), HttpStatus.OK);
    }

    /**
     * GET  /nodes/:ctx_id/:ctx_type -> get the "id" of parent context.
     */
    /*@RequestMapping(value = "/tree/{ctx_id}/{ctx_type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && hasPermission(#ctxId, #ctxType, '" + SecurityConstants.PERM_LOOKOVER + "')")
    public ResponseEntity<List<TreeJS>> getTree(@PathVariable("ctx_id") Long ctxId, @PathVariable("ctx_type") String ctxType, HttpServletResponse response) {
        log.debug("REST request to get Childs of Ctx type: {} and id: {}", ctxType, ctxId);

        ContextKey ctx = new ContextKey(ctxId, ContextType.valueOf(ctxType));

        List<TreeJS> nodes = contextTreeService.getTreeChilds(ctx, PermissionType.LOOKOVER);
        if (nodes != null) return new ResponseEntity<>(nodes, HttpStatus.OK);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }*/


}