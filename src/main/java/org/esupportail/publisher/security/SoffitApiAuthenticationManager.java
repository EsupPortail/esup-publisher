package org.esupportail.publisher.security;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apereo.portal.soffit.security.SoffitApiPreAuthenticatedProcessingFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;

/**
 * Concrete <code>AuthenticationManager</code> implementation (Spring Security) for use with {@link
 * SoffitApiPreAuthenticatedProcessingFilter}. Use <code>
 * SoffitApiPreAuthenticatedProcessingFilter.setAuthenticationManager</code> when constructing the
 * bean.
 *
 * @since 5.1
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SoffitApiAuthenticationManager extends org.apereo.portal.soffit.security.SoffitApiAuthenticationManager implements AuthenticationManager {

    @NotNull
    private AuthenticationUserDetailsService<UsernamePasswordAuthenticationToken> userDetailsServiceFromSoffit;

    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {

        log.debug("Authenticating the following Authentication object:  {}", authentication);

        final Authentication auth = super.authenticate(authentication);

        final CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsServiceFromSoffit.loadUserDetails((UsernamePasswordAuthenticationToken) auth);

        return new UsernamePasswordAuthenticationToken(
            customUserDetails, authentication.getCredentials(), customUserDetails.getAuthorities());
    }
}
