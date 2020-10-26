package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.telegram.BotContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 异常处理
 * <p>
 * Before all middleware
 */
@Order(-1000_000)
@Component
public class ExceptionHandler implements Onion.Middleware<BotContext> {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) {
        try {
            next.next();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            sendMessageIfPossible(context, "nano fault: " + ex.getMessage());
        }
    }

    private static void sendMessageIfPossible(BotContext context, String text) {
        if (context.chatId() != null) {
            context.sendMessage(text);
        }
    }
}
