package org.esupportail.publisher.service;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.repository.PublisherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 *
 */
@Service
@Slf4j
@Transactional
public class PublisherService {

	@Inject
	private PublisherRepository publisherRepository;

	public void doMove(final Publisher publisher, final int newPos) {
		final int curPos = publisherRepository.getDisplayOrderOf(publisher.getId());
		log.debug(
				"Changing displayOrder of Organization id {} from position {} to position {} ",
            publisher.getId(), curPos, newPos);
		if (newPos > curPos) {
            publisherRepository.setLowerDisplayOrderOfRange(publisher.getContext().getOrganization().getId(), curPos, newPos);
		} else if (newPos < curPos) {
            publisherRepository.setUpperDisplayOrderOfRange(publisher.getContext().getOrganization().getId(),newPos, curPos);
		} else
			return;

        Publisher mpublisher = publisherRepository.findOne(publisher.getId());
        mpublisher.setDisplayOrder(newPos);
        publisherRepository.save(mpublisher);

	}

}
