package nano.security.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.security.entity.Session;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
@RequiredArgsConstructor
public class SessionRepository {

    @NonNull
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Integer createSessionAndReturnsPrimaryKey(Session session) {
        return 0;
//        var sql = """
//                INSERT INTO nano_session (chat_id, user_id, attributes, creation_time, last_accessed_time)
//                VALUES (:chatId, :userId, :attributes, :creationTime, :lastAccessedTime);
//                """;
//        var paramSource = new BeanPropertySqlParameterSource(session);
//        var keyHolder = new GeneratedKeyHolder();
//        this.jdbcTemplate.update(sql, paramSource, keyHolder);
//        var generatedKey = keyHolder.getKey();
//        Assert.notNull(generatedKey, "generatedKey");
//        return generatedKey.intValue();
    }

    public Session querySession(Number chatId, Number userId) {
        return new Session();
    }

    public void updateSession(Session session) {
    }

}
