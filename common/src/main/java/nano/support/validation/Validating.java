package nano.support.validation;

import java.lang.annotation.*;

/**
 * @see Validator
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validating {

    Class<? extends Validator>[] value() default {};
}
