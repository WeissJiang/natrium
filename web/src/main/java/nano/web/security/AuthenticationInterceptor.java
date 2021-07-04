package nano.web.security;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        var authorized = handlerMethod.getMethodAnnotation(Authorized.class);
        if (authorized == null) {
            authorized = handlerMethod.getBeanType().getAnnotation(Authorized.class);
        }
        if (authorized == null) {
            return true;
        }
        var tokenChecker = this.tokenChecker(request, List.of(authorized.privilege()));
        var ticketChecker = this.ticketChecker(request, List.of(authorized.ticket()));
        // privilege or ticket
        if (authorized.privilege().length > 0 && authorized.ticket().length > 0) {
            var checkerList = List.of(tokenChecker, ticketChecker);
            var errors = checkerList.stream()
                    .map(AuthenticationInterceptor::runChecker)
                    .filter(Objects::nonNull)
                    .map(Exception::getMessage);
            if (errors.count() == checkerList.size()) {
                var message = errors.collect(Collectors.joining(","));
                throw new AuthenticationException(message);
            }
        }
        // privilege
        else if (authorized.privilege().length > 0) {
            tokenChecker.run();
        }
        // ticket
        else if (authorized.ticket().length > 0) {
            ticketChecker.run();
        }
        return true;
    }

    private Runnable tokenChecker(@NotNull HttpServletRequest request, List<String> privilegeList) {
        return () -> {
            var token = getToken(request);
            var desensitizedToken = desensitizeToken(token);
            this.securityService.checkTokenPrivilege(desensitizedToken, privilegeList);
            request.setAttribute(DESENSITIZED_X_TOKEN, desensitizedToken);
        };
    }

    private Runnable ticketChecker(@NotNull HttpServletRequest request, List<String> ticketNameList) {
        return () -> this.securityService.checkTicketPermission(getTicket(request), ticketNameList);
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

    private static @Nullable Exception runChecker(@NotNull Runnable runnable) {
        try {
            runnable.run();
            return null;
        } catch (Exception ex) {
            return ex;
        }
    }
}
