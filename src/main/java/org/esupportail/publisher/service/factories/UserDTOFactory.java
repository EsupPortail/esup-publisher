/**
 *
 */
package org.esupportail.publisher.service.factories;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.web.rest.dto.UserDTO;

/**
 * @author GIP RECIA - Julien Gribonvald 25 juil. 2014
 */
public interface UserDTOFactory {

	User from(@NotNull final UserDTO dtObject);

	UserDTO from(final IExternalUser extModel, final boolean withInternal);

	UserDTO from(final User model, final IExternalUser extModel);

	UserDTO from(@NotNull final User model);

	UserDTO from(@NotNull final String id);

	Set<UserDTO> asDTOSet(final Collection<IExternalUser> models,
			boolean withInternal);

	List<UserDTO> asDTOList(final Collection<IExternalUser> models,
			boolean withInternal);

}
