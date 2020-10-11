package nano.web.telegram;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import nano.support.Json;
import nano.support.Sugar;
import nano.web.nano.Bot;
import nano.web.security.NanoPrivilege;
import nano.web.security.model.Session;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class BotContext {

    private final Bot bot;

    private final Map<String, ?> parameters;
    private final DocumentContext documentContext;

    // -- context相关对象

    private Session session;

    private TelegramService telegramService;


    public BotContext(@NotNull Bot bot, @NotNull Map<String, ?> parameters) {
        this.bot = bot;
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

    public String chatType() {
        return this.read("$.message.chat.type");
    }

    public Number messageId() {
        return this.read("$.message.message_id");
    }

    public List<NanoPrivilege> userPrivilegeList() {
        var privilege = this.getSession().getToken().getPrivilege();
        return Json.decodeValueAsList(privilege)
                .stream()
                .map(String::valueOf)
                .map(NanoPrivilege::valueOf)
                .distinct()
                .collect(Collectors.toList());
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
            return emptyList();
        }
        var text = this.text();
        var commands = new ArrayList<String>();
        for (var entity : commandEntities) {
            var offset = getInteger(entity.get("offset"));
            var length = getInteger(entity.get("length"));
            if (offset == null || length == null) {
                continue;
            }
            var command = text.substring(offset, offset + length);
            commands.add(command);
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

    public void sendMessage(String text, Object... args) {
        this.getTelegramService().sendMessage(this.bot(), this.chatId(), String.format(text, args));
    }

    public void replyMessage(String text, Object... args) {
        this.getTelegramService().replyMessage(this.bot(), this.chatId(), this.messageId(), String.format(text, args));
    }

    public void sendPhoto(String photo) {
        this.getTelegramService().sendPhoto(this.bot(), this.chatId(), photo);
    }

    private static Integer getInteger(Object o) {
        if (o instanceof Integer) {
            return (Integer) o;
        }
        if (o instanceof Number) {
            return ((Number) o).intValue();
        }
        return null;
    }

    public Map<String, ?> getParameters() {
        return parameters;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public TelegramService getTelegramService() {
        Assert.notNull(this.telegramService, "this.telegramService is null");
        return this.telegramService;
    }

    public void setTelegramService(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    public Bot bot() {
        return this.bot;
    }

}
