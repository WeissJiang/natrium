package nano.web.telegram;

import nano.support.Json;
import nano.web.nano.model.Bot;
import nano.web.nano.model.Session;
import nano.web.security.NanoPrivilege;
import org.jetbrains.annotations.NotNull;
import org.jianzhao.jsonpath.JsonPathModule;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static nano.support.Sugar.cast;
import static nano.support.Sugar.filter;

public class BotContext {

    private final Bot bot;

    private final Function<String, ?> read0;
    private final Map<String, ?> parameters;

    // -- context相关对象

    private Session session;

    private TelegramService telegramService;


    public BotContext(@NotNull Bot bot, @NotNull Map<String, ?> parameters) {
        this.bot = bot;
        this.parameters = parameters;
        var context = JsonPathModule.parse(parameters);
        this.read0 = context::read;
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
        var commandEntities = filter(entities, it -> "bot_command".equals(it.get("type")));
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
            return cast(this.read0.apply(jsonPath));
        } catch (Exception ex) {
            return null;
        }
    }

    // -- proxy to TelegramService

    public void sendMessage(String text) {
        var payload = Map.of(
                "chat_id", this.chatId(),
                "text", text
        );
        this.getTelegramService().sendMessage(this.bot(), payload);
    }

    public void replyMessage(String text) {
        var payload = Map.of(
                "chat_id", this.chatId(),
                "reply_to_message_id", this.messageId(),
                "text", text
        );
        this.getTelegramService().sendMessage(this.bot(), payload);
    }

    public void replyPhoto(Resource photo) {
        this.getTelegramService().replyPhoto(this.bot(), this.chatId(), this.messageId(), photo);
    }

    public String getFileUrl(String fileId) {
        var result = this.getTelegramService().getFile(this.bot(), fileId);
        var filePath = (String) JsonPathModule.read(result, "$.result.file_path");
        Assert.notNull(filePath, "filePath is null");
        return TelegramService.getFileUrl(this.bot(), filePath);
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
