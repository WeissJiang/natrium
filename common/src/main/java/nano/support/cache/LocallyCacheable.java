package nano.support.cache;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocallyCacheable {

    /**
     * local cache option
     */
    String value() default "";
}
