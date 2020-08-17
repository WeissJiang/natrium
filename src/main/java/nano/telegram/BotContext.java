package nano.telegram;

import com.jayway.jsonpath.DocumentContext;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class BotContext {

    private Map<String, Object> attributes = new HashMap<>();

    private Map<String, Object> parameters;
    private DocumentContext documentContext;

    public <T> T readParameter(String jsonPath) {
        return this.documentContext.read(jsonPath);
    }

}
