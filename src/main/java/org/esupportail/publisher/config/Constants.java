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

/**
 * Application constants.
 */
public final class Constants {

	private Constants() {
	}

	public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
	public static final String SPRING_PROFILE_PRODUCTION = "prod";
	public static final String SPRING_PROFILE_TEST = "test";
	public static final String SPRING_PROFILE_FAST = "fast";
	public static final String SPRING_PROFILE_API_DOCS = "api-docs";
	public static final String SPRING_PROFILE_DBH2 = "db-h2";
	//public static final String SPRING_PROFILE_CLOUD = "cloud";
	public static final String SYSTEM_ACCOUNT = "system";
    public static final String SPRING_PROFILE_LDAP_GROUP = "ldapgrp";
    public static final String SPRING_PROFILE_WS_GROUP = "wsgrp";

}