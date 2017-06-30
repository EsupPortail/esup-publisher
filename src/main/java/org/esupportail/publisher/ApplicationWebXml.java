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
package org.esupportail.publisher;

import org.esupportail.publisher.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * This is an helper Java class that provides an alternative to creating a web.xml.
 */
public class ApplicationWebXml extends SpringBootServletInitializer {

	private final Logger log = LoggerFactory.getLogger(ApplicationWebXml.class);

	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.profiles(addDefaultProfile())
                .showBanner(false)
				.sources(Application.class);
	}

	/**
	 * Set a default profile if it has not been set.
	 * <p/>
	 * <p>
	 * Please use -Dspring.profiles.active=dev
	 * </p>
	 */
	private String addDefaultProfile() {
		String profile = System.getProperty("spring.profiles.active");
		if (profile != null) {
			log.info("Running with Spring profile(s) : {}", profile);
			return profile;
		}

		log.warn("No Spring profile configured, running with default configuration");
		return Constants.SPRING_PROFILE_DEVELOPMENT;
	}
}
