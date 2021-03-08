package nano.web.security;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static nano.web.security.TokenCode.*;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final SecurityService securityService;

    public AuthenticationInterceptor(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        var handlerMethod = (HandlerMethod) handler;
        var authorized = handlerMethod.getMethodAnnotation(Authorized.class);
        if (authorized == null) {
            authorized = handlerMethod.getBeanType().getAnnotation(Authorized.class);
        }
        if (authorized == null) {
            return true;
        }
        if (authorized.privilege().length > 0) {
            var token = getToken(request);
            var desensitizedToken = desensitizeToken(token);
            this.securityService.checkTokenPrivilege(desensitizedToken, List.of(authorized.privilege()));
            request.setAttribute(DESENSITIZED_X_TOKEN, desensitizedToken);
        }
        if (authorized.ticket().length > 0) {
            var ticket = getTicket(request);
            this.securityService.checkTicketPermission(ticket, List.of(authorized.ticket()));
        }

        return true;
    }

    private static @Nullable String getToken(@NotNull HttpServletRequest request) {
        var token = request.getHeader(X_TOKEN);
        if (token == null) {
            token = request.getParameter(TOKEN);
        }
        return token;
    }

    private static @Nullable String getTicket(@NotNull HttpServletRequest request) {
        var ticket = request.getHeader(X_TICKET);
        if (ticket == null) {
            ticket = request.getParameter(TICKET);
        }
        return ticket;
    }
}
