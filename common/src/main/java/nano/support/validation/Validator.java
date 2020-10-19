package nano.support.validation;

import org.jetbrains.annotations.Nullable;

/**
 * @see Validating
 */
@FunctionalInterface
public interface Validator {

    @Nullable String validate(Object... args);
}
