/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
