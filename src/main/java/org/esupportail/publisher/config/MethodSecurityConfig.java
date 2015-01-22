package org.esupportail.publisher.config;

import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomPermissionEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public RoleHierarchyVoter roleVoter() {
		return new RoleHierarchyVoter(roleHierarchy());
	}

	@Bean
	public RoleHierarchy roleHierarchy() {
		RoleHierarchyImpl rhi = new RoleHierarchyImpl();
		rhi.setHierarchy(AuthoritiesConstants.ADMIN + " > " + AuthoritiesConstants.USER + " "
				+ AuthoritiesConstants.USER + " > " + AuthoritiesConstants.AUTHENTICATED + " "
				+ AuthoritiesConstants.AUTHENTICATED + " > " + AuthoritiesConstants.ANONYMOUS);
		return rhi;
	}

	@Bean
	public PermissionEvaluator permissionEvaluator() {
		return new CustomPermissionEvaluator();
	}

	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setPermissionEvaluator(permissionEvaluator());
		expressionHandler.setRoleHierarchy(roleHierarchy());
		expressionHandler.setApplicationContext(applicationContext);
		return expressionHandler;
	}

	@Bean
	public DefaultWebSecurityExpressionHandler webExpressionHandler() {
		DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
		expressionHandler.setRoleHierarchy(roleHierarchy());
		expressionHandler.setPermissionEvaluator(permissionEvaluator());
		expressionHandler.setApplicationContext(applicationContext);
		return expressionHandler;
	}

	@Bean
	public DefaultMethodSecurityExpressionHandler expressionHandler() {
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setPermissionEvaluator(permissionEvaluator());
		expressionHandler.setRoleHierarchy(roleHierarchy());
		expressionHandler.setApplicationContext(applicationContext);
		return expressionHandler;
	}

	/*protected AccessDecisionManager accessDecisionManager() {
	    List<AccessDecisionVoter> decisionVoters = new ArrayList<AccessDecisionVoter>();
	    WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
	    webExpressionVoter.setExpressionHandler(webExpressionHandler());
	    decisionVoters.add(webExpressionVoter);
	    decisionVoters.add(roleVoter());
	    return new AffirmativeBased(decisionVoters);
	}*/
	/*@Bean
	@Override
	protected AccessDecisionManager accessDecisionManager() {
	    List<AccessDecisionVoter> decisionVoters = new ArrayList<AccessDecisionVoter>();*/
	/*WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
	webExpressionVoter.setExpressionHandler(webExpressionHandler());
	decisionVoters.add(webExpressionVoter);*/
	/*ExpressionBasedPreInvocationAdvice expressionAdvice = new ExpressionBasedPreInvocationAdvice();
	expressionAdvice.setExpressionHandler(getExpressionHandler());
	decisionVoters.add(new PreInvocationAuthorizationAdviceVoter(expressionAdvice));
	decisionVoters.add(new Jsr250Voter());
	decisionVoters.add(roleVoter());
	decisionVoters.add(new AuthenticatedVoter());
	return new AffirmativeBased(decisionVoters);
	}*/
}
