package nano.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    public static final String TOKEN = "X-Token";

    @NonNull
    private final SecurityService securityService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var token = request.getHeader(TOKEN);
        this.securityService.checkNanoToken(token);
        return true;
    }
}
