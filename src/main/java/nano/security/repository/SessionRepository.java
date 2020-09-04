package nano.security.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.security.entity.Session;
import nano.security.model.InitialSession;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.Map;

import static nano.support.SqlUtils.slim;

@Repository
@RequiredArgsConstructor
public class SessionRepository {

    @NonNull
    private final NamedParameterJdbcTemplate jdbcTemplate;

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
        var sessionList = this.jdbcTemplate.query(slim(sql), paramMap, rowMapper);
        if (CollectionUtils.isEmpty(sessionList)) {
            return null;
        }
        return sessionList.get(0);
    }

    public Integer createSessionAndReturnsPrimaryKey(Session session) {
        var sql = """
                INSERT INTO nano_session (chat_id, user_id, attributes, creation_time, last_accessed_time)
                VALUES (:chatId, :userId, :attributes, :creationTime, :lastAccessedTime);
                """;
        var paramSource = new BeanPropertySqlParameterSource(session);
        var keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(slim(sql), paramSource, keyHolder);
        var generatedKey = keyHolder.getKeyList().get(0).get("id");
        Assert.isInstanceOf(Number.class, generatedKey);
        return ((Number) generatedKey).intValue();
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
        this.jdbcTemplate.update(slim(sql), paramSource);
    }

    @Transactional
    public Session upsertSession(InitialSession initial) {
        var chatId = initial.getChatId();
        var userId = initial.getUserId();

        var session = this.querySession(chatId, userId);
        var createNew = false;
        if (session == null) {
            session = new Session();
            session.setCreationTime(Timestamp.from(initial.getLastAccessedTime()));
            createNew = true;
        }
        session.setChatId(chatId);
        session.setUserId(userId);
        session.setLastAccessedTime(Timestamp.from(initial.getLastAccessedTime()));
        // persistence
        if (createNew) {
            var id = this.createSessionAndReturnsPrimaryKey(session);
            session.setId(id);
        } else {
            this.updateSession(session);
        }
        return session;
    }
}
