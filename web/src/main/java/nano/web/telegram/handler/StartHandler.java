package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.nano.Bot;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;

/**
 * Start
 */
@Component
public class StartHandler implements Onion.Middleware<BotContext> {

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var commands = context.commands();
        var bot = context.bot();
        if (commands.contains("/start") || commands.contains("/start@" + bot.getUsername())) {
            replyStartMessage(context);
        } else {
            next.next();
        }
    }

    private static void replyStartMessage(BotContext context) {
        var bot = context.bot();
        switch (bot.getName()) {
            case Bot.NANO -> context.replyMessage("至尊戒，驭众戒");
            case Bot.NANO_026 -> context.replyMessage("Chinese English translation");
            case Bot.NANO_100 -> context.replyMessage("Encyclopedia");
            case Bot.NANO_233 -> context.replyMessage("Get Sticker");
            case Bot.NANO_262 -> context.replyMessage("Evaluate JavaScript");
        }
    }
}
