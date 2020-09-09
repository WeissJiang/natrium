package nano.web.service.scripting;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * JavaScript service
 *
 * @see <a href="https://github.com/Microsoft/TypeScript/wiki/Using-the-Compiler-API">Using the Compiler API</a>
 */
@Service
@RequiredArgsConstructor
public class ScriptService {

    private static final String TYPESCRIPT_PATH = "classpath:/META-INF/resources/webjars/typescript/3.9.5/lib/typescript.js";
    private static final String LESS_PATH = "classpath:/META-INF/resources/webjars/less/3.11.3/dist/less.js";

    private Value tsc;
    private Value lessc;

    private final ResourceLoader resourceLoader;

    /**
     * Init script engine
     */
    @PostConstruct
    public synchronized void init() throws IOException, NoSuchMethodException {
        var callback = Consumer.class.getMethod("accept", Object.class);
        var hostAccess = HostAccess.newBuilder().allowAccess(callback).build();
        var context = Context.newBuilder("js").allowHostAccess(hostAccess).build();
        // typescript
        @Cleanup var typescript = this.resourceLoader.getResource(TYPESCRIPT_PATH).getInputStream();
        context.eval(Source.newBuilder("js", new InputStreamReader(typescript), "typescript").buildLiteral());
        this.tsc = context.eval("js", """
                function tsc(script) {
                    return ts.transpileModule(script, {
                        compilerOptions: {
                            lib: ['es2018', 'dom'],
                            allowJs: true,
                            target: 'es2018',
                            jsx: ts.JsxEmit.React,
                        }
                    })
                }
                tsc
                """);
        // less
        @Cleanup var less = this.resourceLoader.getResource(LESS_PATH).getInputStream();
        context.eval("js", """
                const document = {
                    getElementsByTagName() {
                        return []
                    },
                    createElement() {
                        return {
                            appendChild() { }
                        }
                    },
                    createTextNode() { },
                    currentScript: 'less',
                    head: {
                        appendChild() { },
                        removeChild() { }
                    }
                }
                const window = {
                    location: {},
                    document,
                }
                """);
        context.eval(Source.newBuilder("js", new InputStreamReader(less), "less").buildLiteral());
        this.lessc = context.eval("js", """
                function lessc(style) {
                    return less.render(style)
                }
                lessc
                """);
    }

    /**
     * Uses TypeScript
     *
     * @param origin original script
     * @return javascript
     */
    public synchronized String transpileScriptModule(@NonNull String origin) {
        try {
            var scriptValue = this.tsc.execute(origin);
            return scriptValue.getMember("outputText").asString();
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String eval(@NonNull String script) {
        return Context.create("js").eval("js", script).asString();
    }

    @SneakyThrows
    public synchronized String transpileStyleModule(@NonNull String origin) {
        var promise = this.lessc.execute(origin);
        var latch = new CountDownLatch(1);
        var ref = new String[2];
        promise.invokeMember("then", (Consumer<Map<String, Object>>) (result) -> {
            var css = result.get("css");
            Assert.notNull(css, "css is null");
            ref[0] = String.valueOf(css);
            latch.countDown();
        }).invokeMember("catch", (Consumer<Throwable>) (ex) -> {
            ref[1] = ex.getMessage();
            latch.countDown();
        });
        latch.await();
        Assert.notNull(ref[0], ref[1]);
        var escaped = ref[0].replaceAll("\r|\n|\r\n", "\\\\n");
        return """
                ; (function (text) {
                    var style = document.createElement('style');
                    style.innerHTML = text;
                    document.head.appendChild(style);
                })('%s')
                """.formatted(escaped);
    }

    public static void main(String[] args) throws NoSuchMethodException {
        System.out.println(Consumer.class.getMethod("accept", Object.class));
    }
}
