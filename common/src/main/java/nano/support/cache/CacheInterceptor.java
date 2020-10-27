package nano.support.cache;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;

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
        return this.cache.get(getCacheKey(invocation), invocation::proceed, cast(ref));
    }

    private static int getCacheKey(@NotNull MethodInvocation invocation) {
        return Objects.hash(invocation.getThis(), invocation.getMethod(), invocation.getArguments());
    }

    private static Class<?> getRefClass(String refName) throws ClassNotFoundException {
        if (StringUtils.isEmpty(refName)) {
            refName = ReferenceCache.WEAK_REFERENCE;
        }
        return ClassUtils.forName(refName, null);
    }
}
