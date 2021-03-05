package nano.web.nano.repository;

import nano.support.jdbc.SimpleJdbcSelect;
import nano.web.nano.entity.KeyValue;
import nano.web.nano.entity.NanoToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static nano.support.EntityUtils.slim;

@Repository
public class KeyValueRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public KeyValueRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public @Nullable KeyValue queryKeyValue(@NotNull String key) {
        var select = new SimpleJdbcSelect<>(KeyValue.class)
                .withTableName("key_value").whereEqual("key").limit(1);
        return select.usesJdbcTemplate(this.jdbcTemplate).queryOne(Map.of("key", key));
    }

    public void createKeyValue(@NotNull String key, @NotNull String value) {
        var sql = """
                INSERT INTO key_value (key, value, last_updated_time, creation_time)
                VALUES (:key, :value::JSONB, NOW(), NOW());
                """;
        this.jdbcTemplate.update(slim(sql), Map.of("key", key, "value", value));
    }

    public void updateKeyValue(@NotNull String key, @NotNull String value) {
        var sql = """
                UPDATE key_value
                SET value             = :value::JSONB,
                    last_updated_time = NOW()
                WHERE key = :key;
                """;
        this.jdbcTemplate.update(slim(sql), Map.of("key", key, "value", value));
    }
}
