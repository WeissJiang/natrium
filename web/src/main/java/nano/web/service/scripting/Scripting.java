package nano.web.service.scripting;

import lombok.Data;
import org.springframework.http.MediaType;

import java.util.Map;

@Data
public class Scripting {

    // https://html.spec.whatwg.org/multipage/scripting.html#scriptingLanguages
    public static final MediaType TEXT_JAVASCRIPT = MediaType.parseMediaType("text/javascript");
    public static final Map<String, MediaType> MEDIA_TYPE = Map.of(
            "mjs", TEXT_JAVASCRIPT, "jsx", TEXT_JAVASCRIPT,
            "ts", TEXT_JAVASCRIPT, "tsx", TEXT_JAVASCRIPT,
            "less", TEXT_JAVASCRIPT
    );
}
