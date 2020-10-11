package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.nano.Bot;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;

@Component
public class Nano233Handler implements Onion.Middleware<BotContext> {

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        if (Bot.NANO_233.equals(context.bot().getName())) {
            this.getSticker(context);
        } else {
            next.next();
        }
    }

    /**
     * convert sticker to jpeg, then reply
     */
    private void getSticker(BotContext context) {
        String stickerFileId = context.read("$.message.sticker.file_id");
        if (stickerFileId == null) {
            context.sendMessage("⚠️The sticker missing");
            return;
        }
        context.sendPhoto(stickerFileId);
    }
}
