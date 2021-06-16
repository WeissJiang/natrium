package nano.web.telegram.handler;

import com.github.houbb.pinyin.util.PinyinHelper;
import nano.support.LanguageUtils;
import nano.support.Onion;
import nano.web.baidu.TranslationService;
import nano.web.nano.model.Bot;
import nano.web.telegram.BotContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class Nano063Handler implements Onion.Middleware<BotContext> {

    private final TranslationService translationService;

    public Nano063Handler(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        if (Bot.NANO_063.equals(context.bot().getName())) {
            this.toPinyin(context);
        } else {
            next.next();
        }
    }

    private void toPinyin(BotContext context) {
        var text = context.text();
        if (ObjectUtils.isEmpty(text)) {
            context.sendMessage("Input text is empty, please input");
            return;
        }
        String zhText;
        if (LanguageUtils.containsChinese(text)) {
            zhText = text;
        } else if (LanguageUtils.containsRussian(text)) {
            zhText = this.translationService.translate(text, "ru", "zh");
        } else {
            zhText = this.translationService.translate(text, "en", "zh");
        }
        var pinyin = PinyinHelper.toPinyin(zhText);
        context.replyMessage(zhText + "\n---\n" + pinyin);
    }
}
