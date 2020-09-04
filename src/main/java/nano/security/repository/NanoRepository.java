package nano.security.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NanoRepository {

    @NonNull
    private final JdbcTemplate jdbcTemplate;

    public String getVersion() {
        var mapper = new SingleColumnRowMapper<String>();
        var version = this.jdbcTemplate.query("SELECT VERSION();", mapper);
        return String.join(", ", version);
    }
}
