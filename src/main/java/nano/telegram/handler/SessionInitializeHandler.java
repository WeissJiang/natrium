package nano.telegram.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.security.entity.Session;
import nano.security.repository.SessionRepository;
import nano.support.Onion;
import nano.telegram.BotContext;
import nano.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
public class SessionInitializeHandler implements Onion.Middleware<BotContext> {

    @NonNull
    private final SessionRepository sessionRepository;

    @Override
    public void via(BotContext context, Onion.Next next) {
        // build session
        var session = this.buildSession(context);
        context.setSession(session);
    }

    private Session buildSession(BotContext context) {
        var chatId = context.chatId();
        var userId = context.fromId();
        var date = context.date();
        Assert.notNull(chatId, "chatId");
        Assert.notNull(userId, "userId");

        var session = this.sessionRepository.querySession(chatId, userId);
        var createNew = false;
        if (session == null) {
            session = new Session();
            session.setCreationTime(date);
            createNew = true;
        }
        session.setChatId(chatId);
        session.setUserId(userId);
        session.setLastAccessedTime(date);
        // persistence
        this.persistSession(session, createNew);
        return session;
    }

    private void persistSession(Session session, boolean createNew) {
        if (createNew) {
            var id = this.sessionRepository.createSession(session);
            session.setId(id);
        } else {
            this.sessionRepository.updateSession(session);
        }
    }
}
