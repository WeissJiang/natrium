package nano.telegram;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class BotContext {

    @Getter
    private final Map<String, Object> attributes = new HashMap<>();

    @Getter
    private final Map<String, Object> parameters;

    private final DocumentContext documentContext;

    public BotContext(Map<String, Object> parameters) {
        this.parameters = parameters;
        this.documentContext = JsonPath.parse(parameters);
    }

    /**
     * @param jsonPath JSON path
     * @param <T>      result type
     * @return null if absent
     */
    public <T> T read(String jsonPath) {
        try {
            return this.documentContext.read(jsonPath);
        } catch (PathNotFoundException ex) {
            return null;
        }
    }

    public Integer chatId() {
        return this.read("$.message.chat.id");
    }

    public String text() {
        return this.read("$.message.text");
    }
}
