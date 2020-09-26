package nano.web.security;

import nano.support.Json;
import nano.web.security.entity.NanoChat;
import nano.web.security.entity.NanoToken;
import nano.web.security.entity.NanoUser;
import nano.web.security.model.Session;
import nano.web.security.repository.ChatRepository;
import nano.web.security.repository.TokenRepository;
import nano.web.security.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static nano.support.Sugar.every;

@Service
public class SessionService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public SessionService(ChatRepository chatRepository,
                          UserRepository userRepository,
                          TokenRepository tokenRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public Session getSession(NanoChat chat, NanoUser user) {
        this.updateOrCreateChatIfAbsent(chat);
        this.updateOrCreateUserIfAbsent(user);
        var token = this.getOrCreateTokenIfAbsent(user.getId(), chat.getId());
        return new Session(chat, user, token);
    }

    private void updateOrCreateChatIfAbsent(NanoChat incomming) {
        var exist = this.chatRepository.queryChat(incomming.getId());
        if (!Objects.equals(exist, incomming)) {
            this.chatRepository.upsertChat(incomming);
        }
    }

    private void updateOrCreateUserIfAbsent(NanoUser incomming) {
        var exist = this.userRepository.queryUser(incomming.getId());
        if (exist == null) {
            this.userRepository.upsertUser(incomming);
        }
        // incomming not changed
        else if (userNotChanged(incomming, exist)) {
            incomming.setEmail(exist.getEmail());
        }
        //  incomming not changed
        else {
            incomming.setEmail(exist.getEmail());
            this.userRepository.upsertUser(incomming);
        }
    }

    /**
     * Whether user changed
     */
    private static boolean userNotChanged(NanoUser incomming, NanoUser exist) {
        List<Function<NanoUser, ?>> getterList = List.of(
                NanoUser::getUsername,
                NanoUser::getFirstname,
                NanoUser::getLanguageCode
        );
        return every(getterList, getter -> {
            var incommingVal = getter.apply(incomming);
            var existVal = getter.apply(exist);
            return Objects.equals(incommingVal, existVal);
        });
    }

    private NanoToken getOrCreateTokenIfAbsent(Long userId, Long chatId) {
        var tokenKey = "%s-%s".formatted(userId, chatId);
        var token = this.tokenRepository.queryToken(tokenKey);
        var now = Timestamp.from(Instant.now());
        if (token == null) {
            token = new NanoToken();
            token.setPrivilege(Json.encode(List.of(NanoPrivilege.BASIC.name())));
            token.setName("Telegram");
            token.setCreationTime(now);
            token.setLastActiveTime(now);
            token.setUserId(userId);
            token.setChatId(chatId);
            token.setToken(tokenKey);
            token.setStatus(NanoToken.VALID);
            this.tokenRepository.createToken(token);
        }
        // token exists
        else {
            this.tokenRepository.updateLastActiveTime(tokenKey, now);
        }
        return token;
    }
}
