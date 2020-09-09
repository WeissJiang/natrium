package nano.web.service.scripting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static nano.support.jdbc.SqlUtils.slim;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ScriptServiceTests {

    @Autowired
    public ScriptService scriptService;

    @Test
    public void testCompileToEsm() {
        var js = """
                const hi = (props) => <div>hello world<div>'
                """;
        var compiled = this.scriptService.transpileScriptModule(js);
        assertNotNull(compiled);
        var expected = """
                const hi = (props) => React.createElement("div", null, "hello world", React.createElement("div", null, "'"));
                """;
        assertEquals(expected.trim(), slim(compiled));
    }
}
