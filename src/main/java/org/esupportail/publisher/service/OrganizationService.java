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

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 *
 */
@Service
@Slf4j
@Transactional
public class OrganizationService {

	@Inject
	private OrganizationRepository organizationRepository;

	public void doMove(final Long id, final int newPos) {
		final int curPos = organizationRepository.getDisplayOrderOf(id);
		log.debug(
				"Changing displayOrder of Organization id {} from position {} to position {} ",
				id, curPos, newPos);
		if (newPos > curPos) {
			organizationRepository.setLowerDisplayOrderOfRange(curPos, newPos);
		} else if (newPos < curPos) {
			organizationRepository.setUpperDisplayOrderOfRange(newPos, curPos);
		} else
			return;

		Organization organization = organizationRepository.findOne(id);
		organization.setDisplayOrder(newPos);
		organizationRepository.save(organization);

	}

}
