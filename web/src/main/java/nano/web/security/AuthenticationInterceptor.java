package nano.web.security;

import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationInterceptor implements HandlerInterceptor {

    public static final String TOKEN = "X-Token";

    private final SecurityService securityService;

    public AuthenticationInterceptor(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        var token = request.getHeader(TOKEN);
        this.securityService.checkNanoToken(token);
        return true;
    }
}
