package nano.web.scripting;

import org.graalvm.polyglot.Context;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ScriptingTests {

    @Autowired
    public Scripting scripting;

    @Test
    public void testBase64() {
        var hello = "hello";
        var utf8 = StandardCharsets.UTF_8;
        var encode = Base64.encode(hello.getBytes(utf8));
        var decode = this.scripting.eval("Base64.decode('%s')".formatted(new String(encode, utf8)));
        assertEquals(hello, decode);
    }

    @Disabled
    @Test
    public void testTypeScript() {
        var context = Context.newBuilder("js").allowIO(true).build();
        context.eval("js", """
                import ts from 'https://jspm.dev/typescript@4.0.2'
                """);
        var transpileModule = context.eval("js", "ts.transpileModule");
        assertNotNull(transpileModule);
    }
}
