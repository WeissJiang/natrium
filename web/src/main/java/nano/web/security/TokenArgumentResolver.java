package nano.web.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static nano.web.security.TokenCode.*;

/**
 * Resolve token argument
 *
 * @see Token
 * @see TokenCode
 */
@Component
public class TokenArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Token.class);
    }

    @Override
    public String resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        var token = webRequest.getHeader(X_TOKEN);
        return desensitizeToken(token);
    }
}
