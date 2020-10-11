package nano.web.telegram.handler;

import nano.support.Json;
import nano.support.Onion;
import nano.web.nano.Bot;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;

import java.util.Map;

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
        var bot = context.bot();
        var service = context.getTelegramService();
        var fileDTO = service.getFile(bot, stickerFileId);
        var payload = Map.of(
                "chat_id", context.chatId(),
                "document", stickerFileId,
                "reply_to_message_id", context.messageId()
        );
        context.sendMessage(Json.encode(fileDTO));
        service.call(bot, "sendDocument", payload);
//        var filePath = (String) fileDTO.get("file_path");
//        Assert.notNull(filePath, "filePath is null");
//        var path = service.downloadFile(bot, filePath);
    }
}
