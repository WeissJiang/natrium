package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.baidu.TranslationService;
import nano.web.nano.Bot;
import nano.web.telegram.BotContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * ZH/EN translation
 */
@Component
public class Nano026Handler implements Onion.Middleware<BotContext> {

    private final TranslationService translationService;

    public Nano026Handler(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        if (Bot.NANO_026.equals(context.bot().getName())) {
            this.translate(context);
        } else {
            next.next();
        }
    }

    private void translate(BotContext context) {
        var text = context.text();
        if (StringUtils.isEmpty(text)) {
            context.sendMessage("⚠️The content is empty, please input the text to be translated");
            return;
        }
        var translated = this.translationService.autoTranslate(text);
        context.replyMessage(translated);
    }
}
