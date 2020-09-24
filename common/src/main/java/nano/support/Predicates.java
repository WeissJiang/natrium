package nano.support;

import java.util.function.Predicate;

import static java.util.regex.Pattern.compile;

public abstract class Predicates {

    public static final Predicate<String> ALPHANUM = compile("^[a-zA-Z0-9]+$").asPredicate();

    /**
     * @see <a href="https://www.rfc-editor.org/rfc/rfc5322.txt">Internet Message Format</a>
     */
    public static final Predicate<String> EMAIL = compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$").asPredicate();
}
