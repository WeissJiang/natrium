package nano.support;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.util.Assert;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Cache uses SoftReference
 *
 * @see ConcurrentMapCache
 */
public class SoftCache {

    private final String name;

    private final ConcurrentMap<Object, SoftReference<Object>> store = new ConcurrentHashMap<>();

    public SoftCache(@NotNull String name) {
        Assert.notNull(name, "Name must not be null");
        this.name = name;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull ConcurrentMap<Object, SoftReference<Object>> getNativeCache() {
        return this.store;
    }

}
