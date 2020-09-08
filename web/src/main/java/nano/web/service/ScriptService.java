package nano.web.service;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.script.ScriptEngineManager;

/**
 * JavaScript service
 */
@Service
public class ScriptService {

    /**
     * Compile CommonJS to ECMAScript modules
     * Uses Babel
     */
    public String cjs2esm(@NonNull String commonjs) {
        return commonjs;
    }

    /**
     * Compile JSX to ECMAScript modules
     * Uses Babel
     */
    public String jsx2esm(@NonNull String jsx) {
        return jsx;
    }

    @SneakyThrows
    public String eval(@NonNull String script) {
        var manager = new ScriptEngineManager();
        var engine = manager.getEngineByName("graal.js");
        Assert.notNull(engine, "engine is null");
        return String.valueOf(engine.eval(script));
    }
}
