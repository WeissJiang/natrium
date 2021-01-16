package nano.web.nano;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class NanoService implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(NanoService.class);

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

    private ApplicationContext context;

    private final JdbcTemplate jdbcTemplate;

    public NanoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public @NotNull Map<String, String> system() {
        var properties = new LinkedHashMap<String, String>();

        properties.put("Postgres", this.getPostgresVersion());

        // system properties
        SYSTEM_PROPERTIES.forEach(key -> properties.put(key, System.getProperty(key)));

        var memoryMXBean = ManagementFactory.getMemoryMXBean();

        // heap memory usage
        var heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        properties.put("heapMemoryUsage.init", megabytes(heapMemoryUsage.getInit()));
        properties.put("heapMemoryUsage.committed:", megabytes(heapMemoryUsage.getCommitted()));
        properties.put("heapMemoryUsage.max", megabytes(heapMemoryUsage.getMax()));
        properties.put("heapMemoryUsage.used", megabytes(heapMemoryUsage.getUsed()));

        // non heap memory usage
        var nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        properties.put("nonHeapMemoryUsage.init", megabytes(nonHeapMemoryUsage.getInit()));
        properties.put("nonHeapMemoryUsage.committed", megabytes(nonHeapMemoryUsage.getCommitted()));
        properties.put("nonHeapMemoryUsage.max", megabytes(nonHeapMemoryUsage.getMax()));
        properties.put("nonHeapMemoryUsage.used", megabytes(nonHeapMemoryUsage.getUsed()));

        // object pending finalization count
        properties.put("objectPendingFinalizationCount", String.valueOf(memoryMXBean.getObjectPendingFinalizationCount()));
        return properties;
    }

    /**
     * Get current Postgres version
     */
    public @NotNull String getPostgresVersion() {
        try {
            var mapper = new SingleColumnRowMapper<String>();
            var version = this.jdbcTemplate.query("SELECT VERSION();", mapper);
            return String.join(", ", version);
        } catch (Exception ex) {
            log.warn(ex.getMessage());
            return "?";
        }
    }

    /**
     * Get all bean definition names of the application
     */
    public @NotNull String[] getBeanDefinitionNames(@Nullable String q) {
        var beanDefinitionNames = this.context.getBeanDefinitionNames();
        var beans = Arrays.stream(beanDefinitionNames).sorted();
        if (q != null) {
            beans = beans.filter(it -> it.contains(q));
        }
        return beans.toArray(String[]::new);
    }

    /**
     * bytes -> mega bytes
     */
    private @NotNull String megabytes(long bytes) {
        return "%sMB".formatted(DataSize.ofBytes(bytes).toMegabytes());
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
