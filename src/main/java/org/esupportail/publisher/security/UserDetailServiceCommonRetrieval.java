package org.esupportail.publisher.security;

import java.util.Collection;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.repository.externals.IExternalUserDao;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
public abstract class UserDetailServiceCommonRetrieval {

    @Inject
    @Getter
    private transient UserRepository userDao;

    @Inject
    @Getter
    private transient IExternalUserDao extDao;

    @Inject
    private transient IAuthorityService grantedAuthorityService;

    @Inject
    private transient UserDTOFactory userDTOFactory;

    @Transactional
    public CustomUserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> optionalUser = userDao.findById(userName);
        User internal = optionalUser.orElse(null);
        final IExternalUser external = extDao.getUserByUid(userName);
        if (external == null) {
            throw new UsernameNotFoundException(String.format(
                "User [%s] without permission !", userName));
        }
        UserDTO user = userDTOFactory.from(internal, external);
        Collection<? extends GrantedAuthority> authorities = grantedAuthorityService
            .getUserAuthorities(user);

        if (internal == null) {
            // TODO save the user if he has permissions before, so check perms !
            boolean isAtLeastUser = authorities
                .contains(new SimpleGrantedAuthority(
                    AuthoritiesConstants.USER))
                || authorities.contains(new SimpleGrantedAuthority(
                AuthoritiesConstants.ADMIN));
            if (authorities != null && !authorities.isEmpty() && isAtLeastUser) {
                internal = userDao.saveAndFlush(userDTOFactory.from(user));
                Optional<User> optionalU = userDao.findById(userName);
                internal = optionalU.orElse(null);
                if (internal == null) {
                    log.error(
                        "User with username {} could not be read back after being created.",
                        userName);
                    throw new UsernameNotFoundException(
                        String.format(
                            "User with username [%s] could not be read back after being created.",
                            userName));
                }

            } else if (authorities == null || authorities.isEmpty()) {
                log.error("User {} without permission !", userName);
                throw new UsernameNotFoundException(String.format(
                    "User [%s] without permission !", userName));
            } else if (!isAtLeastUser) {
                if (log.isDebugEnabled()) {
                    log.debug("Loading a user without permissions defined as only Authenticated.");
                }
            }
        }
        return new CustomUserDetails(userDTOFactory.from(internal, external), internal,	authorities);
    }
}
