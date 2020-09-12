package nano.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    public static final String TOKEN = "X-Token";

    @lombok.NonNull
    private final SecurityService securityService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            if (handlerMethod.hasMethodAnnotation(Authorized.class)
                || handlerMethod.getBeanType().isAnnotationPresent(Authorized.class)) {
                var token = request.getHeader(TOKEN);
                this.securityService.checkNanoToken(token);
            }
        }
        return true;
    }
}
