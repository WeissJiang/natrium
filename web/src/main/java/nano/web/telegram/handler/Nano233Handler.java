package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.nano.Bot;
import nano.web.telegram.BotContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

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
        var stickerFileId = (String) context.read("$.message.sticker.file_id");
        if (stickerFileId == null) {
            context.sendMessage("⚠️The sticker missing");
            return;
        }
        var webpUrl = context.getFileUrl(stickerFileId);
        var pngFilePath = convertWebpToJpeg(webpUrl);
        if (pngFilePath == null) {
            context.sendMessage("⚠️The sticker is not supported");
            return;
        }
        context.replyPhoto(new FileSystemResource(pngFilePath));
    }

    private static @Nullable Path convertWebpToJpeg(@NotNull String webpUrl) throws Exception {
        var bufferedImage = ImageIO.read(new URL(webpUrl));
        if (bufferedImage == null) {
            return null;
        }
        var tempFilePath = Files.createTempFile("convert_webp_to_png", "tmp");
        var tempFile = tempFilePath.toFile();
        tempFile.deleteOnExit();
        ImageIO.write(bufferedImage, "png", tempFile);
        return tempFilePath;
    }

}
