package nano.web.nano;

import nano.support.validation.Validating;
import nano.support.validation.Validator;
import nano.web.Application;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * @see Validating
 * @see Validator
 * @see Application#validatePointcutAdvisor
 */
@Component
public class ValidateInterceptor implements MethodInterceptor, ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public Object invoke(MethodInvocation invocation) {
        var method = invocation.getMethod();
        var target = invocation.getThis();
        var arguments = invocation.getArguments();
        // validate
        var validating = method.getAnnotation(Validating.class);
        Assert.notNull(validating, "Validating annotation is null");
        for (Class<? extends Validator> validatorClass : validating.value()) {
            var validator = this.context.getBean(validatorClass);
            var message = validator.validate(arguments);
            if (message != null) {
                throw new IllegalArgumentException(message);
            }
        }
        return ReflectionUtils.invokeMethod(method, target, arguments);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
