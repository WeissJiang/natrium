package nano.web.telegram.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.support.Onion;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartHandler implements Onion.Middleware<BotContext> {

    private static final String HELP = """
            /babel - 中英互译
            /baike - 百度百科
            /eval - Evaluate JavaScript
            /wiki - Wikipedia
            /moe - 萌娘百科
            """;

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        if (context.commands().contains("start")) {
            context.sendMessage(HELP);
            return;
        }
        // next
        next.next();
    }
}
