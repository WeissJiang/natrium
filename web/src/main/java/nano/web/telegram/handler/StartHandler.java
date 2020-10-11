package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.nano.Bot;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;

import java.util.Map;

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
            case Bot.NANO -> replyNanoStart(context);
            case Bot.NANO_026 -> context.replyMessage("Chinese English translation");
            case Bot.NANO_100 -> context.replyMessage("Encyclopedia");
            case Bot.NANO_233 -> context.replyMessage("Get Sticker");
            case Bot.NANO_262 -> context.replyMessage("Evaluate JavaScript");
        }
    }

    private static void replyNanoStart(BotContext context) {
        var text = """
                <b>至尊戒，驭众戒</b>
                <br>
                <a href="https://t.me/nano_026_bot">nano-026</a> - Chinese English translation
                <a href="https://t.me/nano_100_bot">nano-100</a> - Encyclopedia
                <a href="https://t.me/nano_233_bot">nano-233</a> - Get sticker
                <a href="https://t.me/nano_262_bot">nano-262</a> - Evaluate JavaScript
                """;
        var payload = Map.of(
                "chat_id", context.chatId(),
                "reply_to_message_id", context.messageId(),
                "parse_mode", "HTML",
                "disable_web_page_preview", true,
                "text", text
        );
        context.getTelegramService().sendMessage(context.bot(), payload);
    }
}
