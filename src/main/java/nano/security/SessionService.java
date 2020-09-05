package nano.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.security.entity.NanoSession;
import nano.security.model.Session;
import nano.security.model.SessionKey;
import nano.security.repository.ChatRepository;
import nano.security.repository.SessionRepository;
import nano.security.repository.UserRepository;
import nano.support.Json;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SessionService {

    @NonNull
    private final SessionRepository sessionRepository;
    @NonNull
    private final ChatRepository chatRepository;
    @NonNull
    private final UserRepository userRepository;

    public Session getSession(SessionKey key, Map<String, Object> attributes) {
        var session = this.createOrUpdateSessionIfExists(key, attributes);

        return Session.create(session,
                () -> this.chatRepository.queryChat(key.getChatId()),
                () -> this.userRepository.queryUser(key.getUserId()));
    }

    private NanoSession createOrUpdateSessionIfExists(SessionKey key, Map<String, Object> attributes) {
        var chatId = key.getChatId();
        var userId = key.getUserId();
        var lastAccessedTime = Instant.now();

        var repository = this.sessionRepository;

        var session = repository.querySession(chatId, userId);
        var createNew = false;
        if (session == null) {
            session = new NanoSession();
            session.setChatId(chatId);
            session.setUserId(userId);
            session.setAttributes(Json.encode(attributes));
            session.setCreationTime(Timestamp.from(lastAccessedTime));
            createNew = true;
        } else {
            var existAttributes = Json.decodeValueAsMap(session.getAttributes());
            existAttributes.putAll(attributes);
            session.setAttributes(Json.encode(existAttributes));
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
