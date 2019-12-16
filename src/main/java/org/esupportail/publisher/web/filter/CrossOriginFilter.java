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
package org.esupportail.publisher.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@Slf4j
public class CrossOriginFilter implements Filter {

	// private RelaxedPropertyResolver propertyResolver;

	// @Override
	// public void setEnvironment(Environment environment) {
	// this.propertyResolver = new RelaxedPropertyResolver(environment,
	// ENV_APP_CORS);
	// }

	// @Override
	// public void doFilter(ServletRequest req, ServletResponse res,
	// FilterChain chain) throws IOException, ServletException {
	// HttpServletResponse response = (HttpServletResponse) res;
	//
	// Set<String> allowedOrigins = new HashSet<String>(
	// Arrays.asList(propertyResolver.getProperty("allowed.origins")
	// .split(",")));
	//
	// String origin = req.getHeader("Origin");
	// if (allowedOrigins.contains(origin)) {
	// response.addHeader("Access-Control-Allow-Origin", origin);
	// return true;
	// }
	// response.setHeader("Access-Control-Allow-Origin", "*");
	// response.setHeader("Access-Control-Allow-Methods",
	// "POST, GET, PUT, DELETE");
	// response.setHeader("Access-Control-Max-Age", "3600");
	// response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
	// chain.doFilter(req, res);
	// }
	//
	// @Override
	// public void init(FilterConfig filterConfig) {
	// }
	//
	// @Override
	// public void destroy() {
	// }
	private Environment env;
	
	private List<String> allowedOrigins = new ArrayList<String>();

	public CrossOriginFilter(Environment environment) throws ServletException {
		this.env = environment;
		this.setAllowedOrigins(env.getProperty("app.cors.allowed.origins", String.class));
	}

	// preflight cache duration in the browser
	private String maxAge = "600"; // 600 seconds = 10 minutes
	private String allowedMethods = "GET,POST,PUT,DELETE";
	private String allowedHeaders = "X-CSRF-TOKEN,Content-Type,Accept,Origin";

	public void destroy() {
	}

	public void init(FilterConfig config) {
	}

	public void setAllowedOrigins(String origins) {
		allowedOrigins.clear();
		allowedOrigins.addAll(Arrays.asList(origins.split(",")));
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		handle((HttpServletRequest) request, (HttpServletResponse) response,
				chain);
	}

	private void handle(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String origin = request.getHeader("Origin");
		log.debug("origin {}", origin);
		log.debug("allowed origins {}", allowedOrigins);
		if (origin != null && allowedOrigins.contains(origin)) {
			response.setHeader("Access-Control-Allow-Origin", origin);
			response.setHeader("Access-Control-Allow-Credentials", "true");
			if (isPreflightRequest(request)) {
				response.setHeader("Access-Control-Max-Age", maxAge);
				response.setHeader("Access-Control-Allow-Methods",
						allowedMethods);
				response.setHeader("Access-Control-Allow-Headers",
						allowedHeaders);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean isPreflightRequest(HttpServletRequest request) {
		return "OPTIONS".equalsIgnoreCase(request.getMethod())
				&& request.getHeader("Access-Control-Request-Method") != null;
	}

}
