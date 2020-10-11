package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.nano.Bot;
import nano.web.telegram.BotContext;
import nano.web.telegram.TelegramService;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private void getSticker(BotContext context) throws Exception {
        String stickerFileId = context.read("$.message.sticker.file_id");
        if (stickerFileId == null) {
            context.sendMessage("⚠️The sticker missing");
            return;
        }
        var bot = context.bot();
        var service = context.getTelegramService();
        var getFileResult = service.getFile(bot, stickerFileId);
        var filePath = getFilePath(getFileResult);
        var webpUrl = TelegramService.getFileUrl(bot, filePath);
        var pngFilePath = convertWebpToJpeg(webpUrl);
        service.sendPhoto(bot, context.chatId(), new FileSystemResource(pngFilePath));
    }

    private static Path convertWebpToJpeg(String webpUrl) throws Exception {
        var bufferedImage = ImageIO.read(new URL(webpUrl));
        var tempFilePath = Files.createTempFile("convert_webp_to_png", "tmp");
        var tempFile = tempFilePath.toFile();
        tempFile.deleteOnExit();
        ImageIO.write(bufferedImage, "png", tempFile);
        return tempFilePath;
    }

    @SuppressWarnings("unchecked")
    private static @NotNull String getFilePath(Map<String, ?> getFileResult) {
        var result = getFileResult.get("result");
        Assert.isInstanceOf(Map.class, result);
        var filePath = (String) ((Map<String, ?>) result).get("file_path");
        Assert.notNull(filePath, "filePath is null");
        return filePath;
    }

}
