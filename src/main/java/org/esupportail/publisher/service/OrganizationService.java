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
