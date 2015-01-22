package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.SubjectContextKey;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.web.rest.dto.SubjectContextKeyDTO;
import org.esupportail.publisher.web.rest.dto.SubscriberDTO;

public interface SubscriberDTOFactory
		extends
		GenericDTOFactory<SubscriberDTO, Subscriber, SubjectContextKeyDTO, SubjectContextKey> {

	SubjectContextKey convertToModelKey(final SubjectContextKeyDTO dtoKey);

	SubjectContextKeyDTO convertToDTOKey(final SubjectContextKey modelKey);
}
