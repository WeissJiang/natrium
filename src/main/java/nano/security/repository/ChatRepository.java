package nano.security.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.security.entity.Chat;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRepository {

    @NonNull
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Chat queryChat(Number chatId) {
        return new Chat();
    }
}
