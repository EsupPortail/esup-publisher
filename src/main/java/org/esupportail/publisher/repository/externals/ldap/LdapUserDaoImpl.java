package org.esupportail.publisher.repository.externals.ldap;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.esupportail.publisher.domain.externals.ExternalUserHelper;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.externals.IExternalUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.*;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author GIP RECIA - Julien Gribonvald 11 juil. 2014
 */
@Service
@Data
@AllArgsConstructor
public class LdapUserDaoImpl implements IExternalUserDao {

	/**
	 * Logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Spring template used to perform search in the ldap.
	 */
	@Autowired
	private LdapTemplate ldapTemplate;

	@Autowired
	private ExternalUserHelper externalUserHelper;

	/**
	 * constructor.
	 */
	public LdapUserDaoImpl() {
		super();
	}

	@Override
	// @Cacheable(value = "ExternalUsers", key = "#uid")
	public IExternalUser getUserByUid(String uid) {
		final AndFilter filter = new AndFilter();
		filter.append(new EqualsFilter(externalUserHelper.getUserIdAttribute(),
				uid));
		if (logger.isDebugEnabled()) {
			logger.debug("LDAP filter applied : " + filter);
		}
		ContextMapper<IExternalUser> mapper = new LdapUserContextMapper(
				this.externalUserHelper);
		// SearchControls constraints = new SearchControls();
		// constraints.setReturningAttributes((String[])
		// ldapUserHelper.getAttributes().toArray());
		LdapQuery query = LdapQueryBuilder.query()
				.base(externalUserHelper.getUserDNSubPath()).filter(filter);
		IExternalUser user;
		try {
			user = ldapTemplate.searchForObject(query, mapper);
		} catch (IncorrectResultSizeDataAccessException e) {
			user = null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("LDAP user found : {}", user);
		}
		return user;
	}

	@Override
	// @Cacheable(value = "ExternalUsers")
	public List<IExternalUser> getUsersWithFilter(final String stringFilter,final String token) {
        Filter tokenFilter;
        Filter paramFilter;
        AndFilter mainFilter = new AndFilter();
        boolean canSearch = false;
        if (stringFilter != null && !stringFilter.isEmpty()) {
            paramFilter = new HardcodedFilter(stringFilter);
            mainFilter.append(paramFilter);
            canSearch = true;
        }
        if (token != null && !token.trim().isEmpty()) {
            tokenFilter = new WhitespaceWildcardsFilter(externalUserHelper.getUserSearchAttribute(), token.trim());
            mainFilter.append(tokenFilter);
            canSearch = true;
        }

        if (canSearch)
            return searchWithFilter(mainFilter);
        return Lists.newArrayList();
    }

	/**
	 * @param uids
	 * @return a list of users
	 */
	@Override
	// @Cacheable(value = "ExternalUsers")
	public List<IExternalUser> getUsersByUids(final Iterable<String> uids) {
		final OrFilter filter = orFilterOnUids(uids);
		if (filter == null)
			return new LinkedList<IExternalUser>();
		else
			return searchWithFilter(filter);
	}
    /**
     * @param uids
     * @param token
     * @return a list of users
     */
    @Override
    // @Cacheable(value = "ExternalUsers")
    public List<IExternalUser> getUsersByUidsWithFilter(final Iterable<String> uids, final String token) {
        final OrFilter filter = orFilterOnUids(uids);
        if (filter == null || token == null || token.trim().isEmpty())
            return new LinkedList<IExternalUser>();
        else
            return searchWithFilter(new AndFilter().append(filter).append(new WhitespaceWildcardsFilter(externalUserHelper.getUserSearchAttribute(), token.trim())));
    }


	/**
	 * userSearchAttribute
	 *
	 * @param uids
	 * @return a list for user mails.
	 */
	@Override
	public List<String> getUserMailsByUids(final Iterable<String> uids) {
		final List<String> retVal = new ArrayList<String>();
		for (IExternalUser externalUser : getUsersByUids(uids)) {
			String mail = externalUser.getEmail();
			if (mail != null) {
				logger.debug("mail added to list :" + mail);
				retVal.add(mail.trim());
			} else {
				logger.warn("no mail for " + externalUser.getId());
			}
		}
		return retVal;
	}

    @Override
    public List<IExternalUser> getUsersByGroupId(final String groupId) {
        final String userAttrGroup = this.externalUserHelper.getUserGroupAttribute();
        if (userAttrGroup != null && !userAttrGroup.isEmpty() && groupId != null && !groupId.trim().isEmpty()) {
            Filter filter = new EqualsFilter(userAttrGroup, groupId.trim());
            return searchWithFilter(filter);
        }
        return new LinkedList<IExternalUser>();
    }

    @Override
    public List<IExternalUser> getUsersFromParentGroups(final Iterable<String> groupIds, final String search) {
        final String userAttrGroup = this.externalUserHelper.getUserGroupAttribute();
        if (userAttrGroup != null && !userAttrGroup.isEmpty() && groupIds != null && !Iterables.isEmpty(groupIds)) {
            OrFilter groupFilter = new OrFilter();
            boolean emptyFilter = true;
            for (String groupId : groupIds) {
                if (groupId != null && !groupId.trim().isEmpty()) {
                    groupFilter.or(new LikeFilter(externalUserHelper.getUserGroupAttribute(), groupId.trim() + "*"));
                    emptyFilter = false;
                }
            }
            if (emptyFilter) return new LinkedList<IExternalUser>();

            AndFilter filter = new AndFilter().and(groupFilter);
            if (search != null && !search.trim().isEmpty()) {
                filter.and(new WhitespaceWildcardsFilter(externalUserHelper.getUserSearchAttribute(), search.trim()));
            }
            return searchWithFilter(filter);
        }
        return new LinkedList<IExternalUser>();
    }

    @Override
    public boolean isUserFoundWithFilter(@NotNull final String stringFilter, @NotNull final String uid) {
        AndFilter filter = new AndFilter()
            .and(new HardcodedFilter(stringFilter));
        filter.and(new EqualsFilter(externalUserHelper.getUserIdAttribute(), uid));

        return !searchWithFilter(filter).isEmpty();
    }

    // @Cacheable(value = "ExternalUsers", key = "#filter")
	private List<IExternalUser> searchWithFilter(final Filter filter) {
		final String filterAsStr = filter.encode();
		if (logger.isDebugEnabled()) {
			logger.debug("LDAP filter applied : " + filterAsStr);
		}
		ContextMapper<IExternalUser> mapper = new LdapUserContextMapper(
				this.externalUserHelper);
		// SearchControls constraints = new SearchControls();
		// constraints.setReturningAttributes((String[])
		// ldapUserHelper.getAttributes().toArray());
		LdapQuery query = LdapQueryBuilder.query()
				.base(externalUserHelper.getUserDNSubPath()).filter(filter);
		return ldapTemplate.search(query, mapper);
	}

	private OrFilter orFilterOnUids(final Iterable<String> uids) {
		// needed since empty OrFilter() is true instead of false
        // we must not have an empty filter !
		// (https://jira.springsource.org/browse/LDAP-226)
		if (Iterables.isEmpty(uids))
			return null;

		OrFilter filter = new OrFilter();
        boolean emptyFilter = true;
		for (String uid : uids) {
            if (uid != null && !uid.trim().isEmpty()) {
                filter.or(new EqualsFilter(externalUserHelper.getUserIdAttribute(),uid));
                emptyFilter = false;
            }
		}
        if (emptyFilter) return null;
		return filter;
	}

}
