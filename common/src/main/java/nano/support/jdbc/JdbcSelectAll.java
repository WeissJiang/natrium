package nano.support.jdbc;

import lombok.NonNull;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

import static nano.support.EntityUtils.*;

public class JdbcSelectAll<T> {

    private final Class<T> entityClass;
    private final RowMapper<T> rowMapper;

    private NamedParameterJdbcTemplate jdbcTemplate;
    private String tableName;
    private String[] whereEqualsColumns;
    private Integer limit;
    private Integer offset;

    public JdbcSelectAll(@NonNull Class<T> entityClass) {
        this.entityClass = entityClass;
        this.rowMapper = new BeanPropertyRowMapper<>(entityClass);
    }

    public JdbcSelectAll<T> usesJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        return this;
    }

    public JdbcSelectAll<T> withTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * just equals
     */
    public JdbcSelectAll<T> whereEqual(String... whereEqualsColumns) {
        this.whereEqualsColumns = whereEqualsColumns;
        return this;
    }

    public JdbcSelectAll<T> limit(int limit) {
        Assert.isTrue(limit >= 0, "limit < 0");
        this.limit = limit;
        return this;
    }

    public JdbcSelectAll<T> offset(int offset) {
        Assert.isTrue(offset >= 0, "offset < 0");
        this.offset = offset;
        return this;
    }

    public List<T> query(SqlParameterSource paramSource) {
        this.jdbcTemplateProvided();
        return this.jdbcTemplate.query(this.getSql(), paramSource, this.rowMapper);
    }

    public List<T> query(Map<String, ?> paramMap) {
        this.jdbcTemplateProvided();
        return this.jdbcTemplate.query(this.getSql(), paramMap, this.rowMapper);
    }

    public List<T> query() {
        this.jdbcTemplateProvided();
        return this.jdbcTemplate.query(this.getSql(), this.rowMapper);
    }

    public String getSql() {
        Assert.hasText(this.tableName, "this.tableName");
        var sb = new StringBuilder();
        var columns = String.join(", ", entityColumnNames(this.entityClass));
        sb.append("SELECT ").append(columns).append(" FROM ").append(tableName);
        if (this.whereEqualsColumns != null && this.whereEqualsColumns.length > 0) {
            sb.append(" WHERE ");
            boolean first = true;
            for (String where : this.whereEqualsColumns) {
                var sourceName = propertyName(where);
                if (first) {
                    sb.append(where).append(" = :").append(sourceName);
                    first = false;
                } else {
                    sb.append(" AND ").append(where).append(" = :").append(sourceName);
                }
            }
        }
        if (this.limit != null) {
            sb.append(" LIMIT ").append(this.limit);
        }
        if (this.offset != null) {
            sb.append(" OFFSET ").append(this.offset);
        }
        return sb.append(";").toString();
    }

    private void jdbcTemplateProvided() {
        Assert.notNull(this.jdbcTemplate, "this.jdbcTemplate is null");
    }

}
