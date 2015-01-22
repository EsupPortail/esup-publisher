/**
 *
 */
package org.esupportail.publisher.repository;

import java.util.Set;

import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.Publisher;

/**
 * @author GIP RECIA - Julien Gribonvald 24 juil. 2014
 */
public interface ClassificationRepository<T extends AbstractClassification>
		extends AbstractRepository<T, Long> {

	Set<T> findByPublisher(Publisher publisher);

}
