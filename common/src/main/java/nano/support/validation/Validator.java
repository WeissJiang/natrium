package nano.support.validation;

import org.jetbrains.annotations.Nullable;

/**
 * @see Validated
 */
@FunctionalInterface
public interface Validator {

    @Nullable String validate(Object... args);
}
