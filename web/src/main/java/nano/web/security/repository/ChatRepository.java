package nano.web.security.repository;

import nano.support.jdbc.SimpleJdbcSelect;
import nano.web.security.entity.NanoChat;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static nano.support.EntityUtils.slim;
import static nano.support.Sugar.getFirst;

@Repository
public class ChatRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ChatRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public NanoChat queryChat(Long id) {
        var select = new SimpleJdbcSelect<>(NanoChat.class)
                .withTableName("nano_chat").whereEqual("id").limit(1);
        var paramMap = Map.of("id", id);
        var chatList = select.usesJdbcTemplate(this.jdbcTemplate).query(paramMap);
        return getFirst(chatList);
    }

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
