package nano.web.scripting;

import nano.support.io.SimpleResource;
import org.graalvm.polyglot.Context;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class Scripting {

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
    public void setBase64Script(@NotNull Resource script) {
        this.base64Script = new SimpleResource(script).getAsString();
    }

    private @NotNull Context getContext() {
        var context = Context.create("js");
        // Base64
        Assert.notNull(this.base64Script, "this.base64Script is null");
        context.eval("js", this.base64Script);
        return context;
    }

}
