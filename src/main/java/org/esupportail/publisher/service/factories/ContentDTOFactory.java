package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.web.rest.dto.ContentDTO;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald 22 avril. 2015.
 */
public interface ContentDTOFactory {

    ContentDTO from (@NotNull final Long id) throws ObjectNotFoundException;

    //ContentDTO from (@NotNull final AbstractItem model);

    Set<ContentDTO> asDTOSet (final Collection<AbstractItem> models);

    List<ContentDTO> asDTOList (final Collection<AbstractItem> models);
}
