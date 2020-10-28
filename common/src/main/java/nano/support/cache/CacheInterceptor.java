package nano.support.cache;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @see LocallyCacheable
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
        return this.cache.get(new CacheKey(invocation), getValueLoader(invocation));
    }

    private Callable<Object> getValueLoader(@NotNull MethodInvocation invocation) {
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

        private final Object target;
        private final Method method;
        private final Object[] arguments;

        public CacheKey(@NotNull MethodInvocation invocation) {
            this.target = invocation.getThis();
            this.method = invocation.getMethod();
            this.arguments = invocation.getArguments();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return Objects.equals(target, cacheKey.target) &&
                   Objects.equals(method, cacheKey.method) &&
                   Arrays.equals(arguments, cacheKey.arguments);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(target, method);
            result = 31 * result + Arrays.hashCode(arguments);
            return result;
        }
    }
}
