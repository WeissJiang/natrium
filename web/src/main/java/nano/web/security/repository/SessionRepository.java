package nano.web.security.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.web.security.entity.NanoSession;
import nano.support.jdbc.JdbcSelectAll;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.Map;

import static nano.support.jdbc.SqlUtils.*;

@Repository
@RequiredArgsConstructor
public class SessionRepository {

    @NonNull
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public NanoSession querySession(Number chatId, Number userId) {
        var paramMap = Map.of("chatId", chatId, "userId", userId);
        var selectAll = new JdbcSelectAll<>(NanoSession.class)
                .withTableName("nano_session").whereEqual("chat_id", "user_id").limit(1);
        var sessionList = selectAll.usesJdbcTemplate(this.jdbcTemplate).query(paramMap);
        if (CollectionUtils.isEmpty(sessionList)) {
            return null;
        }
        return sessionList.get(0);
    }

    public Integer createSessionAndReturnsKey(NanoSession nanoSession) {
        var sql = """
                INSERT INTO nano_session (chat_id, user_id, attributes,
                                          creation_time, last_accessed_time)
                VALUES (:chatId, :userId, :attributes::JSON,
                        :creationTime, :lastAccessedTime)
                RETURNING id;
                """;
        var paramSource = new BeanPropertySqlParameterSource(nanoSession);
        var keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(slim(sql), paramSource, keyHolder);
        var generatedKey = keyHolder.getKey();
        Assert.notNull(generatedKey, "generatedKey is null");
        return generatedKey.intValue();
    }

    public void updateLastAccessedTime(Integer id, Timestamp lastAccessedTime) {
        var sql = """
                UPDATE nano_session
                SET last_accessed_time = :lastAccessedTime
                WHERE id = :id;
                """;
        this.jdbcTemplate.update(slim(sql), Map.of("id", id, "lastAccessedTime", lastAccessedTime));
    }

    public void updateAttributes(Integer id, String attributes) {
        var sql = """
                UPDATE nano_session
                SET attributes = :attributes
                WHERE id = :id;
                """;
        this.jdbcTemplate.update(slim(sql), Map.of("id", id, "attributes", attributes));
    }
}
