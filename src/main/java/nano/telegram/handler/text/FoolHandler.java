package nano.telegram.handler.text;

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

    public void via(BotContext context, Onion.Next next) throws Exception {
        var random = ThreadLocalRandom.current();
        var count = random.nextInt(1, 7);
        var text = "阿巴".repeat(count);

        context.sendMessage(text);

        // next
        next.next();
    }
}
