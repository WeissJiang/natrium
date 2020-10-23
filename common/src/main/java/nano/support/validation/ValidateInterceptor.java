package nano.support.validation;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * @see Validating
 * @see Validator
 */
public class ValidateInterceptor implements MethodInterceptor {

    private final BeanFactory beanFactory;

    public ValidateInterceptor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object invoke(MethodInvocation invocation) {
        var method = invocation.getMethod();
        var target = invocation.getThis();
        var arguments = invocation.getArguments();
        // validate
        var validating = method.getAnnotation(Validating.class);
        Assert.notNull(validating, "Validating annotation is null");
        for (var validatorClass : validating.value()) {
            var validator = this.beanFactory.getBean(validatorClass);
            var message = validator.validate(arguments);
            if (message != null) {
                throw new IllegalArgumentException(message);
            }
        }
        return ReflectionUtils.invokeMethod(method, target, arguments);
    }

}
