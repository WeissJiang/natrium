package nano.telegram;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class BotContext {

    private Map<String, Object> attributes = new HashMap<>();

    private Map<String, Object> parameters;
    private DocumentContext documentContext;

    public <T> T readParameter(String jsonPath) {
        try {
            return this.documentContext.read(jsonPath);
        } catch (PathNotFoundException ex) {
            return null;
        }
    }

}
