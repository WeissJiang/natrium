package nano.web.telegram;

import nano.web.nano.Bot;
import nano.web.nano.ConfigVars;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @see <a href="https://core.telegram.org/bots/api">Telegram Bot API</a>
 */
@Service
public class TelegramService {

    private static final ParameterizedTypeReference<Map<String, ?>> STRING_OBJECT_MAP_TYPE = new ParameterizedTypeReference<>() {
    };

    private static final String TELEGRAM_API = "https://api.telegram.org/bot%s/%s";
    private static final String TELEGRAM_FILE_API = "https://api.telegram.org/file/bot%s/%s";

    private final RestTemplate restTemplate;

    private final ConfigVars configVars;

    public TelegramService(RestTemplate restTemplate, ConfigVars configVars) {
        this.restTemplate = restTemplate;
        this.configVars = configVars;
    }

    public Map<String, ?> setWebhook() {
        var apiKey = this.configVars.getNanoApiKey();
        var nanoApi = this.configVars.getNanoApi();
        var result = new HashMap<String, Object>();
        for (var bot : this.configVars.getBots().values()) {
            String botName = bot.getName();
            var url = "%s/api/telegram/webhook/%s/%s".formatted(nanoApi, botName, apiKey);
            var r = this.call(bot, "setWebhook", Map.of("url", url));
            result.put(botName, r);
        }
        return result;
    }

    public Map<String, ?> sendMessage(@NotNull Bot bot, @NotNull Number chatId, @NotNull String text) {
        var parameters = Map.of("chat_id", chatId, "text", text);
        return this.call(bot, "sendMessage", parameters);
    }

    public Map<String, ?> replyMessage(@NotNull Bot bot, @NotNull Number chatId,
                                       @NotNull Number replyToMessageId, @NotNull String text) {
        var parameters = Map.of(
                "chat_id", chatId,
                "reply_to_message_id", replyToMessageId,
                "text", text
        );
        return this.call(bot, "sendMessage", parameters);
    }

    public Map<String, ?> sendPhoto(@NotNull Bot bot, @NotNull Number chatId, @NotNull String photo) {
        var parameters = Map.of("chat_id", chatId, "photo", photo);
        return this.call(bot, "sendPhoto", parameters);
    }

    public Map<String, ?> getFile(@NotNull Bot bot, @NotNull String fileId) {
        var parameters = Map.of("file_id", fileId);
        return this.call(bot, "getFile", parameters);
    }

    public Path downloadFile(@NotNull Bot bot, @NotNull String filePath) {
        var token = bot.getToken();
        var fileUrl = TELEGRAM_FILE_API.formatted(token, filePath);
        return this.restTemplate.execute(fileUrl, HttpMethod.GET, null, response -> {
            var tempFile = Files.createTempFile("download", "tmp");
            tempFile.toFile().deleteOnExit();
            var is = response.getBody();
            Assert.notNull(is, "response input stream require non null");
            var os = Files.newOutputStream(tempFile);
            try (is; os) {
                is.transferTo(os);
            }
            return tempFile;
        });
    }

    /**
     * Telegram API caller
     */
    public Map<String, ?> call(@NotNull Bot bot, @NotNull String method, @NotNull Map<String, ?> parameters) {
        var token = bot.getToken();
        var telegramApi = TELEGRAM_API.formatted(token, method);
        var url = URI.create(telegramApi);
        var request = RequestEntity.post(url).body(parameters);
        var response = this.restTemplate.exchange(request, STRING_OBJECT_MAP_TYPE);
        return response.getBody();
    }
}
