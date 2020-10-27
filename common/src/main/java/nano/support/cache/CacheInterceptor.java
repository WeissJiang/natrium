package nano.support.cache;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import static java.util.Objects.hash;
import static nano.support.Sugar.cast;

/**
 * @see LocalCached
 * @see ReferenceCache
 */
public class CacheInterceptor implements MethodInterceptor {

    private final ReferenceCache cache = new ReferenceCache("localCache@" + this.hashCode());

    @Override
    public Object invoke(@NotNull MethodInvocation invocation) throws Exception {
        var localCached = invocation.getMethod().getAnnotation(LocalCached.class);
        Assert.notNull(localCached, "LocalCached annotation is null");
        var ref = getRefClass(localCached.value());
        return this.cache.get(new CacheKey(invocation), invocation::proceed, cast(ref));
    }

    private static Class<?> getRefClass(String refName) throws ClassNotFoundException {
        if (StringUtils.isEmpty(refName)) {
            refName = ReferenceCache.WEAK_REFERENCE;
        }
        return ClassUtils.forName(refName, null);
    }

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
            int result = hash(target, method);
            result = 31 * result + Arrays.hashCode(arguments);
            return result;
        }
    }
}
