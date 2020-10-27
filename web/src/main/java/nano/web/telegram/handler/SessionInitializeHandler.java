package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.security.SessionService;
import nano.web.security.entity.NanoChat;
import nano.web.security.entity.NanoUser;
import nano.web.security.model.Session;
import nano.web.telegram.BotContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 初始化会话
 * <p>
 * After {@link LogHandler}
 */
@Order(-10_000)
@Component
public class SessionInitializeHandler implements Onion.Middleware<BotContext> {

    private static final Logger log = LoggerFactory.getLogger(SessionInitializeHandler.class);

    private final SessionService sessionService;

    public SessionInitializeHandler(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        try {
            // sync chat and user
            var chat = this.readChat(context);
            var user = this.readUser(context);
            // build session
            var session = this.buildSession(chat, user);
            context.setSession(session);
        } catch (Exception ex) {
            log.warn("build session failed: {}", ex.getMessage());
        } finally {
            next.next();
        }
    }

    private NanoUser readUser(BotContext context) {
        var user = new NanoUser();
        Number userId = context.read("$.message.from.id");
        Assert.notNull(userId, "userId is null");
        user.setId(userId.longValue());
        user.setUsername(context.read("$.message.from.username"));
        user.setFirstname(context.read("$.message.from.first_name"));
        user.setIsBot(context.read("$.message.from.is_bot"));
        user.setLanguageCode(context.read("$.message.from.language_code"));
        return user;
    }

    private NanoChat readChat(BotContext context) {
        var chat = new NanoChat();
        Number chatId = context.read("$.message.chat.id");
        Assert.notNull(chatId, "chatId is null");
        chat.setId(chatId.longValue());
        chat.setUsername(context.read("$.message.chat.username"));
        chat.setFirstname(context.read("$.message.chat.first_name"));
        chat.setTitle(context.read("$.message.chat.title"));
        chat.setType(context.read("$.message.chat.type"));
        return chat;
    }

    private Session buildSession(NanoChat chat, NanoUser user) {
        return this.sessionService.getSession(chat, user);
    }

}
