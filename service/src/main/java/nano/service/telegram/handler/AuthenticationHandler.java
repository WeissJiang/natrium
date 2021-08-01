package nano.service.telegram.handler;

import nano.service.nano.entity.NanoToken;
import nano.service.telegram.BotContext;
import nano.support.Onion;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Authentication
 * <p>
 * After {@link SessionInitializeHandler}
 */
@Order(-1000)
@Component
public class AuthenticationHandler implements Onion.Middleware<BotContext> {

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        var session = context.getSession();
        if (session != null && !NanoToken.VALID.equals(session.getToken().status())) {
            context.replyMessage("Telegram token is not valid");
        } else {
            next.next();
        }
    }
}
