package nano.web.service.scripting;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.springframework.stereotype.Service;

/**
 * Script service
 */
@Slf4j
@Service
public class ScriptService {

    public String eval(@NonNull String script) {
        return Context.create("js").eval("js", script).asString();
    }
}
