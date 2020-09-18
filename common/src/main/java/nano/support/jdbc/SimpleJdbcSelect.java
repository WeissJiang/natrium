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

/**
 * 简易实体字段查询SQL生成器
 *
 * @param <T> 对应实体类
 * @author cbdyzj
 * @see org.springframework.jdbc.core.simple.SimpleJdbcInsert
 * @see org.springframework.jdbc.core.simple.SimpleJdbcCall
 * @since 2020.9.5
 */
public class SimpleJdbcSelect<T> {

    private final Class<T> entityClass;
    private final RowMapper<T> rowMapper;

    private NamedParameterJdbcTemplate jdbcTemplate;
    private String tableName;

    private String[] whereEqualColumns;
    private String[] whereInColumns;
    private String whereClause;

    private Integer limit;
    private Integer offset;

    public SimpleJdbcSelect(@NonNull Class<T> entityClass) {
        this.entityClass = entityClass;
        this.rowMapper = new BeanPropertyRowMapper<>(entityClass);
    }

    public SimpleJdbcSelect<T> usesJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        return this;
    }

    public SimpleJdbcSelect<T> withTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public SimpleJdbcSelect<T> whereEqual(String... whereEqualColumns) {
        this.whereEqualColumns = whereEqualColumns;
        return this;
    }

    public SimpleJdbcSelect<T> whereIn(String... whereInColumns) {
        this.whereInColumns = whereInColumns;
        return this;
    }

    public SimpleJdbcSelect<T> whereClause(String whereClause) {
        this.whereClause = whereClause;
        return this;
    }

    public SimpleJdbcSelect<T> limit(int limit) {
        Assert.isTrue(limit >= 0, "limit < 0");
        this.limit = limit;
        return this;
    }

    public SimpleJdbcSelect<T> offset(int offset) {
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
        boolean firstWhereClause = true;
        if (notEmpty(this.whereEqualColumns)) {
            Assert.state(this.whereClause == null, "\"whereEqualColumns\" and \"WhereClause\" cannot exist at the same time");
            for (String where : this.whereEqualColumns) {
                var sourceName = propertyName(where);
                if (firstWhereClause) {
                    sb.append(" WHERE ").append(where).append(" = :").append(sourceName);
                    firstWhereClause = false;
                } else {
                    sb.append(" AND ").append(where).append(" = :").append(sourceName);
                }
            }
        }
        if (notEmpty(this.whereInColumns)) {
            Assert.state(this.whereClause == null, "\"WhereInColumns\" and \"WhereClause\" cannot exist at the same time");
            for (String where : this.whereInColumns) {
                var sourceName = propertyName(where);
                if (firstWhereClause) {
                    sb.append(" WHERE ").append(where).append(" IN (:").append(sourceName).append(")");
                    firstWhereClause = false;
                } else {
                    sb.append(" AND ").append(where).append(" IN (:").append(sourceName).append(")");
                }
            }
        }
        if (this.whereClause != null) {
            sb.append(" ").append(slim(this.whereClause));
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

    private static boolean notEmpty(Object[] a) {
        return a != null && a.length > 0;
    }

}
