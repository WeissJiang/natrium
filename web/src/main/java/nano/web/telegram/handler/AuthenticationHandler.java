package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.security.entity.NanoToken;
import nano.web.telegram.BotContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

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
