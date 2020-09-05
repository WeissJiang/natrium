package nano.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.util.List;

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
            nano.append(key).append(property).append("\n");
        }
        var memoryMXBean = ManagementFactory.getMemoryMXBean();

        var heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        nano.append("heapMemoryUsage.init: ").append(heapMemoryUsage.getInit()).append("\n");
        nano.append("heapMemoryUsage.committed: ").append(heapMemoryUsage.getCommitted()).append("\n");
        nano.append("heapMemoryUsage.max: ").append(heapMemoryUsage.getMax()).append("\n");
        nano.append("heapMemoryUsage.used: ").append(heapMemoryUsage.getUsed()).append("\n");

        var nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        nano.append("nonHeapMemoryUsage.init: ").append(nonHeapMemoryUsage.getInit()).append("\n");
        nano.append("nonHeapMemoryUsage.committed: ").append(nonHeapMemoryUsage.getCommitted()).append("\n");
        nano.append("nonHeapMemoryUsage.max: ").append(nonHeapMemoryUsage.getMax()).append("\n");
        nano.append("nonHeapMemoryUsage.used: ").append(nonHeapMemoryUsage.getUsed()).append("\n");

        var objectPendingFinalizationCount = memoryMXBean.getObjectPendingFinalizationCount();
        nano.append("objectPendingFinalizationCount").append(objectPendingFinalizationCount).append("\n");
        return nano.toString();
    }

    public String getPostgresVersion() {
        var mapper = new SingleColumnRowMapper<String>();
        var version = this.jdbcTemplate.query("SELECT VERSION();", mapper);
        return String.join(", ", version);
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

}
