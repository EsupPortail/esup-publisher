package org.esupportail.publisher.repository.externals;

import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalUser;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 * Created by jgribonvald on 11/06/15.
 */
public class EmptyGroupDaoImpl implements IExternalGroupDao {
    @Override
    public IExternalGroup getGroupById(@NotNull String id, boolean withMembers) {
        return null;
    }

    @Override
    public List<IExternalGroup> getGroupsById(@NotNull Collection<String> id, boolean withMembers) {
        return null;
    }

    @Override
    public List<IExternalGroup> getGroupsByIdStartWith(@NotNull Collection<String> id, boolean withMembers) {
        return null;
    }

    @Override
    public List<IExternalGroup> getDirectGroupMembers(@NotNull String id, boolean withMembers) {
        return null;
    }

    @Override
    public List<IExternalGroup> getGroupsWithFilter(@NotNull String stringFilter, String token, boolean withMembers) {
        return null;
    }

    @Override
    public List<IExternalUser> getDirectUserMembers(@NotNull String id) {
        return null;
    }
}
