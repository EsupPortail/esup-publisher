package org.esupportail.publisher.web.interceptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class CORSInterceptor extends HandlerInterceptorAdapter implements
		EnvironmentAware {

	private static final String ENV_APP_CORS = "app.cors.";

	private RelaxedPropertyResolver propertyResolver;

	@Override
	public void setEnvironment(Environment environment) {
		this.propertyResolver = new RelaxedPropertyResolver(environment,
				ENV_APP_CORS);
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		Set<String> allowedOrigins = new HashSet<String>(
				Arrays.asList(propertyResolver.getProperty("allowed.origins")
						.split(",")));

		String origin = request.getHeader("Origin");
		if (allowedOrigins.contains(origin)) {
			response.addHeader("Access-Control-Allow-Origin", origin);
			return true;
		} else {
			return false;
		}
	}
}