package nano.telegram.handler;

import lombok.extern.slf4j.Slf4j;
import nano.support.Onion;
import nano.telegram.BotContext;

@Slf4j
public class LogHandler implements Onion.Middleware<BotContext> {

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var parameters = context.getParameters();
        log.info("parameters: {}", parameters);
        next.next();
        var attributes = context.getAttributes();
        log.info("result: {}", attributes.get("result"));
    }
}
