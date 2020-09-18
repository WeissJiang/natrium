package nano.web.security.repository;

import lombok.RequiredArgsConstructor;
import nano.support.jdbc.SimpleJdbcSelect;
import nano.web.security.entity.NanoToken;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
        var select = new SimpleJdbcSelect<>(NanoToken.class)
                .withTableName("nano_token").whereEqual("token").limit(1);
        var tokenList = select.usesJdbcTemplate(this.jdbcTemplate).query(Map.of("token", token));
        return getFirst(tokenList);
    }

    public NanoToken queryValidToken(String token) {
        var select = new SimpleJdbcSelect<>(NanoToken.class)
                .withTableName("nano_token").whereEqual("token", "status").limit(1);
        var paramMap = Map.of("token", token, "status", NanoToken.VALID);
        var tokenList = select.usesJdbcTemplate(this.jdbcTemplate).query(paramMap);
        return getFirst(tokenList);
    }

    public List<NanoToken> queryTokenList(List<Integer> idList) {
        var select = new SimpleJdbcSelect<>(NanoToken.class)
                .withTableName("nano_token").whereIn("id");
        return select.usesJdbcTemplate(this.jdbcTemplate).query(Map.of("id", idList));
    }

    public List<NanoToken> queryAssociatedTokenList(String token) {
        var select = new SimpleJdbcSelect<>(NanoToken.class)
                .withTableName("nano_token").whereClause("""
                        WHERE status = 'VALID'
                          AND user_id = (
                            SELECT nt.user_id
                            FROM nano_token nt
                            WHERE nt.status = 'VALID'
                              AND nt.token = :token
                        )
                        """);
        return select.usesJdbcTemplate(this.jdbcTemplate).query(Map.of("token", token));
    }

    public List<NanoToken> queryVerificatingToken(String username, String verificationCode) {
        var select = new SimpleJdbcSelect<>(NanoToken.class)
                .withTableName("nano_token").whereEqual("status");
        var status = NanoToken.verificatingStatus(username, verificationCode);
        return select.usesJdbcTemplate(this.jdbcTemplate).query(Map.of("status", status));
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
        var insert = new SimpleJdbcInsert(this.jdbcTemplate.getJdbcTemplate())
                .withTableName("nano_token").usingGeneratedKeyColumns("id");
        var paramSource = new BeanPropertySqlParameterSource(token);
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

    public void batchDeleteById(List<Integer> idList) {
        var sql = """
                DELETE
                FROM nano_token
                WHERE id IN (:idList);
                """;
        this.jdbcTemplate.update(slim(sql), Map.of("idList", idList));
    }


    public void batchDeleteByToken(List<String> tokenList) {
        var sql = """
                DELETE
                FROM nano_token
                WHERE token IN (:tokenList);
                """;
        this.jdbcTemplate.update(slim(sql), Map.of("tokenList", tokenList));
    }

}
