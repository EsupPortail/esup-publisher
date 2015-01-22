package org.esupportail.publisher.service.bean;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.service.evaluators.IEvaluation;

import com.google.common.collect.Maps;

@Getter
@Setter
@Slf4j
public class AuthoritiesDefinition implements IAuthoritiesDefinition {

	/**
	 * Admins are super admin of the app.
	 */
	private IEvaluation admins;

	/**
	 * Users are users that can manage, edit or contributes.
	 */
	private IEvaluation users;

	public AuthoritiesDefinition() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.esupportail.publisher.service.beans.IRolesDefinition#getAppRoles()
	 */
	@Override
	public Map<String, IEvaluation> getAppRoles() {
		Map<String, IEvaluation> accessRoles = Maps.newLinkedHashMap();
		accessRoles.put(AuthoritiesConstants.ADMIN, admins);
		accessRoles.put(AuthoritiesConstants.USER, users);
		if (log.isDebugEnabled()) {
			log.debug("App Roles defined : {}", accessRoles.toString());
		}
		return accessRoles;
	}

}
