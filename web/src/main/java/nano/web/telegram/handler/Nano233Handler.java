package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.nano.Bot;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class Nano233Handler implements Onion.Middleware<BotContext> {

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var text = context.text();
        if (!StringUtils.isEmpty(text) && Bot.NANO_233.equals(context.bot().getName())) {
            this.getSticker(context);
        } else {
            next.next();
        }
    }

    private void getSticker(BotContext context) {
        context.replyMessage("See: https://t.me/GIFDownloader_bot");
    }
}
