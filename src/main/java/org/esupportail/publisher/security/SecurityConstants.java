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
