package nano.web.scripting;

import org.graalvm.polyglot.Context;
import org.jetbrains.annotations.NotNull;

public class Scripting {

    /**
     * @see <a href="https://html.spec.whatwg.org/multipage/scripting.html#scriptingLanguages">Scripting languages</a>
     */
    public static final String TEXT_JAVASCRIPT = "text/javascript";

    public static String eval(@NotNull String script) {
        try {
            var value = Context.create("js").eval("js", script);
            return String.valueOf(value);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
}
