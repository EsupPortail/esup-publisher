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
package org.esupportail.publisher.web.interceptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerInterceptor;
@Slf4j
public class CORSInterceptor implements HandlerInterceptor {

	@Autowired
	private Environment env;

	private Set<String> allowedOrigins;

	public CORSInterceptor() {
		allowedOrigins = new HashSet<>(
				Arrays.asList(env.getProperty("app.cors.allowed.origins")
						.split(",")));
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		log.debug("Entering into {}", this.getClass().getName());

		String origin = request.getHeader("Origin");
		if (allowedOrigins.contains(origin)) {
			log.debug("Allowed Origin {}", origin);
			response.addHeader("Access-Control-Allow-Origin", origin);
			return true;
		} else {
			log.debug("Origin {} isn't allowed from configured {}", origin, allowedOrigins);
			return false;
		}
	}
}