package org.esupportail.publisher.repository.externals;

import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalUser;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 11 juil. 2014
 */
public interface IExternalGroupDao {

    IExternalGroup getGroupById(@NotNull final String id, final boolean withMembers);

    List<IExternalGroup> getGroupsById(@NotNull final Collection<String> id, final boolean withMembers);

    List<IExternalGroup> getGroupsByIdStartWith(@NotNull final Collection<String> ids, final boolean withMembers);

    List<IExternalGroup> getDirectGroupMembers(@NotNull final String id, final boolean withMembers);

    List<IExternalGroup> getGroupsWithFilter(@NotNull final String stringFilter, final String token, final boolean withMembers);

    List<IExternalUser> getDirectUserMembers(@NotNull final String id);

}
