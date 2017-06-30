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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.security.core.GrantedAuthority;

public class CustomUserDetails implements
    org.springframework.security.core.userdetails.UserDetails {

    /** */
    private static final long serialVersionUID = -4777124807325532850L;

    @Getter
    //@Setter
    private UserDTO user;

    @Getter
    @JsonIgnore
    private User internalUser;

    @Getter
    @JsonIgnore
    private Collection<? extends GrantedAuthority> authorities;

    @Getter
    private List<String> roles;

    @Getter
    @Setter
    private String sessionId;

    public CustomUserDetails() {
        super();
    }

    /**
     * @param user
     * @param authorities
     */
    public CustomUserDetails(UserDTO user, User internalUser,
                             Collection<? extends GrantedAuthority> authorities) {
        super();
        this.user = user;
        this.internalUser = internalUser;
        this.authorities = authorities;
        roles = new ArrayList<>();
        for (GrantedAuthority authority : authorities) {
            roles.add(authority.getAuthority());
        }
    }

    // @Override
    // public Collection<? extends GrantedAuthority> getAuthorities() {
    // List<GrantedAuthority> l = new ArrayList<GrantedAuthority>();
    // l.add( new GrantedAuthority() {
    // private static final long serialVersionUID = 1L;
    //
    // @Override
    // public String getAuthority() {
    // return "ROLE_AUTHENTICATED";
    // }
    // });
    // return l;
    // }

    @Override
    public String getPassword() {
        return user.getLogin();
    }

    @Override
    public String getUsername() {
        return user.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isFoundOnExternalSource();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UserDetails [user=" + user + ", authorities=" + authorities
            + ", role= " + roles
            + ", isAccountNonExpired()=" + isAccountNonExpired()
            + ", isAccountNonLocked()=" + isAccountNonLocked()
            + ", isCredentialsNonExpired()=" + isCredentialsNonExpired()
            + ", isEnabled()=" + isEnabled() + "]";
    }

}
