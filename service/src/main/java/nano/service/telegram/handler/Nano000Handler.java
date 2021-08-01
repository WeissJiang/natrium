package nano.service.telegram.handler;

import nano.service.nano.model.Bot;
import nano.service.telegram.BotContext;
import nano.support.Onion;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class Nano000Handler implements Onion.Middleware<BotContext> {

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        if (Bot.NANO_000.equals(context.getBot().name())) {
            context.sendMessage("nano-000");
        } else {
            next.next();
        }
    }
}
