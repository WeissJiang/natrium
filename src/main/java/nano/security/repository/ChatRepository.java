package nano.security.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.security.entity.NanoChat;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import static nano.support.jdbc.SqlUtils.slim;

@Repository
@RequiredArgsConstructor
public class ChatRepository {

    @NonNull
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void upsertChat(NanoChat nanoChat){
        var sql = """
                INSERT INTO nano_chat (id, username, title, firstname, type)
                VALUES (:id, :username, :title, :firstname, :type)
                ON CONFLICT (id)
                    DO UPDATE SET username  = EXCLUDED.username,
                                  title     = EXCLUDED.title,
                                  firstname = EXCLUDED.firstname,
                                  type      = EXCLUDED.type;
                """;
        var paramSource = new BeanPropertySqlParameterSource(nanoChat);
        this.jdbcTemplate.update(slim(sql), paramSource);
    }

}
