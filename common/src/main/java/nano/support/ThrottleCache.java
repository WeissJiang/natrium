package nano.support;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Cache updated at a certain pace
 *
 * @see ConcurrentMapCache
 */
public class ThrottleCache extends AbstractValueAdaptingCache {

    // 1 seconds
    private static final int INTERVAL_TIME = 1;

    private final String name;

    private final ConcurrentMap<Object, Instant> timeRecorder = new ConcurrentHashMap<>(256);

    private final ConcurrentMap<Object, Object> store;

    private final TaskExecutor taskExecutor;

    public ThrottleCache(String name, TaskExecutor taskExecutor) {
        this(name, true, taskExecutor, new ConcurrentHashMap<>(256));
    }

    public ThrottleCache(String name, boolean allowNullValues, TaskExecutor taskExecutor, ConcurrentMap<Object, Object> store) {
        super(allowNullValues);
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(store, "TaskExecutor must not be null");
        Assert.notNull(store, "Store must not be null");
        this.name = name;
        this.taskExecutor = taskExecutor;
        this.store = store;
    }

    @Override
    protected Object lookup(@NotNull Object key) {
        return this.store.get(key);
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull ConcurrentMap<Object, Object> getNativeCache() {
        return this.store;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(@NotNull Object key, @NotNull Callable<T> valueLoader) {
        this.evictIfExpired(key);
        var reloaded = new AtomicBoolean(false);
        var value = (T) fromStoreValue(this.store.computeIfAbsent(key, k -> {
            reloaded.set(true);
            return this.toStoreValue(this.loadValueSync(k, valueLoader));
        }));
        if (!reloaded.get()) {
            this.reloadedValueAsync(key, valueLoader);
        }
        return value;
    }

    @Override
    public void put(@NotNull Object key, Object value) {
        this.store.put(key, this.toStoreValue(value));
    }

    @Override
    public void evict(@NotNull Object key) {
        this.store.remove(key);
    }

    @Override
    public void clear() {
        this.store.clear();
    }

    @Override
    public boolean evictIfPresent(@NotNull Object key) {
        return (this.store.remove(key) != null);
    }

    @Override
    public boolean invalidate() {
        boolean notEmpty = !this.store.isEmpty();
        this.store.clear();
        return notEmpty;
    }

    @Override
    public @Nullable ValueWrapper putIfAbsent(@NotNull Object key, @Nullable Object value) {
        var existing = this.store.putIfAbsent(key, toStoreValue(value));
        return toValueWrapper(existing);
    }

    protected void reloadedValueAsync(@NotNull Object key, @NotNull Callable<?> valueLoader) {
        var recordedTime = this.timeRecorder.get(key);
        if (recordedTime != null && !isExpired(recordedTime)) {
            return;
        }
        this.taskExecutor.execute(() -> {
            Object o = this.loadValueSync(key, valueLoader);
            this.put(key, o);
        });
    }

    private void evictIfExpired(Object key) {
        var recordedTime = this.timeRecorder.get(key);
        if (recordedTime != null && isExpired(recordedTime)) {
            this.evictIfPresent(key);
        }
    }

    protected <T> T loadValueSync(@NotNull Object key, @NotNull Callable<T> valueLoader) {
        try {
            var value = valueLoader.call();
            this.timeRecorder.put(key, Instant.now());
            return value;
        } catch (Throwable ex) {
            throw new ValueRetrievalException(key, valueLoader, ex);
        }
    }

    private static boolean isExpired(Instant recordedTime) {
        return recordedTime.plusSeconds(INTERVAL_TIME).isAfter(Instant.now());
    }
}
