package nano.telegram.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.support.Onion;
import nano.telegram.BotApi;
import nano.telegram.BotContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionHandler implements Onion.Middleware<BotContext> {

    @NonNull
    private final BotApi botApi;

    @Override
    public void via(BotContext context, Onion.Next next) {
        Integer chatId = context.readParameter("$.message.chat.id");
        try {
            next.next();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            if (chatId != null) {
                this.botApi.sendMessage(chatId, "nano故障：" + ex.getMessage());
            }
        }
    }
}
