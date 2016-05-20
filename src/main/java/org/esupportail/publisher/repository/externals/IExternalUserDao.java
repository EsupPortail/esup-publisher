package org.esupportail.publisher.repository.externals;

import org.esupportail.publisher.domain.externals.IExternalUser;

import java.util.List;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 11 juil. 2014
 */
public interface IExternalUserDao {

    IExternalUser getUserByUid(final String uid);

    List<String> getUserMailsByUids(final Iterable<String> uids);

    List<IExternalUser> getUsersByUids(final Iterable<String> uids);

    List<IExternalUser> getUsersByUidsWithFilter(final Iterable<String> uids, final String token);

    List<IExternalUser> getUsersByGroupId(final String groupId);

    List<IExternalUser> getUsersFromParentGroups(final Iterable<String> groupIds, final String token);

    List<IExternalUser> getUsersWithFilter(final String stringFilter, final String token);

    boolean isUserFoundWithFilter(final String stringFilter, final String uid);

}
