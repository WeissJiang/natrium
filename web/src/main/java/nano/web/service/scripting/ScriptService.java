package nano.web.service.scripting;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * JavaScript service
 *
 * @see <a href="https://github.com/Microsoft/TypeScript/wiki/Using-the-Compiler-API">Using the Compiler API</a>
 */
@Service
@RequiredArgsConstructor
public class ScriptService {

    private static final String TYPESCRIPT_PATH = "classpath:/META-INF/resources/webjars/typescript/3.9.5/lib/typescript.js";

    private Value transpileModule;

    private final ResourceLoader resourceLoader;

    /**
     * Init script engine
     */
    @PostConstruct
    public synchronized void init() throws IOException {
        var context = Context.create("js");
        var resource = this.resourceLoader.getResource(TYPESCRIPT_PATH);
        @Cleanup var in = resource.getInputStream();
        var source = Source.newBuilder("js", new InputStreamReader(in), "typescript").buildLiteral();
        context.eval(source);
        var wrapper = """
                function transpileModule(script) {
                    return ts.transpileModule(script, {
                        compilerOptions: {
                            lib: ['es2018', 'dom'],
                            allowJs: true,
                            target: 'es2018',
                            jsx: ts.JsxEmit.React,
                        }
                    })
                }
                transpileModule
                """;
        this.transpileModule = context.eval("js", wrapper);
    }

    /**
     * Uses TypeScript
     *
     * @param origin original script
     * @return javascript
     */
    public synchronized String transpileScriptModule(@NonNull String origin) {
        try {
            var scriptValue = this.transpileModule.execute(origin);
            return scriptValue.getMember("outputText").asString();
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String eval(@NonNull String script) {
        return Context.create("js").eval("js", script).asString();
    }
}
