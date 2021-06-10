package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.nano.model.Bot;
import nano.web.telegram.BotContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Nano000Handler implements Onion.Middleware<BotContext> {

    private static final Logger log = LoggerFactory.getLogger(Nano000Handler.class);

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        if (Bot.NANO_000.equals(context.bot().getName())) {
            log.info("context.text(): {}", context.text());
        } else {
            next.next();
        }
    }
}
