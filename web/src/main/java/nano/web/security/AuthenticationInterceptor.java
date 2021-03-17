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
        var checkToken = this.getCheckToken(request, List.of(authorized.privilege()));
        var checkTicket = this.getCheckTicket(request, List.of(authorized.ticket()));
        // privilege or ticket
        if (authorized.privilege().length > 0 && authorized.ticket().length > 0) {
            var checkList = List.of(checkToken, checkTicket);
            var exceptions = checkList.stream()
                    .map(it -> {
                        try {
                            it.run();
                            return null;
                        } catch (Exception ex) {
                            return ex;
                        }
                    })
                    .filter(Objects::nonNull);
            if (exceptions.count() == checkList.size()) {
                var message = exceptions.map(Throwable::getMessage).collect(Collectors.joining(","));
                throw new AuthenticationException(message);
            }
        }
        // privilege
        else if (authorized.privilege().length > 0) {
            checkToken.run();
        }
        // ticket
        else if (authorized.ticket().length > 0) {
            checkTicket.run();
        }
        return true;
    }

    private Runnable getCheckToken(@NotNull HttpServletRequest request, List<String> privilegeList) {
        return () -> {
            var token = getToken(request);
            var desensitizedToken = desensitizeToken(token);
            this.securityService.checkTokenPrivilege(desensitizedToken, privilegeList);
            request.setAttribute(DESENSITIZED_X_TOKEN, desensitizedToken);
        };
    }

    private Runnable getCheckTicket(@NotNull HttpServletRequest request, List<String> ticketNameList) {
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
}
