package nano.web.nano.repository;

import nano.web.nano.entity.NanoTask;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static nano.support.EntityUtils.slim;

@Repository
public class TaskRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TaskRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public @NotNull List<@NotNull NanoTask> queryAllAvailableTaskList() {
        var sql = """
                SELECT id, name, description, options
                FROM nano_task
                WHERE enabled = TRUE
                  AND last_execution_time + time_interval < NOW();
                """;
        var rowMapper = new BeanPropertyRowMapper<>(NanoTask.class);
        return this.jdbcTemplate.query(slim(sql), rowMapper);
    }

    public void updateLastExecutionTime(@NotNull Integer taskId, @NotNull Timestamp lastExecutionTime) {
        var sql = """
                UPDATE nano_task
                SET last_execution_time = :lastExecutionTime
                WHERE id = :taskId;
                """;
        this.jdbcTemplate.update(slim(sql), Map.of("taskId", taskId, "lastExecutionTime", lastExecutionTime));
    }
}
