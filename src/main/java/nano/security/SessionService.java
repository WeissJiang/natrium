package nano.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.security.entity.NanoSession;
import nano.security.model.Session;
import nano.security.model.SessionSeed;
import nano.security.repository.ChatRepository;
import nano.security.repository.SessionRepository;
import nano.security.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class SessionService {

    @NonNull
    private final SessionRepository sessionRepository;
    @NonNull
    private final ChatRepository chatRepository;
    @NonNull
    private final UserRepository userRepository;

    public Session getSession(SessionSeed seed) {
        var nanoSession = this.createOrUpdateSessionIfExists(seed);
        var nanoChat = this.chatRepository.queryChat(seed.getChatId());
        var nanoUser = this.userRepository.queryUser(seed.getUserId());

        var session = new Session();
        session.setDelegate(nanoSession);
        session.setChat(nanoChat);
        session.setUser(nanoUser);
        return session;
    }

    private NanoSession createOrUpdateSessionIfExists(SessionSeed seed) {
        var chatId = seed.getChatId();
        var userId = seed.getUserId();
        var lastAccessedTime = seed.getLastAccessedTime();

        var repository = this.sessionRepository;

        var session = repository.querySession(chatId, userId);
        var createNew = false;
        if (session == null) {
            session = new NanoSession();
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
