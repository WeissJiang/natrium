package nano.web.scripting;

import org.graalvm.polyglot.Context;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
public class TypeScriptTests {

    @Test
    public void test() {
        var context = Context.newBuilder("js").allowIO(true).build();
        context.eval("js", """
                import ts from 'https://jspm.dev/typescript@4.0.2'
                """);
        var transpileModule = context.eval("js", "ts.transpileModule");
        assertNotNull(transpileModule);
    }
}
