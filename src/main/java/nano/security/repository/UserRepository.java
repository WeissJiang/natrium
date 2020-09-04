package nano.security.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.security.entity.NanoUser;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    @NonNull
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public NanoUser queryUser(Number userId) {
        return new NanoUser();
    }
}
