package nano.web.security;

/**
 * Token的权限
 *
 * @see Authorized
 * @see nano.web.security.entity.NanoToken
 */
public enum NanoPrivilege {

    /**
     * Basic API
     */
    BASIC,

    /**
     * System API
     */
    NANO_API,

    /**
     * Mail service
     */
    MAIL,
}
