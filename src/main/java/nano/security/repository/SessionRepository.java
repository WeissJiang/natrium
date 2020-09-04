package nano.security.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.security.entity.Session;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SessionRepository {

    @NonNull
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Integer createSessionAndReturnsPrimaryKey(Session session) {
        var sql = """
                INSERT INTO nano_session (chat_id, user_id, attributes, creation_time, last_accessed_time)
                VALUES (:chatId, :userId, :attributes, :creationTime, :lastAccessedTime);
                """;
        var paramSource = new BeanPropertySqlParameterSource(session);
        var keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(sql, paramSource, keyHolder);
        var generatedKey = keyHolder.getKey();
        Assert.notNull(generatedKey, "generatedKey");
        return generatedKey.intValue();
    }

    public Session querySession(Number chatId, Number userId) {
        var sql = """
                SELECT chat_id, user_id, attributes, creation_time, last_accessed_time
                FROM nano_session
                WHERE TRUE
                  AND chat_id = :chatId
                  AND user_id = :userId
                LIMIT 1;
                """;
        var paramMap = Map.of(
                "chatId", chatId,
                "userId", userId
        );
        var rowMapper = new BeanPropertyRowMapper<>(Session.class);
        var sessionList = this.jdbcTemplate.query(sql, paramMap, rowMapper);
        if (CollectionUtils.isEmpty(sessionList)) {
            return null;
        }
        return sessionList.get(0);
    }

    public void updateSession(Session session) {
        var sql = """
                UPDATE nano_session
                SET attributes         = :attributes,
                    creation_time      = :creationTime,
                    last_accessed_time = :lastAccessedTime
                WHERE id = :id;
                """;
        var paramSource = new BeanPropertySqlParameterSource(session);
        this.jdbcTemplate.update(sql, paramSource);
    }

    @Transactional
    public Session upsertSession(Session session) {

        var chatId = session.getChatId();
        var userId = session.getUserId();

        var current = this.querySession(chatId, userId);
        var createNew = false;
        if (current == null) {
            current = new Session();
            current.setCreationTime(session.getLastAccessedTime());
            createNew = true;
        }
        current.setChatId(chatId);
        current.setUserId(userId);
        current.setLastAccessedTime(session.getCreationTime());
        // persistence
        if (createNew) {
            var id = this.createSessionAndReturnsPrimaryKey(current);
            current.setId(id);
        } else {
            this.updateSession(current);
        }
        return current;
    }
}
