package nano.web.telegram.handler;

import com.github.houbb.pinyin.util.PinyinHelper;
import nano.support.Onion;
import nano.web.nano.model.Bot;
import nano.web.telegram.BotContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class Nano063Handler implements Onion.Middleware<BotContext>{

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
            context.sendMessage("The content is empty, please input the text");
            return;
        }
        var pinyin = PinyinHelper.toPinyin(text);
        context.replyMessage(pinyin);
    }
}
