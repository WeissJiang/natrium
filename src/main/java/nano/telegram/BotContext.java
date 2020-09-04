package nano.telegram;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.Data;
import lombok.NonNull;
import nano.security.entity.Session;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

@Data
public class BotContext {

    private final Map<String, Object> parameters;
    private final DocumentContext documentContext;

    // -- context相关对象

    private Session session;

    private TelegramService telegramService;

    public BotContext(@NonNull Map<String, Object> parameters) {
        this.parameters = parameters;
        this.documentContext = JsonPath.parse(parameters);
    }

    public Number chatId() {
        return this.read("$.message.chat.id");
    }

    public Number fromId() {
        return this.read("$.message.from.id");
    }

    public String text() {
        return this.read("$.message.text");
    }

    public Instant date() {
        Number timestamp = this.read("$.message.date");
        Assert.notNull(timestamp, "timestamp");
        return Instant.ofEpochSecond(timestamp.longValue());
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

    // -- proxy to TelegramService

    public void sendMessage(String text) {
        Assert.notNull(this.telegramService, "this.telegramService");
        this.telegramService.sendMessage(this.chatId(), text);
    }

}
