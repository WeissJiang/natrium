package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.telegram.BotContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 异常处理
 */
@Component
public class ExceptionHandler implements Onion.Middleware<BotContext> {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public void via(BotContext context, Onion.Next next) {
        try {
            next.next();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            sendMessageIfPossible(context, "nano故障：" + ex.getMessage());
        }
    }

    private static void sendMessageIfPossible(BotContext context, String text) {
        if (context.chatId() != null) {
            context.replyMessage(text);
        }
    }
}
