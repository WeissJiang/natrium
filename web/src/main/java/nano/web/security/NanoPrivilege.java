package nano.web.security;

/**
 * Token privilege
 *
 * @see Authorized
 * @see nano.web.nano.entity.NanoToken
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
