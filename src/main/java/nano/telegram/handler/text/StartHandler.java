package nano.telegram.handler.text;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.support.Onion;
import nano.telegram.BotContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartHandler implements Onion.Middleware<BotContext> {

    private static final String START = "/start";

    public void via(BotContext context, Onion.Next next) throws Exception {
        var text = context.text();
        if (text == null || !text.startsWith(START)) {
            // next
            next.next();
        }
        context.sendMessage("""
                /babel 中英互译
                /baike 百度百科
                /eval Evaluate JavaScript
                /wiki Wikipedia
                """);
    }
}