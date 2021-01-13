package nano.support;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

@FunctionalInterface
public interface Task {

    void execute(@NotNull Map<String, ?> options);
}
