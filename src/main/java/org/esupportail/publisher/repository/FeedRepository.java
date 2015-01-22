/**
 *
 */
package org.esupportail.publisher.repository;

import java.util.Set;

import org.esupportail.publisher.domain.AbstractFeed;
import org.esupportail.publisher.domain.Category;

/**
 * @author GIP RECIA - Julien Gribonvald 24 juil. 2014
 */
public interface FeedRepository<T extends AbstractFeed> extends
		ClassificationRepository<T> {

	Set<T> findByParent(Category category);

}
