package nano.telegram;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class BotContext {

    @Getter
    private final Map<String, Object> attributes = new HashMap<>();

    @Getter
    @Setter
    private Map<String, Object> parameters;

    @Setter
    private DocumentContext documentContext;

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

    public String text(){
        return this.read("$.message.text");
    }
}
