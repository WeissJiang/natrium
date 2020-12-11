package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.nano.entity.NanoToken;
import nano.web.telegram.BotContext;
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
        if (session != null && !NanoToken.VALID.equals(session.getToken().getStatus())) {
            context.replyMessage("Telegram token is not valid");
        } else {
            next.next();
        }
    }
}
