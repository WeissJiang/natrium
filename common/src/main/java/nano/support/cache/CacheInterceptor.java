package nano.support.cache;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;

/**
 * @see LocalCached
 */
public class CacheInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(@NotNull MethodInvocation invocation) throws Throwable {
        return invocation.proceed();
    }

}
