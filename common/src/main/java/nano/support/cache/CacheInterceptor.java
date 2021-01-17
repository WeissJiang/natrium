package nano.support.cache;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Cache interceptor
 *
 * @author cbdyzj
 * @see LocallyCacheable
 * @since 2020.10.27
 */
public class CacheInterceptor implements MethodInterceptor {

    private final Cache cache;

    public CacheInterceptor() {
        this.cache = new ConcurrentMapCache("localCache@" + this.hashCode());
    }

    public CacheInterceptor(@NotNull Cache cache) {
        this.cache = Objects.requireNonNull(cache, "cache is null");
    }

    @Override
    public Object invoke(@NotNull MethodInvocation invocation) {
        var locallyCacheable = invocation.getMethod().getAnnotation(LocallyCacheable.class);
        Assert.notNull(locallyCacheable, "LocallyCacheable annotation missing");
        var cacheKey = new CacheKey(locallyCacheable.value(), invocation.getArguments());
        return this.cache.get(cacheKey, getValueLoader(invocation));
    }

    private static Callable<Object> getValueLoader(@NotNull MethodInvocation invocation) {
        return () -> {
            try {
                return invocation.proceed();
            } catch (Throwable ex) {
                throw convertException(ex);
            }
        };
    }

    private static Exception convertException(Throwable t) {
        return t instanceof Exception ? (Exception) t : new RuntimeException(t);
    }

    /**
     * May cause memory leak
     */
    public static class CacheKey {

        private final String prefix;
        private final Object[] arguments;

        public CacheKey(@NotNull String prefix, @NotNull Object[] arguments) {
            this.prefix = prefix;
            this.arguments = arguments;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return Objects.equals(prefix, cacheKey.prefix) &&
                    Arrays.equals(arguments, cacheKey.arguments);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(prefix);
            result = 31 * result + Arrays.hashCode(arguments);
            return result;
        }
    }
}
