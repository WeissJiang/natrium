package nano.telegram.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.security.SessionService;
import nano.security.model.Session;
import nano.security.model.SessionKey;
import nano.support.Onion;
import nano.telegram.BotContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionInitializeHandler implements Onion.Middleware<BotContext> {

    @NonNull
    private final SessionService sessionService;

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        try {
            // sync chat and user

            // build session
            var session = this.buildSession(context);
            context.setSession(session);
        } catch (Exception ex) {
            log.warn("build session failed: {}", ex.getMessage());
        } finally {
            next.next();
        }
    }

    private Session buildSession(BotContext context) {
        var sessionKey = new SessionKey();

        var chatId = context.chatId();
        Assert.notNull(chatId, "chatId");
        sessionKey.setChatId(chatId);

        var userId = context.fromId();
        Assert.notNull(userId, "userId");
        sessionKey.setUserId(userId);

        return this.sessionService.getSession(sessionKey, Map.of("date", context.date()));
    }

}
