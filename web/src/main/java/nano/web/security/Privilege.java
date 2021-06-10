package nano.web.security;

/**
 * Token privilege
 *
 * @see Authorized
 * @see nano.web.nano.entity.NanoToken
 */
public abstract class Privilege {
   public static final String BASIC = "BASIC";
   public static final String NANO_API = "NANO_API";
   public static final String ACCOUNTING = "ACCOUNTING";
   public static final String MAIL = "MAIL";
}
