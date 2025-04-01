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
package org.esupportail.publisher.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apereo.portal.soffit.security.SoffitApiPreAuthenticatedProcessingFilter;
import org.esupportail.publisher.security.AjaxAuthenticationFailureHandler;
import org.esupportail.publisher.security.AjaxAuthenticationSuccessHandler;
import org.esupportail.publisher.security.AjaxLogoutSuccessHandler;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomSessionFixationProtectionStrategy;
import org.esupportail.publisher.security.CustomSingleSignOutFilter;
import org.esupportail.publisher.security.HasIpRangeExpressionCreator;
import org.esupportail.publisher.security.RememberCasAuthenticationEntryPoint;
import org.esupportail.publisher.security.RememberCasAuthenticationProvider;
import org.esupportail.publisher.security.RememberWebAuthenticationDetailsSource;
import org.esupportail.publisher.security.SoffitApiAuthenticationManager;
import org.esupportail.publisher.security.UserDetailsServiceFromCAS;
import org.esupportail.publisher.security.UserDetailsServiceFromSoffit;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.esupportail.publisher.web.FeedController;
import org.esupportail.publisher.web.filter.CsrfCookieGeneratorFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {

    private static final String APP_URI_LOGIN = "/app/login";

    private static final String APP_CONTEXT_PATH = "server.servlet.context-path";

    // preflight cache duration in the browser
    private static final Long maxAge = 600L; // 600 seconds = 10 minutes
    private static final String[] allowedMethods = {"GET","POST","PUT","DELETE","OPTIONS"};
    private static final String[] allowedHeaders = {"X-CSRF-TOKEN","Content-Type","Accept","Origin"};

    private final ESUPPublisherProperties esupPublisherProperties;

    private Environment env;
    @Inject
    private AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;

    @Inject
    private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

    @Inject
    private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

    //    @Inject
    //    private Http401UnauthorizedEntryPoint authenticationEntryPoint;

    // @Inject
    // private RememberMeServices rememberMeServices;

    public SecurityConfiguration(Environment environment, ESUPPublisherProperties esupPublisherProperties) {
        this.env = environment;
        this.esupPublisherProperties = esupPublisherProperties;
    }

    // @Bean
    // public PasswordEncoder passwordEncoder() {
    // return new BCryptPasswordEncoder();
    // }

    /*@Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enable(S)
            jsonConverter.setObjectMapper(objectMapper);
        return jsonConverter;
        }*/

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties sp = new ServiceProperties();
        Assert.hasText(esupPublisherProperties.getSecurity().getAuthUriFilterPath(), "The CAS URI service should be set to be able to apply CAS Auth Filter, see property 'app.security.auth-uri-filter-path'");
        sp.setService(esupPublisherProperties.getSecurity().getAuthUriFilterPath());
        sp.setSendRenew(false);
        sp.setAuthenticateAllArtifacts(true);
        return sp;
    }

    @Bean
    public ServiceUrlHelper serviceUrlHelper() {
        String ctxPath = env.getRequiredProperty(APP_CONTEXT_PATH);
        if (!ctxPath.startsWith("/")) ctxPath = "/" + ctxPath;
        final String protocol = esupPublisherProperties.getSecurity().getProtocol();
        Assert.isTrue(Lists.newArrayList("http://", "https://").contains(protocol), "Protocol param doesn't match required value, see property 'app.security.protocol'");
        final List<String> domainNames = Lists.newArrayList(esupPublisherProperties.getSecurity().getAuthorizedDomainNames());
        Assert.notEmpty(domainNames, "The list of the application Domain Names set shouldn't be empty, see property 'app.security.authorizedDomainNames'");
        ServiceUrlHelper serviceUrlHelper = new ServiceUrlHelper(ctxPath, domainNames, protocol, "/view/item/", "/news/item/");
        log.info("ServiceUrlHelper is configured with properties : {}", serviceUrlHelper.toString());
        return serviceUrlHelper;
    }

    @Bean String getCasTargetUrlParameter() {
        Assert.hasText(esupPublisherProperties.getSecurity().getRedirectParamName(), "Redirect Param Name shouldn't be null, see property 'app.security.redirectParamName'");
        return esupPublisherProperties.getSecurity().getRedirectParamName();
    }

    @Bean
    public SimpleUrlAuthenticationSuccessHandler authenticationSuccessHandler() {
        SimpleUrlAuthenticationSuccessHandler authenticationSuccessHandler = new SimpleUrlAuthenticationSuccessHandler();
        authenticationSuccessHandler.setDefaultTargetUrl("/");
        authenticationSuccessHandler.setTargetUrlParameter(getCasTargetUrlParameter());
        return authenticationSuccessHandler;
    }

    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> userDetailsServiceCAS() {
        return new UserDetailsServiceFromCAS();
    }

    @Bean
    public AuthenticationUserDetailsService<UsernamePasswordAuthenticationToken> userDetailsServiceSoffit() {
        return new UserDetailsServiceFromSoffit();
    }

    @Bean
    public RememberCasAuthenticationProvider casAuthenticationProvider() {
        RememberCasAuthenticationProvider casAuthenticationProvider = new RememberCasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(userDetailsServiceCAS());
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
        Assert.hasText(esupPublisherProperties.getSecurity().getIdKeyProvider(), "The CAS security Key should be set, see property 'app.security.idKeyProvider'");
        casAuthenticationProvider.setKey(esupPublisherProperties.getSecurity().getIdKeyProvider());
        return casAuthenticationProvider;
    }

    @Bean
    public SessionAuthenticationStrategy sessionStrategy() {
        SessionFixationProtectionStrategy sessionStrategy = new CustomSessionFixationProtectionStrategy(
            serviceUrlHelper(), serviceProperties(), getCasTargetUrlParameter());
        sessionStrategy.setMigrateSessionAttributes(false);
        return sessionStrategy;
    }

    @Bean
    public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
        return new Cas20ServiceTicketValidator(esupPublisherProperties.getCas().getUrlPrefix());
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        Assert.hasText(esupPublisherProperties.getSecurity().getAuthUriFilterPath(), "The CAS URI service should be set to be able to apply CAS Auth Filter, see property 'app.security.auth-uri-filter-path'");
        casAuthenticationFilter.setFilterProcessesUrl("/" + esupPublisherProperties.getSecurity().getAuthUriFilterPath());
        casAuthenticationFilter.setAuthenticationManager(authenticationManager());
        casAuthenticationFilter.setAuthenticationDetailsSource(new RememberWebAuthenticationDetailsSource(
            serviceUrlHelper(), serviceProperties(), getCasTargetUrlParameter()));
        casAuthenticationFilter.setSessionAuthenticationStrategy(sessionStrategy());
        casAuthenticationFilter.setAuthenticationFailureHandler(ajaxAuthenticationFailureHandler);
        casAuthenticationFilter.setAuthenticationSuccessHandler(ajaxAuthenticationSuccessHandler);
        // casAuthenticationFilter.setRequiresAuthenticationRequestMatcher(new
        // AntPathRequestMatcher("/login", "GET"));
        return casAuthenticationFilter;
    }

    @Bean
    public RememberCasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        RememberCasAuthenticationEntryPoint casAuthenticationEntryPoint = new RememberCasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setLoginUrl(esupPublisherProperties.getCas().getUrlLogin());
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
        casAuthenticationEntryPoint.setUrlHelper(serviceUrlHelper());
        //move to /app/login due to cachebuster instead of api/authenticate
        casAuthenticationEntryPoint.setPathLogin(APP_URI_LOGIN);
        return casAuthenticationEntryPoint;
    }

    @Bean
    public CustomSingleSignOutFilter singleSignOutFilter() {
        CustomSingleSignOutFilter singleSignOutFilter = new CustomSingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix(esupPublisherProperties.getCas().getUrlPrefix());
        return singleSignOutFilter;
    }

    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Collections.singletonList(casAuthenticationProvider()));
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = esupPublisherProperties.getCors();
        if (config.getAllowedMethods() == null || config.getAllowedMethods().isEmpty())
            config.setAllowedMethods(Arrays.asList(allowedMethods));
        if (config.getAllowedHeaders() == null || config.getAllowedHeaders().isEmpty())
            config.setAllowedHeaders(Arrays.asList(allowedHeaders));
        if (config.getMaxAge() == null)
            config.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        source.registerCorsConfiguration("/management/**", config);
        source.registerCorsConfiguration("/v3/api-docs", config);
        source.registerCorsConfiguration("/swagger-ui/**", config);
        return source;
    }

    @Bean
    public HasIpRangeExpressionCreator servicesPublishedIpAdressFilter(ESUPPublisherProperties esupPublisherProperties) {
        return new HasIpRangeExpressionCreator(esupPublisherProperties.getAuthorizedServices().getIpRanges());
    }

    @Bean
    public HasIpRangeExpressionCreator prometeusIpAdressFilter(ESUPPublisherProperties esupPublisherProperties) {
        return new HasIpRangeExpressionCreator(esupPublisherProperties.getMetrics().getPrometeusAuthorizedAcess().getIpRanges());
    }

    @Bean
    public AuthenticationManager soffitAuthenticationManager() {
        return new SoffitApiAuthenticationManager(userDetailsServiceSoffit());
    }

    @Bean
    @Order(1) // Priorité élevée pour les URLs liées à JWT
    public SecurityFilterChain configureSoffit(HttpSecurity http) throws Exception {

        final AbstractPreAuthenticatedProcessingFilter soffitFilter =
            new SoffitApiPreAuthenticatedProcessingFilter(esupPublisherProperties.getSecurity().getSoffitJwtSigningKey());

        soffitFilter.setAuthenticationManager(soffitAuthenticationManager());

        http.antMatcher("/news/**")
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .cors().and().csrf().disable()
            .addFilterBefore(soffitFilter, BasicAuthenticationFilter.class)
            .authorizeRequests()
            .antMatchers("/news/home").permitAll() // URLs publiques
            .antMatchers("/news/**").authenticated() // URLs nécessitant une authentification JWT
            .and().authenticationManager(soffitAuthenticationManager());

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.cors().and().addFilterAfter(new CsrfCookieGeneratorFilter(), CsrfFilter.class).exceptionHandling()
            .authenticationEntryPoint(casAuthenticationEntryPoint()).and()
            .addFilterBefore(casAuthenticationFilter(), BasicAuthenticationFilter.class)
            .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class);


        // .and()
        // .rememberMe()
        // .rememberMeServices(rememberMeServices)
        // .key(env.getProperty("jhipster.security.rememberme.key"))
        // .and()
        // .formLogin()
        // .loginProcessingUrl("/api/authentication")
        // .successHandler(ajaxAuthenticationSuccessHandler)
        // .failureHandler(ajaxAuthenticationFailureHandler)
        // .usernameParameter("j_username")
        // .passwordParameter("j_password")
        // .permitAll()

        http
            .headers()
            .frameOptions()
            .disable()
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .antMatchers("/app/**/*.{js,html}").permitAll()
            .antMatchers("/i18n/**").permitAll()
            .antMatchers("/content/**").permitAll()
            .antMatchers("/h2-console/**").permitAll()
            .antMatchers("/swagger-ui/**").permitAll()
            .antMatchers("/test/**").permitAll()
            .antMatchers("/app/**").authenticated()
            .antMatchers("/api/register").denyAll()
            .antMatchers("/api/activate").denyAll()
            .antMatchers("/api/authenticate").denyAll()
            .antMatchers("/api/logs/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/api/enums/**").permitAll()
            .antMatchers("/api/conf/**").permitAll()
            .antMatchers("/api/admin/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/api/**").hasAuthority(AuthoritiesConstants.USER)
            .antMatchers("/management/health").access("hasRole('" + AuthoritiesConstants.ADMIN + "') or (" + prometeusIpAdressFilter(esupPublisherProperties).getExpression()
                        + " or hasIpAddress('127.0.0.1/32') or hasIpAddress('::1'))")
            .antMatchers("/management/health/**").access("hasRole('" + AuthoritiesConstants.ADMIN + "') or (" + prometeusIpAdressFilter(esupPublisherProperties).getExpression()
                        + " or hasIpAddress('127.0.0.1/32') or hasIpAddress('::1'))")
            .antMatchers("/management/info").access("hasRole('" + AuthoritiesConstants.ADMIN + "') or (" + prometeusIpAdressFilter(esupPublisherProperties).getExpression()
                        + " or hasIpAddress('127.0.0.1/32') or hasIpAddress('::1'))")
            .antMatchers("/management/prometheus").access("hasRole('" + AuthoritiesConstants.ADMIN + "') or (" + prometeusIpAdressFilter(esupPublisherProperties).getExpression()
                        + " or hasIpAddress('127.0.0.1/32') or hasIpAddress('::1'))")
            .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/api-docs/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/published/**").access("hasRole('" + AuthoritiesConstants.ANONYMOUS + "') and (" + servicesPublishedIpAdressFilter(esupPublisherProperties).getExpression()
                        + " or hasIpAddress('127.0.0.1/32') or hasIpAddress('::1'))")
            .antMatchers(FeedController.PRIVATE_RSS_FEED_URL_PATH + "**").access("hasRole('" + AuthoritiesConstants.ANONYMOUS + "') and (" + servicesPublishedIpAdressFilter(esupPublisherProperties).getExpression()
                        + " or hasIpAddress('127.0.0.1/32') or hasIpAddress('::1'))")
            .antMatchers(FeedController.FEED_CONTROLLER_PATH + "/**").permitAll()
            .antMatchers(PROTECTED_PATH + "**").authenticated()
            .antMatchers("/view/**").permitAll()
            .antMatchers("/css/**").permitAll()
            .antMatchers("/images/**").permitAll()
            .antMatchers("/files/**").permitAll()
            .antMatchers("/fonts/**").permitAll()
            .antMatchers("/public/**").permitAll()
            .antMatchers("/static/**").permitAll()
            .antMatchers("/ui/**").permitAll()
            .anyRequest().denyAll()
            .and().authenticationManager(authenticationManager());
        http
            .logout()
            .logoutUrl("/api/logout")
            .logoutSuccessHandler(ajaxLogoutSuccessHandler)
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll();

        return http.build();
    }

    // Specific path where pages are served (outter of REST API with ANGULAR)
    public static final String PROTECTED_PATH = "/protected/";

    /*
     * This allows SpEL support in Spring Data JPA @Query definitions.
     *
     * Watch https://spring.io/blog/2014/07/15/spel-support-in-spring-data-jpa-query-definitions
     */
	/*@Bean
	EvaluationContextExtension securityExtension() {
	    return new EvaluationContextExtensionSupport() {
	        @Override
	        public String getExtensionId() {
	            return "security";
	        }

	        @Override
	        public SecurityExpressionRoot getRootObject() {
	            SecurityExpressionRoot ser = new SecurityExpressionRoot(SecurityContextHolder.getContext().getAuthentication()) {};
	            ser.setPermissionEvaluator(permissionEvaluator());
	            ser.setRoleHierarchy(roleHierarchy());
	            return ser;
	        }
	    };
	}*/
}
