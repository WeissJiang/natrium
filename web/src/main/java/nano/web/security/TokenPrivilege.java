package nano.web.security;

/**
 * Token的权限
 *
 * @see Authorized
 * @see nano.web.security.entity.NanoToken
 */
public enum TokenPrivilege {
    BASIC, NANO_API, TELEGRAM_WEBHOOK
}
