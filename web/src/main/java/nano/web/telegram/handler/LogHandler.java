package nano.web.telegram.handler;

import nano.support.Json;
import nano.support.Onion;
import nano.web.telegram.BotContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 记日志
 */
@Component
public class LogHandler implements Onion.Middleware<BotContext> {

    private static final Logger log = LoggerFactory.getLogger(LogHandler.class);

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        var parameters = context.getParameters();
        log.info("parameters: {}", Json.encode(parameters));
        // next
        next.next();
    }
}
