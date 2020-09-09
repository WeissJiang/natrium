package nano.web.service.scripting;

import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

public abstract class Scripting {

    // https://html.spec.whatwg.org/multipage/scripting.html#scriptingLanguages
    public static final MediaType TEXT_JAVASCRIPT = MediaType.parseMediaType("text/javascript");
    public static final Map<String, MediaType> MEDIA_TYPE = Map.of(
            "mjs", TEXT_JAVASCRIPT, "jsx", TEXT_JAVASCRIPT,
            "ts", TEXT_JAVASCRIPT, "tsx", TEXT_JAVASCRIPT,
            "less", TEXT_JAVASCRIPT
    );

    public static final List<String> SCRIPT_SUFFIX = List.of("mjs", "jsx", "ts", "tsx");
    public static final List<String> STYLE_SUFFIX = List.of("less");
}
