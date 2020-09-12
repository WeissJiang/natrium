package nano.web.security;

import java.lang.annotation.*;

/**
 * 使用nano token鉴权
 *
 * @see SecurityService
 * @see AuthenticationInterceptor
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authorized {
}
