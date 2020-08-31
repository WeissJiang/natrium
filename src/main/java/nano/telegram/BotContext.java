package nano.telegram;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BotContext {

    @Getter
    private final Map<String, Object> attributes = new HashMap<>();

    @Getter
    private final Map<String, Object> parameters;

    private final BotApi botApi;

    private final DocumentContext documentContext;

    public BotContext(@NonNull Map<String, Object> parameters, BotApi botApi) {
        this.botApi = botApi;
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

    public void sendMessage(String text) {
        Objects.requireNonNull(this.botApi);
        var chatId = this.chatId();
        Objects.requireNonNull(chatId);
        var result = this.botApi.sendMessage(chatId, text);
        this.appendLog("sendMessage", Map.of(
                "text", text,
                "chatId", chatId,
                "result", result
        ));
    }

    private void appendLog(String operation, Object body) {
        this.attributes.put("%s-%s".formatted(operation, Instant.now()), body);
    }

    public Number chatId() {
        return this.read("$.message.chat.id");
    }

    public String text() {
        return this.read("$.message.text");
    }
}
