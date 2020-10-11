package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.nano.Bot;
import nano.web.nano.ConfigVars;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;

/**
 * 帮助
 */
@Deprecated
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
        var bot = this.configVars.getBots().get(Bot.BOT_ROOT);
        if ("supergroup".equals(chatType) && commands.contains("/help@" + bot.getUsername())) {
            context.replyMessage(this.help());
        } else if ("private".equals(chatType) && commands.contains("/help")) {
            context.replyMessage(this.help());
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
