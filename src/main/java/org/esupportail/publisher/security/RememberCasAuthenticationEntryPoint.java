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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.esupportail.publisher.config.SecurityConfiguration;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.jasig.cas.client.Protocol;
import org.jasig.cas.client.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;
import org.springframework.web.util.UrlPathHelper;

/**
 * Returns a 401 error code (Unauthorized) to the client when not on login url and if not
 * authenticated.
 */
public class RememberCasAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {

    private final Logger log = LoggerFactory.getLogger(RememberCasAuthenticationEntryPoint.class);

    // ~ Instance fields
    // ================================================================================================
    private ServiceProperties serviceProperties;

    private String casLoginUrl;

    private String pathLogin;

    private String targetUrlParameter = "spring-security-redirect";

    private ServiceUrlHelper urlHelper;

    /**
     * Determines whether the Service URL should include the session id for the specific user. As of
     * CAS 3.0.5, the session id will automatically be stripped. However, older versions of CAS
     * (i.e. CAS 2), do not automatically strip the session identifier (this is a bug on the part of
     * the older server implementations), so an option to disable the session encoding is provided
     * for backwards compatibility.
     *
     * By default, encoding is enabled.
     *
     * @deprecated since 3.0.0 because CAS is currently on 3.3.5.
     */
    @Deprecated
    private boolean encodeServiceUrlWithSessionId = true;

    /** From CAS org.jasig.cas.client.util.CommonUtils to replace deprecated use of constructServiceUrl(...) .*/
    private static final String SERVICE_PARAMETER_NAMES;

    static {
        final Set<String> serviceParameterSet = new HashSet<String>(4);
        for (final Protocol protocol : Protocol.values()) {
            serviceParameterSet.add(protocol.getServiceParameterName());
        }
        SERVICE_PARAMETER_NAMES = serviceParameterSet.toString()
            .replaceAll("\\[|\\]", "")
            .replaceAll("\\s", "");
    }

    // ~ Methods
    // ========================================================================================================

    public void afterPropertiesSet() throws Exception {
        Assert.hasLength(this.casLoginUrl, "loginUrl must be specified");
        Assert.hasLength(this.pathLogin, "pathLogin must be specified");
        Assert.hasLength(this.targetUrlParameter, "targetUrlParameter must be specified");
        Assert.notNull(this.serviceProperties, "serviceProperties must be specified");
        Assert.notNull(this.serviceProperties.getService(), "serviceProperties.getService() cannot be null.");
        Assert.notNull(this.urlHelper, "urlHelper cannot be null.");
    }

    public final void commence(final HttpServletRequest request, final HttpServletResponse response,
                               final AuthenticationException authenticationException) throws IOException, ServletException {

        //HttpServletRequest httpRequest = (HttpServletRequest) request;

		/*SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		UserDetails springSecurityUser = null;

		if (authentication != null) {
			if (authentication.getPrincipal() instanceof UserDetails) {
				springSecurityUser = (UserDetails) authentication.getPrincipal();
			}
		}
*/
        String resourcePath = new UrlPathHelper().getPathWithinApplication(request);
        log.debug("=====================================================================> RESOURCEPATH {}",
            resourcePath);
        boolean isPostMessage = false;
        if (request.getQueryString() != null) {
            isPostMessage = request.getQueryString().contains("postMessage");
        };
        log.debug("=====================================================================> postMessage {}", isPostMessage);
        // String doCASAuth = servletRequest.getHeader("X-request-AuthCAS");

        boolean isViewServingPage = resourcePath.startsWith(SecurityConfiguration.PROTECTED_PATH);
        log.debug("=====================================================================> isViewServingPage {}", isViewServingPage);

        if (this.pathLogin.equals(resourcePath) || isViewServingPage /*&& isPostMessage && springSecurityUser == null*/) {

            // if (doCASAuth != null) {
            //if (pathLogin.equals(resourcePath)) {

            final String urlEncodedService = createServiceUrl(request, response);
            final String redirectUrl = createRedirectUrl(urlEncodedService);

            log.debug("Pre-authenticated entry point called. Calling CAS Authentication with redirectURL {}",
                redirectUrl);
            preCommence(request, response);

            response.sendRedirect(redirectUrl);
        } else {
            log.debug("Pre-authenticated entry point called. Rejecting access");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Denied");
        }

    }

    /**
     * Constructs a new Service Url. The default implementation relies on the CAS client to do the
     * bulk of the work.
     *
     * @param request
     *            the HttpServletRequest
     * @param response
     *            the HttpServlet Response
     * @return the constructed service url. CANNOT be NULL.
     */
    protected String createServiceUrl(final HttpServletRequest request, final HttpServletResponse response) {
        String service = SecurityUtils.makeDynamicCASServiceUrl(urlHelper, request) + this.serviceProperties.getService();
        log.debug("createServiceUrl, service = {}", service);

        String uri = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");

        String servletPath = request.getServletPath();
        if (uri != null && !uri.isEmpty()) {
            service += String.format("?%s=%s", this.targetUrlParameter, uri);
        }
        log.debug("serviceURL = {}", service);
        return CommonUtils.constructServiceUrl(null, response, service, null, SERVICE_PARAMETER_NAMES,
            this.serviceProperties.getArtifactParameter(), this.encodeServiceUrlWithSessionId);
    }

    /**
     * Constructs the Url for Redirection to the CAS server. Default implementation relies on the
     * CAS client to do the bulk of the work.
     *
     * @param serviceUrl
     *            the service url that should be included.
     * @return the redirect url. CANNOT be NULL.
     */
    protected String createRedirectUrl(final String serviceUrl) {
        return CommonUtils.constructRedirectUrl(this.casLoginUrl, this.serviceProperties.getServiceParameter(),
            serviceUrl, this.serviceProperties.isSendRenew(), false);
    }

    /**
     * Template method for you to do your own pre-processing before the redirect occurs.
     *
     * @param request
     *            the HttpServletRequest
     * @param response
     *            the HttpServletResponse
     */
    protected void preCommence(final HttpServletRequest request, final HttpServletResponse response) {

    }

    /**
     * The enterprise-wide CAS login URL. Usually something like
     * <code>https://www.mycompany.com/cas/login</code>.
     *
     * @return the enterprise-wide CAS login URL
     */
    public final String getLoginUrl() {
        return this.casLoginUrl;
    }

    public final ServiceProperties getServiceProperties() {
        return this.serviceProperties;
    }

    public final void setLoginUrl(final String loginUrl) {
        this.casLoginUrl = loginUrl;
    }

    public final void setServiceProperties(final ServiceProperties serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    public void setPathLogin(String pathLogin) {
        this.pathLogin = pathLogin;
    }

    public void setUrlHelper(ServiceUrlHelper urlHelper) {
        this.urlHelper = urlHelper;
    }
}
