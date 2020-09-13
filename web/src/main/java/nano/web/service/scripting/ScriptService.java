package nano.web.service.scripting;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
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

    private boolean initialized = false;

    private Context context;

    private Value tsc;
    private Value lessc;

    @NonNull
    private final Scripting scripting;

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
        // typescript
        @Cleanup var typescript = getReader(this.scripting.getTypescript());
        this.context.eval(Source.newBuilder("js", typescript, "typescript").buildLiteral());
        this.tsc = this.context.eval("js", getString(this.scripting.getTsc()));
    }

    @SneakyThrows
    private synchronized void initLess() {
        // shim browser api
        context.eval("js", getString(this.scripting.getBrowserShim()));
        // less
        @Cleanup var less = getReader(this.scripting.getLess());
        context.eval(Source.newBuilder("js", less, "less").buildLiteral());
        this.lessc = context.eval("js", getString(this.scripting.getLessc()));
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
        promise.invokeMember("then", (Consumer<String>) (injection) -> {
            ref[0] = injection;
            latch.countDown();
        }).invokeMember("catch", (Consumer<Throwable>) (ex) -> {
            ref[1] = ex.getMessage();
            latch.countDown();
        });
        latch.await();
        Assert.notNull(ref[0], ref[1]);
        return ref[0];
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

    @SneakyThrows
    private static String getString(Resource resource) {
        @Cleanup var inputStream = resource.getInputStream();
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    private static Reader getReader(Resource resource) {
        return new InputStreamReader(resource.getInputStream());
    }
}
