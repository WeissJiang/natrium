package nano.web.security.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.support.jdbc.SimpleJdbcSelect;
import nano.web.security.entity.NanoToken;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Map;

import static nano.support.EntityUtils.slim;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

    @NonNull
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public NanoToken queryToken(String token) {
        var paramMap = Map.of("token", token);
        var select = new SimpleJdbcSelect<>(NanoToken.class)
                .withTableName("nano_token").whereEqual("token").limit(1);
        var tokenList = select.usesJdbcTemplate(this.jdbcTemplate).query(paramMap);
        if (CollectionUtils.isEmpty(tokenList)) {
            return null;
        }
        return tokenList.get(0);
    }

    public void createToken(NanoToken token) {
        var paramSource = new BeanPropertySqlParameterSource(token);
        var insert = new SimpleJdbcInsert(this.jdbcTemplate.getJdbcTemplate())
                .withTableName("nano_token");
        insert.execute(paramSource);
    }

    public void updateTokenStatus(String token, String status) {
        var sql = """
                UPDATE nano_token
                SET status = :status
                WHERE token = :token;
                """;
        this.jdbcTemplate.update(slim(sql), Map.of("token", token, "status", status));
    }
}
