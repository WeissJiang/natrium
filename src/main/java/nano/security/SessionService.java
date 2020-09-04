package nano.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.security.entity.Session;
import nano.security.model.InitialSession;
import nano.security.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class SessionService {

    @NonNull
    private final SessionRepository sessionRepository;

    @Transactional
    public Session createOrUpdateSessionIfExists(InitialSession initial) {
        var chatId = initial.getChatId();
        var userId = initial.getUserId();
        var lastAccessedTime = initial.getLastAccessedTime();

        var repository = this.sessionRepository;

        var session = repository.querySession(chatId, userId);
        var createNew = false;
        if (session == null) {
            session = new Session();
            session.setChatId(chatId);
            session.setUserId(userId);
            session.setAttributes("{}");
            session.setCreationTime(Timestamp.from(lastAccessedTime));
            createNew = true;
        }
        session.setLastAccessedTime(Timestamp.from(lastAccessedTime));
        // persistence
        if (createNew) {
            var id = repository.createSessionAndReturnsPrimaryKey(session);
            session.setId(id);
        } else {
            repository.updateSession(session);
        }
        return session;
    }

}
