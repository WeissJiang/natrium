package nano.telegram.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.support.Onion;
import nano.telegram.BotApi;
import nano.telegram.BotContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class FoolHandler implements Onion.Middleware<BotContext> {

    @NonNull
    private final BotApi botApi;

    public void via(BotContext context, Onion.Next next) throws Exception {
        Integer chatId = context.readParameter("$.message.chat.id");

        var random = ThreadLocalRandom.current();
        var count = random.nextInt(1, 7);
        var text = "阿巴".repeat(count);
        this.botApi.sendMessage(chatId, text);

        // next
        next.next();
    }
}
