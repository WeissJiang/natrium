package nano.web.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.support.Sugar;
import nano.web.security.entity.NanoChat;
import nano.web.security.entity.NanoSession;
import nano.web.security.entity.NanoUser;
import nano.web.security.model.Session;
import nano.web.security.model.SessionKey;
import nano.web.security.repository.ChatRepository;
import nano.web.security.repository.SessionRepository;
import nano.web.security.repository.UserRepository;
import nano.support.Json;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

import static nano.support.Sugar.*;

@Service
@RequiredArgsConstructor
public class SessionService {

    @NonNull
    private final SessionRepository sessionRepository;
    @NonNull
    private final ChatRepository chatRepository;
    @NonNull
    private final UserRepository userRepository;

    public Session getSession(SessionKey key, NanoChat chat, NanoUser user) {
        var internalSession = this.internalGetSession(key);
        this.updateOrCreateChatIfAbsent(chat);
        this.updateOrCreateUserIfAbsent(user);

        var session = new Session(internalSession, chat, user);
        // put attributes
        var attributesJson = internalSession.getAttributes();
        session.getAttributes().putAll(Json.decodeValueAsMap(attributesJson));
        return session;
    }

    private void updateOrCreateChatIfAbsent(NanoChat chat) {
        this.chatRepository.upsertChat(chat);
    }

    private void updateOrCreateUserIfAbsent(NanoUser user) {
        var exist = this.userRepository.queryUser(user.getId());
        if (exist == null) {
            this.userRepository.upsertUser(user);
        }
        // user changed
        else if (!is(user::getUsername, exist::getUsername)
                 || !is(user::getFirstname, exist::getFirstname)
                 || !is(user::getLanguageCode, exist::getLanguageCode)) {
            user.setEmail(exist.getEmail());
            this.userRepository.upsertUser(user);
        }
    }

    private NanoSession internalGetSession(SessionKey key) {
        var chatId = key.getChatId();
        var userId = key.getUserId();
        var lastAccessedTime = Timestamp.from(Instant.now());

        var repository = this.sessionRepository;

        var session = repository.querySession(chatId, userId);
        if (session == null) {
            session = new NanoSession();
            session.setChatId(chatId);
            session.setUserId(userId);
            session.setAttributes("{}");
            session.setCreationTime(lastAccessedTime);
            session.setLastAccessedTime(lastAccessedTime);
            var id = repository.createSessionAndReturnsKey(session);
            session.setId(id);
        } else {
            session.setLastAccessedTime(lastAccessedTime);
            repository.updateLastAccessedTime(session.getId(), lastAccessedTime);
        }
        return session;
    }

    public void syncSession(Session session) {
        var attributes = session.getAttributes();

        var internalSession = session.getInternalSession();
        var attributesJson = internalSession.getAttributes();
        var originAttributes = Json.decodeValueAsMap(attributesJson);

        if (Objects.equals(attributes, originAttributes)) {
            return;
        }
        // do sync
        var sessionId = internalSession.getId();
        this.sessionRepository.updateAttributes(sessionId, Json.encode(attributes));
    }
}
