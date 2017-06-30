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

public final class SecurityConstants {

	public static final String IS_ROLE_ADMIN = "hasRole('" + AuthoritiesConstants.ADMIN + "')";

	public static final String IS_ROLE_USER = "hasRole('" + AuthoritiesConstants.USER + "')";

	public static final String IS_ROLE_AUTHENTICATED = "hasRole('" + AuthoritiesConstants.AUTHENTICATED + "')";

	public static final String IS_ROLE_ANONYMOUS = "hasRole('" + AuthoritiesConstants.ANONYMOUS + "')";

	// usefull for Permissions for User Role

	public static final String PERM_MANAGER = "MANAGER";

	public static final String PERM_EDITOR = "EDITOR";

	public static final String PERM_CONTRIBUTOR = "CONTRIBUTOR";

	public static final String PERM_LOOKOVER = "LOOKOVER";

    // usefull for Permissions for ContextType

    public static final String CTX_ORGANIZATION = "ORGANIZATION";
    /** Context Publisher. */
    public static final String CTX_PUBLISHER = "PUBLISHER";
    /** Context Category. */
    public static final String CTX_CATEGORY = "CATEGORY";
    /** Context Topic. */
    public static final String CTX_FEED = "FEED";
    /** Context Item. */
    public static final String CTX_ITEM = "ITEM";

}
