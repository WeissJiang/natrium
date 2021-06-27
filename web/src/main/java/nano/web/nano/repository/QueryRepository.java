package nano.web.nano.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static nano.support.EntityUtils.slim;

@Repository
public class QueryRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public QueryRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> query(@NotNull String sql) {
        var rowMapper = new ColumnMapRowMapper();
        return this.jdbcTemplate.query(slim(sql), rowMapper);
    }
}
