package nano.telegram.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.support.Json;
import nano.support.Onion;
import nano.telegram.BotApi;
import nano.telegram.BotContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EchoHandler implements Onion.Middleware<BotContext> {

    @NonNull
    private final BotApi botApi;

    public void via(BotContext context, Onion.Next next) throws Exception {
        Integer chatId = context.readParameter("$.message.chat.id");
        String originalText = context.readParameter("$.message.text");

        var text = "nano: " + originalText;
        this.botApi.sendMessage(chatId, text);

        // next
        next.next();
    }
}
