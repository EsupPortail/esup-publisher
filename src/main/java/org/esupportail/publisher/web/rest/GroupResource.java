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

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.repository.externals.IExternalGroupDao;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.service.factories.GroupDTOFactory;
import org.esupportail.publisher.service.factories.SubjectDTOSimpleFactory;
import org.esupportail.publisher.web.rest.dto.GroupDTO;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api")
public class GroupResource {

	private final Logger log = LoggerFactory.getLogger(GroupResource.class);

	@Inject
	private IExternalGroupDao groupDao;

	@Inject
	private GroupDTOFactory groupDTOFactory;

    @Inject
    private SubjectDTOSimpleFactory subjectDTOSimpleFactory;

    @Inject
    private ExternalGroupHelper externalGroupHelper;

	/**
	 * GET /groups/:id -> get the "id" group.
	 */
	@RequestMapping(value = "/groups/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed(AuthoritiesConstants.USER)
	public ResponseEntity<IExternalGroup> getGroup(@PathVariable String id) {
		log.debug("REST request to get Group : {}", id);
		IExternalGroup group = groupDao.getGroupById(id,true);
		if (group == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(group, HttpStatus.OK);
	}

	/**
	 * GET /groups/extended/:id -> get the "id" group.
	 */
	@RequestMapping(value = "/groups/extended/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed(AuthoritiesConstants.USER)
	public ResponseEntity<GroupDTO> getAllInfoGroup(@PathVariable String id) {
		log.debug("REST request to get GroupInfo of : {}", id);
        GroupDTO group = groupDTOFactory.from(id);
		if (group == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(group, HttpStatus.OK);
	}

    /**
     * GET /groups/subject/:id -> get the "id" group.
     */
    @RequestMapping(value = "/groups/subject/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<SubjectDTO> getAllInfoSubject(@PathVariable String id) {
        log.debug("REST request to get SubjetInfos of group : {}", id);
        SubjectDTO group = subjectDTOSimpleFactory.from(new SubjectKey(id, SubjectType.GROUP));
        if (group == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    /**
     * GET /group/attributes/ -> get list of displayed group attributes.
     */
    @RequestMapping(value = "/groups/attributes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public List<String> getShownGroupAttributes(HttpServletResponse response) {
        log.debug("REST request to get List of group Attributes to show !");
        return Lists.newArrayList(externalGroupHelper.getOtherGroupDisplayedAttributes());
    }


}