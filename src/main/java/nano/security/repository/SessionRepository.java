package nano.security.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.security.entity.Session;
import nano.support.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Map;

import static nano.support.SqlUtils.*;

@Repository
@RequiredArgsConstructor
public class SessionRepository {

    private final static String ALL_COLUMNS = String.join(" ", entityColumnNames(Session.class));

    @NonNull
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Session querySession(Number chatId, Number userId) {
        var sql = """
                SELECT %s
                FROM nano_session
                WHERE TRUE
                  AND chat_id = :chatId
                  AND user_id = :userId
                LIMIT 1;
                """.formatted(ALL_COLUMNS);
        var paramMap = Map.of(
                "chatId", chatId,
                "userId", userId
        );
        var rowMapper = new BeanPropertyRowMapper<>(Session.class);
        var sessionList = this.jdbcTemplate.query(slim(sql), paramMap, rowMapper);
        if (CollectionUtils.isEmpty(sessionList)) {
            return null;
        }
        return sessionList.get(0);
    }

    public Integer createSessionAndReturnsPrimaryKey(Session session) {
        var sql = """
                INSERT INTO nano_session (chat_id, user_id, attributes,
                                          creation_time, last_accessed_time)
                VALUES (:chatId, :userId, :attributes,
                        :creationTime, :lastAccessedTime);
                """;
        var paramSource = new BeanPropertySqlParameterSource(session);
        var keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(slim(sql), paramSource, keyHolder, new String[]{"id"});
        var generatedKey = keyHolder.getKey();
        Assert.notNull(generatedKey, "generatedKey");
        return generatedKey.intValue();
    }

    public void updateSession(Session session) {
        var sql = """
                UPDATE nano_session
                SET attributes         = :attributes,
                    last_accessed_time = :lastAccessedTime
                WHERE id = :id;
                """;
        var paramSource = new BeanPropertySqlParameterSource(session);
        this.jdbcTemplate.update(slim(sql), paramSource);
    }
}
