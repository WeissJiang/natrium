package nano.web.security;

import java.lang.annotation.*;

/**
 * Authentication with nano token
 *
 * @author cbdyzj
 * @see SecurityService
 * @see AuthenticationInterceptor
 * @see NanoPrivilege
 * @since 2020.9.20
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authorized {

    NanoPrivilege[] value() default NanoPrivilege.BASIC;
}
