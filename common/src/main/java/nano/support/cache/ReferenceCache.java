package nano.support.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.requireNonNull;

/**
 * Cache uses SoftReference
 *
 * @see org.springframework.cache.concurrent.ConcurrentMapCache
 */
public class ReferenceCache {

    public static final String WEAK_REFERENCE = "java.lang.ref.WeakReference";
    public static final String SOFT_REFERENCE = "java.lang.ref.SoftReference";

    private static final NullValue NULL_VALUE = new NullValue();

    private final String name;

    private final ConcurrentMap<Object, Reference<Object>> store = new ConcurrentHashMap<>();

    public ReferenceCache(@NotNull String name) {
        requireNonNull(name, "Name must not be null");
        this.name = name;
    }

    public <T> @Nullable T get(@NotNull Object key, @NotNull Callable<T> valueLoader, @NotNull Class<? extends Reference<Object>> refClass) {
        var ref = this.store.get(key);
        var value = (Object) null;
        // Cache hit
        if (ref != null && (value = ref.get()) != null) {
            return value == NULL_VALUE ? null : cast(value);
        }
        // Cache not hit
        try {
            value = valueLoader.call();
            ref = refClass.getConstructor(Object.class).newInstance(value == null ? NULL_VALUE : value);
            this.store.put(key, ref);
            return cast(value);
        } catch (Throwable ex) {
            throw new ValueRetrievalException(key, valueLoader, ex);
        }
    }


    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull ConcurrentMap<Object, Reference<Object>> getNativeCache() {
        return this.store;
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object o) {
        return (T) o;
    }

    public static class ValueRetrievalException extends RuntimeException {

        @Nullable
        private final Object key;

        public ValueRetrievalException(@Nullable Object key, Callable<?> loader, Throwable ex) {
            super(String.format("Value for key '%s' could not be loaded using '%s'", key, loader), ex);
            this.key = key;
        }

        @Nullable
        public Object getKey() {
            return this.key;
        }
    }

    public interface Callable<V> {

        V call() throws Throwable;
    }

    private static class NullValue {
    }
}
