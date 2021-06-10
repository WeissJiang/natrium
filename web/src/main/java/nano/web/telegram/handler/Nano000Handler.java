package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.nano.model.Bot;
import nano.web.telegram.BotContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class Nano000Handler implements Onion.Middleware<BotContext> {


    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        if (Bot.NANO_000.equals(context.bot().getName())) {
            context.replyMessage("Got: " + context.commands());
        } else {
            next.next();
        }
    }
}
