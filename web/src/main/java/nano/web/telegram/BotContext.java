package nano.web.telegram;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.Data;
import lombok.NonNull;
import nano.support.Sugar;
import nano.web.security.model.Session;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        Assert.notNull(timestamp, "timestamp is null");
        return Instant.ofEpochSecond(timestamp.longValue());
    }

    public List<String> commands() {
        List<Map<String, Object>> entities = this.read("$.message.entities");
        var commandEntities = Sugar.filter(entities, it -> "bot_command".equals(it.get("type")));
        if (CollectionUtils.isEmpty(commandEntities)) {
            return Collections.emptyList();
        }
        var text = this.text();
        var commands = new ArrayList<String>();
        for (var entity : commandEntities) {
            if (entity.get("offset") instanceof Integer offset
                && entity.get("length") instanceof Integer length) {
                var command = text.substring(offset, offset + length);
                commands.add(command);
            }
        }
        return commands;
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
        Assert.notNull(this.telegramService, "this.telegramService is null");
        this.telegramService.sendMessage(this.chatId(), text);
    }
}
