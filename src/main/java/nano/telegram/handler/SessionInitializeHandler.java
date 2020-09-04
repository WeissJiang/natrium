package nano.telegram.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.security.entity.Session;
import nano.security.repository.SessionRepository;
import nano.support.Onion;
import nano.telegram.BotContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionInitializeHandler implements Onion.Middleware<BotContext> {

    @NonNull
    private final SessionRepository sessionRepository;

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
        var chatId = context.chatId();
        var userId = context.fromId();
        var date = context.date();

        Assert.notNull(chatId, "chatId");
        Assert.notNull(userId, "userId");

        var session = new Session();
        session.setChatId(chatId);
        session.setUserId(userId);
        session.setLastAccessedTime(date);

        return this.sessionRepository.upsertSession(session);
    }

}
