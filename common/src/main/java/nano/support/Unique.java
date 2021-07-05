package nano.support;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A tool for checking whether items are duplicated
 *
 * @author cbdyzj
 * @since 2021.3.16
 */
public class Unique<T> {

    private final Set<T> us = new HashSet<>();

    public void accept(T item) {
        this.accept(item, () -> "duplicate item: %s".formatted(item));
    }

    public void accept(T item, @NotNull String message) {
        this.accept(item, () -> message);
    }

    public void accept(T item, @NotNull Supplier<@NotNull String> stringSupplier) {
        if (this.us.contains(item)) {
            throw new IllegalArgumentException(stringSupplier.get());
        }
        this.us.add(item);
    }
}
