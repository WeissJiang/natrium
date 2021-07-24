package nano.service.security;

import nano.service.nano.entity.NanoChat;
import nano.service.nano.entity.NanoToken;
import nano.service.nano.entity.NanoUser;
import nano.service.nano.model.Session;
import nano.service.nano.repository.ChatRepository;
import nano.service.nano.repository.TokenRepository;
import nano.service.nano.repository.UserRepository;
import nano.support.Json;
import org.jetbrains.annotations.NotNull;
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
    public @NotNull Session getSession(@NotNull NanoChat chat, @NotNull NanoUser user) {
        this.updateOrCreateChatIfAbsent(chat);
        this.updateOrCreateUserIfAbsent(user);
        var token = this.getOrCreateTokenIfAbsent(user.getId(), chat.getId());
        return new Session(chat, user, token);
    }

    private void updateOrCreateChatIfAbsent(@NotNull NanoChat incoming) {
        var exist = this.chatRepository.queryChat(incoming.getId());
        if (!Objects.equals(exist, incoming)) {
            this.chatRepository.upsertChat(incoming);
        }
    }

    private void updateOrCreateUserIfAbsent(@NotNull NanoUser incoming) {
        var exist = this.userRepository.queryUser(incoming.getId());
        if (exist == null) {
            this.userRepository.upsertUser(incoming);
        }
        // incoming not changed
        else if (userNotChanged(incoming, exist)) {
            incoming.setEmail(exist.getEmail());
        }
        //  incoming not changed
        else {
            incoming.setEmail(exist.getEmail());
            this.userRepository.upsertUser(incoming);
        }
    }

    /**
     * Whether user changed
     */
    private static boolean userNotChanged(@NotNull NanoUser incoming, @NotNull NanoUser exist) {
        List<Function<NanoUser, ?>> getterList = List.of(
                NanoUser::getUsername,
                NanoUser::getFirstname,
                NanoUser::getLanguageCode
        );
        return every(getterList, getter -> {
            var incomingVal = getter.apply(incoming);
            var existVal = getter.apply(exist);
            return Objects.equals(incomingVal, existVal);
        });
    }

    private @NotNull NanoToken getOrCreateTokenIfAbsent(@NotNull Long userId, @NotNull Long chatId) {
        var tokenKey = "%s-%s".formatted(userId, chatId);
        var token = this.tokenRepository.queryToken(tokenKey);
        var now = Timestamp.from(Instant.now());
        if (token == null) {
            token = new NanoToken();
            token.setPrivilege(Json.encode(List.of(Privilege.BASIC)));
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
