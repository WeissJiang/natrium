package nano.telegram.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.security.SessionService;
import nano.security.model.Session;
import nano.security.model.SessionSeed;
import nano.support.Onion;
import nano.telegram.BotContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionInitializeHandler implements Onion.Middleware<BotContext> {

    @NonNull
    private final SessionService sessionService;

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        try {
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
        var seed = new SessionSeed();

        var chatId = context.chatId();
        Assert.notNull(chatId, "chatId");
        seed.setChatId(chatId);

        var userId = context.fromId();
        Assert.notNull(userId, "userId");
        seed.setUserId(userId);

        var date = context.date();
        seed.setLastAccessedTime(date);

        return this.sessionService.getSession(seed);
    }

}
