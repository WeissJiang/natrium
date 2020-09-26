package nano.web.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static nano.web.security.TokenCode.*;

/**
 * Token脱敏
 */
@Component
public class TokenDesensitizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {
        var token = request.getHeader(X_TOKEN);
        if (token != null) {
            request.setAttribute(D_TOKEN, desensitizeToken(token));
        }
        return true;
    }
}
