package org.esupportail.publisher.repository;

import java.util.List;

import org.esupportail.publisher.domain.LinkedFileItem;

/**
 * Created by jgribonvald on 07/04/17.
 */
public interface LinkedFileItemRepository extends AbstractRepository<LinkedFileItem, Long> {

    List<LinkedFileItem> findByAbstractItemId(final Long itemId);
    List<LinkedFileItem> findByAbstractItemIdAndInBody(final Long itemId, final boolean inBody);
    List<LinkedFileItem> findByUri(final String fileUri);
}
