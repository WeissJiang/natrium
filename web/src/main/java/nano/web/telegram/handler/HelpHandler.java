package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.nano.ConfigVars;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;

/**
 * 帮助
 */
@Component
public class HelpHandler implements Onion.Middleware<BotContext> {

    private final ConfigVars configVars;

    public HelpHandler(ConfigVars configVars) {
        this.configVars = configVars;
    }

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var commands = context.commands();
        var chatType = context.chatType();

        if ("supergroup".equals(chatType) && commands.contains("/help@" + configVars.getBotName())) {
            context.sendMessage(this.help());
        } else if ("private".equals(chatType) && commands.contains("/help")) {
            context.sendMessage(this.help());
        } else {
            next.next();
        }
    }

    private String help() {
        return """
                nano Telegram Bot
                指令列表:
                   
                /babel - 中英互译
                /baike - 百度百科
                /eval - Evaluate JavaScript
                /wiki - Wikipedia
                /moe - 萌娘百科
                """;
    }
}
