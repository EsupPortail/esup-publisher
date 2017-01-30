package org.esupportail.publisher.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;

/**
 * Created by jgribonvald on 31/05/16.
 */
public class CustomSessionFixationProtectionStrategy extends SessionFixationProtectionStrategy {

    private ServiceUrlHelper serviceUrlHelper;
    private ServiceProperties serviceProperties;
    private String casTargetUrlParam;

    public CustomSessionFixationProtectionStrategy(ServiceUrlHelper serviceUrlHelper, ServiceProperties serviceProperties, String casTargetUrlParam) {
        this.serviceUrlHelper = serviceUrlHelper;
        this.serviceProperties = serviceProperties;
        this.casTargetUrlParam = casTargetUrlParam;
    }

    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        super.onAuthentication(authentication, request, response);
        ((AbstractAuthenticationToken)authentication).setDetails(new RememberWebAuthenticationDetails(request, serviceUrlHelper, serviceProperties, casTargetUrlParam));
    }
}

