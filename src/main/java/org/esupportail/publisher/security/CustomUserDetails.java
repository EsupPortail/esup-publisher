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
				+ ", isAccountNonExpired()=" + isAccountNonExpired()
				+ ", isAccountNonLocked()=" + isAccountNonLocked()
				+ ", isCredentialsNonExpired()=" + isCredentialsNonExpired()
				+ ", isEnabled()=" + isEnabled() + "]";
	}

}
