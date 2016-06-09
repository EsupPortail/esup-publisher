package org.esupportail.publisher.config;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.enums.OperatorType;
import org.esupportail.publisher.domain.enums.StringEvaluationMode;
import org.esupportail.publisher.security.*;
import org.esupportail.publisher.service.bean.AuthoritiesDefinition;
import org.esupportail.publisher.service.bean.IAuthoritiesDefinition;
import org.esupportail.publisher.service.bean.IpVariableHolder;
import org.esupportail.publisher.service.evaluators.IEvaluation;
import org.esupportail.publisher.service.evaluators.OperatorEvaluation;
import org.esupportail.publisher.service.evaluators.UserAttributesEvaluation;
import org.esupportail.publisher.service.evaluators.UserMultivaluedAttributesEvaluation;
import org.esupportail.publisher.web.filter.CsrfCookieGeneratorFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebMvcSecurity
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String CAS_URL_LOGIN = "cas.url.login";
    private static final String CAS_URL_LOGOUT = "cas.url.logout";
    private static final String CAS_URL_PREFIX = "cas.url.prefix";
    private static final String CAS_SERVICE_URL = "app.service.security";
    private static final String APP_URI_LOGIN = "/app/login";
    private static final String APP_ADMIN_USER_NAME = "app.admin.userName";
    private static final String APP_ADMIN_GROUP_NAME = "app.admin.groupName";

    @Inject
    private Environment env;

    @Inject
    private AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;

    @Inject
    private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

    @Inject
    private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

    //    @Inject
    //    private Http401UnauthorizedEntryPoint authenticationEntryPoint;

    @Inject
    private AuthenticationUserDetailsService<CasAssertionAuthenticationToken> userDetailsService;

    @Inject
    private IpVariableHolder ipVariableHolder;

    // @Inject
    // private RememberMeServices rememberMeServices;

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
    public IAuthoritiesDefinition mainRolesDefs() {
        Assert.notNull(env);
        AuthoritiesDefinition defs = new AuthoritiesDefinition();

        Set<IEvaluation> set = new HashSet<IEvaluation>();

        final String userAdmin = env.getProperty(APP_ADMIN_USER_NAME);
        if (userAdmin != null && !userAdmin.isEmpty()) {
            final UserAttributesEvaluation uae1 = new UserAttributesEvaluation("uid", userAdmin, StringEvaluationMode.EQUALS);
            set.add(uae1);
        }

        final String groupAdmin = env.getProperty(APP_ADMIN_GROUP_NAME);
        if (groupAdmin != null && !groupAdmin.isEmpty()) {
            final UserAttributesEvaluation uae2 = new UserMultivaluedAttributesEvaluation("isMemberOf", groupAdmin, StringEvaluationMode.EQUALS);
            set.add(uae2);
        }

        Assert.isTrue(set.size() > 0, "Properties '" + APP_ADMIN_USER_NAME + "' or '" + APP_ADMIN_GROUP_NAME
            + "' aren't defined in properties, there are needed to define an Admin user");


        OperatorEvaluation admins = new OperatorEvaluation(OperatorType.OR, set);
        defs.setAdmins(admins);

        UserMultivaluedAttributesEvaluation umae = new UserMultivaluedAttributesEvaluation("isMemberOf",
            "((esco)|(cfa)|(clg37)|(agri)|(coll)):Applications:((Publisher)|(Publication_annonces)):.*",
            StringEvaluationMode.MATCH);
        defs.setUsers(umae);

        return defs;
    }

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties sp = new ServiceProperties();
        sp.setService(env.getRequiredProperty(CAS_SERVICE_URL));
        sp.setSendRenew(false);
        return sp;
    }

    @Bean
    public SimpleUrlAuthenticationSuccessHandler authenticationSuccessHandler() {
        SimpleUrlAuthenticationSuccessHandler authenticationSuccessHandler = new SimpleUrlAuthenticationSuccessHandler();
        authenticationSuccessHandler.setDefaultTargetUrl("/");
        authenticationSuccessHandler.setTargetUrlParameter("spring-security-redirect");
        return authenticationSuccessHandler;
    }

    @Bean
    public RememberCasAuthenticationProvider casAuthenticationProvider() {
        RememberCasAuthenticationProvider casAuthenticationProvider = new RememberCasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(userDetailsService);
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
        casAuthenticationProvider.setKey("an_id_for_this_auth_provider_only");
        return casAuthenticationProvider;
    }

    @Bean
    public SessionAuthenticationStrategy sessionStrategy() {
        SessionFixationProtectionStrategy sessionStrategy = new CustomSessionFixationProtectionStrategy();
        sessionStrategy.setMigrateSessionAttributes(false);
        return sessionStrategy;
    }

    @Bean
    public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
        return new Cas20ServiceTicketValidator(env.getRequiredProperty(CAS_URL_PREFIX));
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(authenticationManager());
        casAuthenticationFilter.setAuthenticationDetailsSource(new RememberWebAuthenticationDetailsSource());
        casAuthenticationFilter.setSessionAuthenticationStrategy(sessionStrategy());
        casAuthenticationFilter.setAuthenticationFailureHandler(ajaxAuthenticationFailureHandler);
        //casAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        casAuthenticationFilter.setAuthenticationSuccessHandler(ajaxAuthenticationSuccessHandler);
        // casAuthenticationFilter.setRequiresAuthenticationRequestMatcher(new
        // AntPathRequestMatcher("/login", "GET"));
        return casAuthenticationFilter;
    }

    @Bean
    public RememberCasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        RememberCasAuthenticationEntryPoint casAuthenticationEntryPoint = new RememberCasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setLoginUrl(env.getRequiredProperty(CAS_URL_LOGIN));
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
        //move to /app/login due to cachebuster instead of api/authenticate
        casAuthenticationEntryPoint.setPathLogin(APP_URI_LOGIN);
        return casAuthenticationEntryPoint;
    }

    @Bean
    public CustomSingleSignOutFilter singleSignOutFilter() {
        CustomSingleSignOutFilter singleSignOutFilter = new CustomSingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix(env.getRequiredProperty(CAS_URL_PREFIX));
        return singleSignOutFilter;
    }

    @Inject
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(casAuthenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/scripts/**/*.{js,html}").antMatchers("/bower_components/**")
            .antMatchers("/i18n/**").antMatchers("/assets/**").antMatchers("/swagger-ui/**")
            .antMatchers("/test/**").antMatchers("/console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(new CsrfCookieGeneratorFilter(), CsrfFilter.class).exceptionHandling()
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
            .authorizeRequests()
            .antMatchers("/app/**").authenticated()
            .antMatchers("/published/**").access("hasRole('" + AuthoritiesConstants.ANONYMOUS + "') and (hasIpAddress('" + ipVariableHolder.getIpRange()
                + "') or hasIpAddress('127.0.0.1/32') or hasIpAddress('::1'))")
            .antMatchers("/api/register").denyAll()
            .antMatchers("/api/activate").denyAll()
            .antMatchers("/api/authenticate").denyAll()
            .antMatchers("/api/logs/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/api/enums/**").permitAll()
            .antMatchers("/api/**").hasAuthority(AuthoritiesConstants.USER)
            .antMatchers("/metrics/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/health/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/dump/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/shutdown/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/beans/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/configprops/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/info/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/autoconfig/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/env/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/api-docs/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/protected/**").authenticated();
        http
            .logout()
            .logoutUrl("/api/logout")
            .logoutSuccessHandler(ajaxLogoutSuccessHandler)
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll();

    }

    /**
     * This allows SpEL support in Spring Data JPA @Query definitions.
     *
     * See https://spring.io/blog/2014/07/15/spel-support-in-spring-data-jpa-query-definitions
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
