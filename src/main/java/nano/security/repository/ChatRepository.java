package nano.security.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.security.entity.NanoChat;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRepository {

    @NonNull
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public NanoChat queryChat(Number chatId) {
        return new NanoChat();
    }
}
