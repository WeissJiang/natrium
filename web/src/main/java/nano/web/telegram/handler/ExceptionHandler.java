package nano.web.telegram.handler;

import lombok.extern.slf4j.Slf4j;
import nano.support.Onion;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExceptionHandler implements Onion.Middleware<BotContext> {

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
            context.sendMessage(text);
        }
    }
}
