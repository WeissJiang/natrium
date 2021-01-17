package nano.support.cache;

import java.lang.annotation.*;

/**
 * Local cacheable
 *
 * @author cbdyzj
 * @since 2020.10.27
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocallyCacheable {

    String value() default "";
}
