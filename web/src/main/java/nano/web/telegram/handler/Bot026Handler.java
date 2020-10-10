package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.baidu.TranslationService;
import nano.web.nano.Bot;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class Bot026Handler implements Onion.Middleware<BotContext> {

    private final TranslationService translationService;

    public Bot026Handler(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var text = context.text();
        if (!StringUtils.isEmpty(text) && Bot.BOT_026.equals(context.botKey())) {
            var translated = this.translationService.autoTranslate(text);
            context.replyMessage(translated);
        } else {
            next.next();
        }
    }
}
