package nano.web.security.repository;

import lombok.RequiredArgsConstructor;
import nano.support.jdbc.SimpleJdbcSelect;
import nano.web.security.entity.NanoToken;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static nano.support.EntityUtils.slim;
import static nano.support.Sugar.getFirst;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public NanoToken queryToken(String token) {
        var paramMap = Map.of("token", token);
        var select = new SimpleJdbcSelect<>(NanoToken.class)
                .withTableName("nano_token").whereEqual("token").limit(1);
        var tokenList = select.usesJdbcTemplate(this.jdbcTemplate).query(paramMap);
        return getFirst(tokenList);
    }

    public List<NanoToken> queryVerificatingToken(String username, String verificationCode) {
        var status = NanoToken.verificatingStatus(username, verificationCode);
        var paramMap = Map.of("status", status);
        var select = new SimpleJdbcSelect<>(NanoToken.class)
                .withTableName("nano_token").whereEqual("status");
        return select.usesJdbcTemplate(this.jdbcTemplate).query(paramMap);
    }

    public List<String> queryVerificatingTimeoutToken() {
        var sql = """
                SELECT token
                FROM nano_token
                WHERE status LIKE 'VERIFICATING%'
                  AND creation_time + '360 sec' < NOW();
                """;
        var rowMapper = new SingleColumnRowMapper<>(String.class);
        return this.jdbcTemplate.query(slim(sql), rowMapper);
    }

    public void createToken(NanoToken token) {
        var paramSource = new BeanPropertySqlParameterSource(token);
        var insert = new SimpleJdbcInsert(this.jdbcTemplate.getJdbcTemplate())
                .withTableName("nano_token");
        insert.execute(paramSource);
    }

    public void updateToken(NanoToken token) {
        var sql = """
                UPDATE nano_token
                SET chat_id          = :chatId,
                    user_id          = :userId,
                    status           = :status,
                    last_active_time = :lastActiveTime
                WHERE token = :token;
                """;
        var paramSource = new BeanPropertySqlParameterSource(token);
        this.jdbcTemplate.update(slim(sql), paramSource);
    }

    public void updateLastActiveTime(String token, Timestamp lastActiveTime) {
        var sql = """
                UPDATE nano_token
                SET last_active_time = :lastActiveTime
                WHERE token = :token;
                """;
        this.jdbcTemplate.update(slim(sql), Map.of("token", token, "lastActiveTime", lastActiveTime));
    }

    public void deleteToken(String token) {
        this.batchDeleteToken(List.of(token));
    }

    public void batchDeleteToken(List<String> tokens) {
        var sql = """
                DELETE
                FROM nano_token
                WHERE token IN (:tokens);
                """;
        this.jdbcTemplate.update(slim(sql), Map.of("tokens", tokens));
    }
}
