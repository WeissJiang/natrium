package nano.web.service.scripting;

import org.graalvm.polyglot.Context;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class TypescriptTests {

    @Test
    public void test() {
        var context = Context.newBuilder("js").allowIO(true).build();
        context.eval("js", """
                import ts from 'https://jspm.dev/typescript@4.0.2'
                """);
        var transpileModule = context.eval("js", "ts.transpileModule");
        Assertions.assertNotNull(transpileModule);
    }
}
