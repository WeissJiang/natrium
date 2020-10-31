package nano.web.scripting;

import org.graalvm.polyglot.Context;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class Scripting {

    /**
     * @see <a href="https://html.spec.whatwg.org/multipage/scripting.html#scriptingLanguages">Scripting languages</a>
     */
    public static final String TEXT_JAVASCRIPT = "text/javascript";

    private String base64Script;

    public @NotNull String eval(@NotNull String script) {
        try {
            // eval
            var value = this.getContext().eval("js", script);
            return String.valueOf(value);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @Value("classpath:/scripting/base64.js")
    public void setBase64Script(@NotNull Resource base64ScriptResource) throws IOException {
        var is = base64ScriptResource.getInputStream();
        try (is) {
            this.base64Script = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private @NotNull Context getContext() {
        var context = Context.create("js");
        // Base64
        Assert.notNull(this.base64Script, "this.base64Script is null");
        context.eval("js", this.base64Script);
        return context;
    }

}
