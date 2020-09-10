package nano.web.security.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.web.security.entity.NanoUser;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import static nano.support.EntityUtils.slim;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    @NonNull
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void upsertUser(NanoUser nanoUser) {
        var sql = """
                INSERT INTO nano_user (id, username, firstname, is_bot, language_code)
                VALUES (:id, :username, :firstname, :isBot, :languageCode)
                ON CONFLICT (id)
                    DO UPDATE SET username      = EXCLUDED.username,
                                  firstname     = EXCLUDED.firstname,
                                  is_bot        = EXCLUDED.is_bot,
                                  language_code = EXCLUDED.language_code;
                """;
        var paramSource = new BeanPropertySqlParameterSource(nanoUser);
        this.jdbcTemplate.update(slim(sql), paramSource);
    }

}
