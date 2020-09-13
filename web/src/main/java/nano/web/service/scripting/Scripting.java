package nano.web.service.scripting;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Data
public class Scripting {

    // https://html.spec.whatwg.org/multipage/scripting.html#scriptingLanguages
    public static final MediaType TEXT_JAVASCRIPT = MediaType.parseMediaType("text/javascript");
    public static final Map<String, MediaType> MEDIA_TYPE = Map.of(
            "mjs", TEXT_JAVASCRIPT, "jsx", TEXT_JAVASCRIPT,
            "ts", TEXT_JAVASCRIPT, "tsx", TEXT_JAVASCRIPT,
            "less", TEXT_JAVASCRIPT
    );

    public static final List<String> SCRIPT_SUFFIX = List.of("mjs", "jsx", "ts", "tsx");
    public static final List<String> STYLE_SUFFIX = List.of("less");

    @Value("classpath:/scripting/typescript/lib/typescript.js")
    private Resource typescript;

    @Value("classpath:/scripting/tsc.js")
    private Resource tsc;

    @Value("classpath:/scripting/less/dist/less.js")
    private Resource less;

    @Value("classpath:/scripting/lessc.js")
    private Resource lessc;

    @Value("classpath:/scripting/browser_shim.js")
    private Resource browserShim;
}
