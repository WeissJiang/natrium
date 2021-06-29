package nano.web.telegram;

import nano.support.Json;
import nano.support.NeverException;
import nano.support.http.MultiPartBodyPublisher;
import nano.web.nano.ConfigVars;
import nano.web.nano.model.Bot;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @see <a href="https://core.telegram.org/bots/api">Telegram Bot API</a>
 */
@Service
public class TelegramService {

    private static final String TELEGRAM_API = "https://api.telegram.org/bot%s/%s";
    private static final String TELEGRAM_FILE_API = "https://api.telegram.org/file/bot%s/%s";

    private final HttpClient httpClient;
    private final ConfigVars configVars;

    public TelegramService(@NotNull ConfigVars configVars, @NotNull HttpClient httpClient) {
        Assert.notNull(configVars, "configVars must be not null");
        Assert.notNull(httpClient, "httpClient must be not null");
        this.configVars = configVars;
        this.httpClient = httpClient;
    }

    public Map<String, ?> setWebhook() {
        var apiKey = this.configVars.getNanoApiKey();
        var nanoApi = this.configVars.getNanoApi();
        var result = new HashMap<String, Object>();
        for (var bot : this.configVars.getBots().values()) {
            String botName = bot.getName();
            var endpoint = "/api/telegram/webhook/%s/%s".formatted(botName, apiKey);
            var url = createUrl(endpoint, nanoApi);
            var r = this.postJson(bot, "setWebhook", Map.of("url", url));
            result.put(botName, r);
        }
        return result;
    }

    public Map<String, ?> sendMessage(@NotNull Bot bot, Map<String, ?> payload) {
        var text = (String) payload.get("text");
        // Text of the message to be sent, 1-4096 characters after entities parsing
        Assert.notNull(text, "Text is missing");
        Assert.isTrue(text.length() <= 4096, "Text length too long");
        return this.postJson(bot, "sendMessage", payload);
    }

    public Map<String, ?> replyPhoto(@NotNull Bot bot, @NotNull Number chatId, @NotNull Number replyToMessageId, @NotNull Resource photo) {
        var payload = Map.of(
                "chat_id", chatId,
                "reply_to_message_id", replyToMessageId,
                "photo", photo
        );
        return this.postFormData(bot, "sendPhoto", payload);
    }

    public Map<String, ?> getFile(@NotNull Bot bot, @NotNull String fileId) {
        var payload = Map.of("file_id", fileId);
        return this.postJson(bot, "getFile", payload);
    }

    /**
     * POST JSON to Telegram API
     */
    public Map<String, ?> postJson(@NotNull Bot bot, @NotNull String method, @NotNull Map<String, ?> payload) {
        var telegramApi = getTelegramApi(bot, method);
        var url = URI.create(telegramApi);
        var request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Json.encode(payload)))
                .build();
        try {
            var body = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            if (body == null) {
                return null;
            }
            return Json.decodeValueAsMap(body);
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
            throw new RuntimeException(ex);
        }
    }

    /**
     * POST Form Data to Telegram API
     */
    public Map<String, ?> postFormData(@NotNull Bot bot, @NotNull String method, @NotNull Map<String, ?> payload) {
        Objects.requireNonNull(payload, "payload must be not null");
        var telegramApi = getTelegramApi(bot, method);
        var url = URI.create(telegramApi);

        var publisher = new MultiPartBodyPublisher();
        for (var it : payload.entrySet()) {
            var name = it.getKey();
            var part = it.getValue();
            if (part instanceof Resource resource) {
                publisher.addPart(name, MultiPartBodyPublisher.FilePart.from(resource));
            } else {
                publisher.addPart(name, String.valueOf(part));
            }
        }

        var request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "multipart/form-data; boundary=" + publisher.getBoundary())
                .POST(publisher.build())
                .build();
        try {
            var body = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return Json.decodeValueAsMap(body);
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof IOException) {
                throw new UncheckedIOException((IOException) ex);
            }
            throw new NeverException(ex);
        }
    }

    public static String getFileUrl(@NotNull Bot bot, @NotNull String filePath) {
        var token = bot.getToken();
        return TELEGRAM_FILE_API.formatted(token, filePath);
    }

    public static String getTelegramApi(@NotNull Bot bot, @NotNull String method) {
        var token = bot.getToken();
        return TELEGRAM_API.formatted(token, method);
    }

    private static @NotNull String createUrl(@NotNull String endpoint, @NotNull String base) {
        try {
            return new URL(new URL(base), endpoint).toString();
        } catch (MalformedURLException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
