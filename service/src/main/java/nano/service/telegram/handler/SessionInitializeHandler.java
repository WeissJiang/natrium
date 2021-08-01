package nano.service.telegram.handler;

import nano.service.nano.entity.NanoChat;
import nano.service.nano.entity.NanoUser;
import nano.service.telegram.Session;
import nano.service.telegram.BotContext;
import nano.support.Onion;
import nano.service.security.SessionService;
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
        Number userId = context.read("$.message.from.id");
        Assert.notNull(userId, "userId is null");

        String username = context.read("$.message.from.username");
        String firstname = context.read("$.message.from.first_name");
        Boolean isBot = context.read("$.message.from.is_bot");
        String languageCode = context.read("$.message.from.language_code");

        return new NanoUser(userId.longValue(), username, firstname, languageCode, isBot, null);
    }

    private NanoChat readChat(BotContext context) {
        Number chatId = context.read("$.message.chat.id");
        Assert.notNull(chatId, "chatId is null");

        String username = context.read("$.message.chat.username");
        String title = context.read("$.message.chat.title");
        String firstname = context.read("$.message.chat.first_name");
        String type = context.read("$.message.chat.type");

        return new NanoChat(chatId.longValue(), username, title, firstname, type);
    }

    private Session buildSession(NanoChat chat, NanoUser user) {
        return this.sessionService.getSession(chat, user);
    }

}
