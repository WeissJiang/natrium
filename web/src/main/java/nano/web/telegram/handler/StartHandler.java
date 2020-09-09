package nano.web.telegram.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.support.Onion;
import nano.web.ConfigVars;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartHandler implements Onion.Middleware<BotContext> {

    @NonNull
    private final ConfigVars configVars;

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var commands = context.commands();
        var chatType = context.chatType();

        if ("supergroup".equals(chatType) && commands.contains("/start@" + configVars.getBotName())) {
            context.sendMessage(this.help());
        } else if ("private".equals(chatType) && commands.contains("/start")) {
            context.sendMessage(this.help());
        } else {
            next.next();
        }
    }

    private String help() {
        return """
                /babel - 中英互译
                /baike - 百度百科
                /eval - Evaluate JavaScript
                /wiki - Wikipedia
                /moe - 萌娘百科
                """;
    }
}
