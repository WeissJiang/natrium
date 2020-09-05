package nano.web.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import java.lang.management.ManagementFactory;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NanoService {

    @NonNull
    private final JdbcTemplate jdbcTemplate;

    public String nano() {
        var nano = new StringBuilder();
        nano.append("Postgres: ").append(this.getPostgresVersion()).append("\n");
        for (String key : SYSTEM_PROPERTIES) {
            var property = System.getProperty(key);
            nano.append(key).append(" :").append(property).append("\n");
        }
        var memoryMXBean = ManagementFactory.getMemoryMXBean();

        var heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        nano.append("heapMemoryUsage.init: ").append(megabytes(heapMemoryUsage.getInit())).append("\n");
        nano.append("heapMemoryUsage.committed: ").append(megabytes(heapMemoryUsage.getCommitted())).append("\n");
        nano.append("heapMemoryUsage.max: ").append(megabytes(heapMemoryUsage.getMax())).append("\n");
        nano.append("heapMemoryUsage.used: ").append(megabytes(heapMemoryUsage.getUsed())).append("\n");

        var nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        nano.append("nonHeapMemoryUsage.init: ").append(megabytes(nonHeapMemoryUsage.getInit())).append("\n");
        nano.append("nonHeapMemoryUsage.committed: ").append(megabytes(nonHeapMemoryUsage.getCommitted())).append("\n");
        nano.append("nonHeapMemoryUsage.max: ").append(megabytes(nonHeapMemoryUsage.getMax())).append("\n");
        nano.append("nonHeapMemoryUsage.used: ").append(megabytes(nonHeapMemoryUsage.getUsed())).append("\n");

        var objectPendingFinalizationCount = memoryMXBean.getObjectPendingFinalizationCount();
        nano.append("objectPendingFinalizationCount: ").append(objectPendingFinalizationCount).append("\n");
        return nano.toString();
    }

    public String getPostgresVersion() {
        try {
            var mapper = new SingleColumnRowMapper<String>();
            var version = this.jdbcTemplate.query("SELECT VERSION();", mapper);
            return String.join(", ", version);
        } catch (Exception ex) {
            log.warn(ex.getMessage());
            return "?";
        }
    }

    private static final List<String> SYSTEM_PROPERTIES = List.of(
            "java.specification.version",
            "java.vm.vendor",
            "java.vendor.url",
            "user.timezone",
            "java.vm.specification.version",
            "os.name", "sun.cpu.endian",
            "java.specification.vendor",
            "java.version.date",
            "java.vm.compressedOopsMode",
            "java.vm.specification.vendor",
            "java.specification.name",
            "sun.management.compiler",
            "java.runtime.version",
            "os.version",
            "java.runtime.name",
            "file.encoding",
            "java.vm.name",
            "java.version",
            "os.arch",
            "java.vm.specification.name",
            "java.vm.info",
            "java.vm.version",
            "java.class.version"
    );

    private String megabytes(long bytes) {
        return "%sMB".formatted(DataSize.ofBytes(bytes).toMegabytes());
    }

}
