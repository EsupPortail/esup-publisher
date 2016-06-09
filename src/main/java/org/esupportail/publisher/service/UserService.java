package org.esupportail.publisher.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mysema.commons.lang.Pair;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.Filter;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.domain.enums.FilterType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.FilterRepository;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.repository.externals.IExternalUserDao;
import org.esupportail.publisher.repository.predicates.FilterPredicates;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.IAuthorityService;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.security.SecurityUtils;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.*;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

	private final Logger log = LoggerFactory.getLogger(UserService.class);

	// @Inject
	// private PasswordEncoder passwordEncoder;

	@Inject
	private UserRepository userRepository;

    @Inject
    private IExternalUserDao externalUserDao;

	@Inject
	private UserDTOFactory userDTOFactory;

	@Inject
	private IAuthorityService autorityService;

    @Inject
    private IPermissionService permissionService;

    @Inject
    private SubscriberService subscriberService;

    @Inject
    private FilterRepository filterRepository;

    @Inject
    private ContextService contextService;

	// @Inject
	// private PersistentTokenRepository persistentTokenRepository;

	// @Inject
	// private AuthorityRepository authorityRepository;

	// public User createUserInformation(String login, String password, String
	// firstName, String lastName, String email,
	// String langKey) {
	// User newUser = new User();
	// // Authority authority = authorityRepository.findOne("ROLE_USER");
	// // Set<Authority> authorities = new HashSet<>();
	// String encryptedPassword = passwordEncoder.encode(password);
	// newUser.setUserId(login);
	// newUser.getDisplayName()
	// newUser.setEmail(email);
	// newUser.setLangKey(langKey);
	// // new user is not active
	// newUser.setActivated(false);
	// // new user gets registration key
	// newUser.setActivationKey(RandomUtil.generateActivationKey());
	// authorities.add(authority);
	// newUser.setAuthorities(authorities);
	// userRepository.save(newUser);
	// log.debug("Created Information for User: {}", newUser);
	// return newUser;
	// }

	public void updateUserInformation() {
        User user = SecurityUtils.getCurrentUser();
        if (user != null) {
            user = userRepository.save(user);
            log.debug("Changed Information for User: {}", user);
        } else {
            log.warn("Tried to update lastAccess of unknown user !");
        }
	}

	@Transactional(readOnly = true)
	public CustomUserDetails getUserWithAuthorities() {
        User currentUser = userRepository.findOne(SecurityUtils.getCurrentLogin());
		UserDTO currentUserDTO = userDTOFactory.from(currentUser);
		return new CustomUserDetails(currentUserDTO, currentUser,
				autorityService.getUserAuthorities(currentUserDTO));
	}

	// /**
	// * Persistent Token are used for providing automatic authentication, they
	// * should be automatically deleted after 30 days.
	// * <p/>
	// * <p>
	// * This is scheduled to get fired everyday, at midnight.
	// * </p>
	// */
	// @Scheduled(cron = "0 0 0 * * ?")
	// public void removeOldPersistentTokens() {
	// LocalDate now = new LocalDate();
	// List<PersistentToken> tokens = persistentTokenRepository
	// .findByTokenDateBefore(now.minusMonths(1));
	// for (PersistentToken token : tokens) {
	// log.debug("Deleting token {}", token.getSeries());
	// User user = token.getUser();
	// user.getPersistentTokens().remove(token);
	// persistentTokenRepository.delete(token);
	// }
	// }

	/**
	 * Not activated users should be automatically deleted after 3 days.
	 * <p/>
	 * <p>
	 * This is scheduled to get fired everyday, at 01:00 (am).
	 * </p>
	 */
	// @Scheduled(cron = "0 0 1 * * ?")
	// public void removeNotActivatedUsers() {
	// DateTime now = new DateTime();
	// List<User> users = userRepository
	// .findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
	// for (User user : users) {
	// log.debug("Deleting not activated user {}", user.getLogin());
	// userRepository.delete(user);
	// }
	// }

    // TODO when several context are selected
    public List<UserDTO> getUserFromSearchInCtx(final ContextKey contextKey, final List<ContextKey> subContextKeys, final String search) {
        if (search == null || search.isEmpty() || search.length() < 3) return Lists.newArrayList();
        if (contextKey.getKeyType() == null || contextKey.getKeyId() == null) {
            return Lists.newArrayList();
        }
        Pair<PermissionType, PermissionDTO> perms = permissionService.getPermsOfUserInContext(SecurityContextHolder.getContext().getAuthentication(), contextKey);
        if ((perms == null || PermissionType.LOOKOVER.equals(perms.getFirst())) && subContextKeys != null) {
            Pair<PermissionType, PermissionDTO> lowerPerm = null;
            // we need to get the lower perm to apply rules on lower context to avoid problems of rights !
            for (ContextKey ctxKey: subContextKeys) {
                perms = permissionService.getPermsOfUserInContext(SecurityContextHolder.getContext().getAuthentication(), ctxKey);
                if (perms != null && (lowerPerm == null || perms.getFirst().getMask() < lowerPerm.getFirst().getMask())) {
                    lowerPerm = perms;
                    // if contributor if found we have the lower rights !
                    if (lowerPerm.getFirst().getMask() == PermissionType.CONTRIBUTOR.getMask()) break;
                }
            }
        }
        log.debug("getRootNodes for ctx {}, with permsType {} and permsDTO {}", contextKey, perms.getFirst(), perms.getSecond());
        if (perms == null || perms.getFirst() == null || !PermissionType.ADMIN.equals(perms.getFirst()) && perms.getSecond() == null) {
            return Lists.newArrayList();
        }

        // if ADMIN perms.getSecond() is null as all is authorized
        if (PermissionType.ADMIN.getMask() <= perms.getFirst().getMask()) {
            final ContextKey rootCtx = contextService.getOrganizationCtxOfCtx(contextKey);
            if (rootCtx != null){
                Filter filter = filterRepository.findOne(FilterPredicates.ofTypeOfOrganization(rootCtx.getKeyId(), FilterType.LDAP));
                if (filter != null) {
                    return userDTOFactory.asDTOList(externalUserDao.getUsersWithFilter(filter.getPattern(), search), false);
                }
            }
            // can be very long and finish with timeout
            return userDTOFactory.asDTOList(externalUserDao.getUsersWithFilter(null, search), false);
        }

        if (PermissionType.CONTRIBUTOR.getMask() <= perms.getFirst().getMask()) {
            PermissionDTO perm = perms.getSecond();
            if (perm != null) {
                if (perm instanceof PermOnClassifWSubjDTO) {
                    Set<SubjectKeyDTO> authorizedSubjects = ((PermOnClassifWSubjDTO) perm).getAuthorizedSubjects();
                    // TODO à voir si on offre le choix de ne pas définir des AuthorizedSubjects et que dans ce cas par défaut recherche sur filtre par défaut (idem perm classiques)

                    // non sens de ne récupérer que des utilisateurs définis, il faut aussi les chercher dans les groupes autorisés
                    Set<String> authorizedUsers = Sets.newHashSet();
                    Set<String> authorizedGroups = Sets.newHashSet();
                    for (SubjectKeyDTO subjDto : authorizedSubjects) {
                        switch (subjDto.getKeyType()) {
                            case PERSON:
                                authorizedUsers.add(subjDto.getKeyId());
                                break;
                            case GROUP:
                                authorizedGroups.add(subjDto.getKeyId());
                                break;
                            default:
                                log.error("Case not managed");
                                throw new IllegalArgumentException("Type of subject not managed " + subjDto.getKeyType());
                        }
                    }
                    // uniquement les utilisateurs définis
                    Set<IExternalUser> filteredSearch = Sets.newHashSet(externalUserDao.getUsersByUidsWithFilter(authorizedUsers, search));
                    // et à partir des groupes
                    filteredSearch.addAll(externalUserDao.getUsersFromParentGroups(authorizedGroups, search));
                    return userDTOFactory.asDTOList(filteredSearch, false);
                } else if (perm instanceof PermOnCtxDTO) {
                    final ContextKey rootCtx = contextService.getOrganizationCtxOfCtx(contextKey);
                    if (rootCtx != null) {
                        final Filter filter = filterRepository.findOne(FilterPredicates.ofTypeOfOrganization(rootCtx.getKeyId(), FilterType.LDAP));
                        if (filter != null) {
                            return userDTOFactory.asDTOList(externalUserDao.getUsersWithFilter(filter.getPattern(), search), false);
                        }
                    }
                    log.warn("No filters are defined for context {}, we procced on default subscribers", rootCtx);
                    // Cas ou aucun filtre n'est défini on tente à partir des subscriber définis par défaut au niveau organization
                    List<Subscriber> subscribers = subscriberService.getDefaultsSubscribersOfContext(contextKey);
                    Set<String> userIds = Sets.newHashSet();
                    Set<String> groupIds = Sets.newHashSet();
                    for (Subscriber subscriber : subscribers) {
                        switch (subscriber.getSubjectCtxId().getSubject().getKeyType()) {
                            case PERSON:
                                userIds.add(subscriber.getSubjectCtxId().getSubject().getKeyId());
                                break;
                            case GROUP:
                                groupIds.add(subscriber.getSubjectCtxId().getSubject().getKeyId());
                                break;
                            default:
                                log.error("Case not managed");
                                throw new IllegalArgumentException("Type of subject not managed " + subscriber.getSubjectCtxId().getSubject().getKeyType());
                        }
                    }
                    // uniquement les utilisateurs définis
                    Set<IExternalUser> filteredSearch = Sets.newHashSet(externalUserDao.getUsersByUidsWithFilter(userIds, search));
                    // et à partir des groupes
                    filteredSearch.addAll(externalUserDao.getUsersFromParentGroups(groupIds, search));

                    return userDTOFactory.asDTOList(filteredSearch, false);
                } else
                    throw new NotYetImplementedException(String.format("Management of %s type is not yet implemented", perm.getClass()));
            }
        }
        return Lists.newArrayList();
    }
}
