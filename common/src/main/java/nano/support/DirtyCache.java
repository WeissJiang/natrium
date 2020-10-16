package nano.support;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.util.Assert;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Cache updated at a certain pace
 *
 * @see ConcurrentMapCache
 */
public class DirtyCache extends AbstractValueAdaptingCache {

    private final String name;

    private final Timer timer;

    private final ConcurrentMap<Object, Object> store;

    public DirtyCache(String name) {
        this(name, true, new ConcurrentHashMap<>(256));
    }

    public DirtyCache(String name, boolean allowNullValues, ConcurrentMap<Object, Object> store) {
        super(allowNullValues);
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(store, "Store must not be null");
        this.name = name;
        this.store = store;
        this.timer = new Timer();
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
        var newlyLoaded = new AtomicBoolean(false);
        var value = (T) fromStoreValue(this.store.computeIfAbsent(key, k -> {
            newlyLoaded.set(true);
            return this.toStoreValue(this.loadValue(k, valueLoader));
        }));
        if (!newlyLoaded.get()) {
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
        var cache = this;
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                var loadedValue = cache.loadValue(key, valueLoader);
                cache.put(key, loadedValue);

            }
        }, 0);
    }

    protected <T> T loadValue(@NotNull Object key, @NotNull Callable<T> valueLoader) {
        try {
            return valueLoader.call();
        } catch (Throwable ex) {
            throw new ValueRetrievalException(key, valueLoader, ex);
        }
    }
}
