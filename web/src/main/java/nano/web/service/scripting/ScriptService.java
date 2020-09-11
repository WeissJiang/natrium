package nano.web.service.scripting;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nano.support.SimpleResourceLoader;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * JavaScript service
 *
 * @see <a href="https://github.com/Microsoft/TypeScript/wiki/Using-the-Compiler-API">Using the Compiler API</a>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScriptService {

    private static final String TYPESCRIPT_PATH = "classpath:/scripting/typescript/lib/typescript.js";
    private static final String LESS_PATH = "classpath:/scripting/less/dist/less.js";

    private boolean initialized = false;

    private Context context;

    private Value tsc;
    private Value lessc;

    @NonNull
    private final SimpleResourceLoader resourceLoader;

    /**
     * Init script engine
     */
    @SneakyThrows
    private synchronized void initScriptEngine() {
        if (log.isInfoEnabled()) {
            log.info("Initializing {}", this.getClass().getSimpleName());
        }
        long startTime = System.currentTimeMillis();
        this.initContext();
        this.initTypeScript();
        this.initLess();
        if (log.isInfoEnabled()) {
            log.info("Completed initialization in {} seconds", (System.currentTimeMillis() - startTime) / 1000.0);
        }
    }

    @SneakyThrows
    private synchronized void initContext() {
        var callback = Consumer.class.getMethod("accept", Object.class);
        var hostAccess = HostAccess.newBuilder().allowAccess(callback).build();
        this.context = Context.newBuilder("js").allowHostAccess(hostAccess).build();
    }

    @SneakyThrows
    private synchronized void initTypeScript() {
        var rl = this.resourceLoader;
        // typescript
        @Cleanup var typescript = rl.getResourceAsReader(TYPESCRIPT_PATH);
        this.context.eval(Source.newBuilder("js", typescript, "typescript").buildLiteral());
        this.tsc = this.context.eval("js", rl.getResourceAsString("classpath:/scripting/tsc.js"));
    }

    @SneakyThrows
    private synchronized void initLess() {
        var rl = this.resourceLoader;
        // shim browser api
        context.eval("js", rl.getResourceAsString("classpath:/scripting/browser_shim.js"));
        // less
        @Cleanup var less = rl.getResourceAsReader(LESS_PATH);
        context.eval(Source.newBuilder("js", less, "less").buildLiteral());
        this.lessc = context.eval("js", rl.getResourceAsString("classpath:/scripting/lessc.js"));
    }


    /**
     * Uses TypeScript
     *
     * @param origin original script
     * @return javascript
     */
    public synchronized String transpileScriptModule(@NonNull String origin) {
        this.ensureInitialized();
        try {
            var scriptValue = this.tsc.execute(origin);
            return scriptValue.getMember("outputText").asString();
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }


    @SneakyThrows
    public synchronized String transpileStyleModule(@NonNull String origin) {
        this.ensureInitialized();
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

    private synchronized void ensureInitialized() {
        if (!this.initialized) {
            this.initScriptEngine();
            this.initialized = true;
        }
    }

    public String eval(@NonNull String script) {
        return Context.create("js").eval("js", script).asString();
    }
}
