package nano.security.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.security.entity.User;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    @NonNull
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private User queryUser(Number userId) {
        return new User();
    }
}
