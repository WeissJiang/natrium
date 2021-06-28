package nano.web.telegram;

import nano.support.Json;
import nano.support.http.MultiPartBodyPublisher;
import nano.support.io.SimpleResource;
import nano.web.nano.model.Bot;
import nano.web.nano.ConfigVars;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

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
    private final HttpClient httpClient;

    private final ConfigVars configVars;

    public TelegramService(@NotNull RestTemplate restTemplate, @NotNull ConfigVars configVars) {
        Assert.notNull(restTemplate, "restTemplate must be not null");
        Assert.notNull(configVars, "configVars must be not null");
        this.restTemplate = restTemplate;
        this.configVars = configVars;
        this.httpClient = HttpClient.newHttpClient();
    }

    public Map<String, ?> setWebhook() {
        var apiKey = this.configVars.getNanoApiKey();
        var nanoApi = this.configVars.getNanoApi();
        var result = new HashMap<String, Object>();
        for (var bot : this.configVars.getBots().values()) {
            String botName = bot.getName();
            var endpoint = "/api/telegram/webhook/%s/%s".formatted(botName, apiKey);
            var url = createUrl(endpoint, nanoApi);
            var r = this.call(bot, "setWebhook", Map.of("url", url));
            result.put(botName, r);
        }
        return result;
    }

    public Map<String, ?> sendMessage(@NotNull Bot bot, Map<String, ?> payload) {
        var text = (String) payload.get("text");
        // Text of the message to be sent, 1-4096 characters after entities parsing
        Assert.notNull(text, "Text is missing");
        Assert.isTrue(text.length() <= 4096, "Text length too long");
        return this.call(bot, "sendMessage", payload);
    }

    public Map<String, ?> replyPhoto(@NotNull Bot bot, @NotNull Number chatId, @NotNull Number replyToMessageId, @NotNull Resource photo) {
        var payload = Map.of(
                "chat_id", chatId,
                "reply_to_message_id", replyToMessageId,
                "photo", photo
        );
        return this.callPostForm(bot, "sendPhoto", payload);
    }

    public Map<String, ?> getFile(@NotNull Bot bot, @NotNull String fileId) {
        var payload = Map.of("file_id", fileId);
        return this.call(bot, "getFile", payload);
    }

    public Path downloadFile(@NotNull Bot bot, @NotNull String filePath) {
        var fileUrl = getFileUrl(bot, filePath);
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
     * Telegram API caller, Call POST JSON
     */
    public Map<String, ?> call0(@NotNull Bot bot, @NotNull String method, @NotNull Map<String, ?> payload) {
        var telegramApi = getTelegramApi(bot, method);
        var url = URI.create(telegramApi);
        var request = RequestEntity.post(url).body(payload);
        var response = this.restTemplate.exchange(request, STRING_OBJECT_MAP_TYPE);
        return response.getBody();
    }

    public Map<String, ?> call(@NotNull Bot bot, @NotNull String method, @NotNull Map<String, ?> payload) {
        var telegramApi = getTelegramApi(bot, method);
        var url = URI.create(telegramApi);
        var request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Json.encode(payload)))
                .build();
        try {
            var body = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return Json.decodeValueAsMap(body);
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
            throw new RuntimeException(ex);
        }
    }

    /**
     * Build BodyPublisher from payload
     *
     * @param payload payload
     * @return BodyPublisher
     */
    public static HttpRequest.BodyPublisher toBodyPublisher(@NotNull Map<String, ?> payload) {
        Objects.requireNonNull(payload, "payload must be not null");
        var publisher = new MultiPartBodyPublisher();
        payload.forEach((name, value) -> {
            if (value instanceof Path) {
                publisher.addPart(name, MultiPartBodyPublisher.FilePartSpec.from((Path) value));
            } else if (value instanceof File) {
                publisher.addPart(name, MultiPartBodyPublisher.FilePartSpec.from((File) value));
            } else if (value instanceof Supplier<?>) {
                publisher.addPart(name, toFilePartSpec(name, (Supplier<?>) value));
            } else if (value instanceof MultiPartBodyPublisher.FilePartSpec) {
                publisher.addPart(name, (MultiPartBodyPublisher.FilePartSpec) value);
            } else if (value instanceof Resource) {
                publisher.addPart(name, toFilePartSpec(name, (Supplier<?>) (new SimpleResource((Resource) value)::getInputStream)));
            } else {
                publisher.addPart(name, String.valueOf(value));
            }
        });
        return publisher.build();
    }

    /**
     * Convert input stream supplier to file part
     *
     * @param name     form item name
     * @param supplier input stream supplier
     * @return file part
     */
    private static MultiPartBodyPublisher.FilePartSpec toFilePartSpec(String name, Supplier<?> supplier) {

        return new MultiPartBodyPublisher.FilePartSpec() {

            @Override
            public String getFilename() {
                return name;
            }

            @Override
            public InputStream getInputStream() {
                var o = supplier.get();
                if (o instanceof InputStream is) {
                    return is;
                } else {
                    throw new IllegalStateException("Supplier must supplies inputStream");
                }
            }
        };
    }

    /**
     * Telegram API caller, Call POST Form
     */
    public Map<String, ?> callPostForm0(@NotNull Bot bot, @NotNull String method, @NotNull Map<String, ?> payload) {
        var telegramApi = getTelegramApi(bot, method);
        var url = URI.create(telegramApi);
        var formData = new LinkedMultiValueMap<String, Object>();
        payload.forEach(formData::add);
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        var request = RequestEntity.post(url).headers(headers).body(formData);
        var response = this.restTemplate.exchange(request, STRING_OBJECT_MAP_TYPE);
        return response.getBody();
    }

    public Map<String, ?> callPostForm(@NotNull Bot bot, @NotNull String method, @NotNull Map<String, ?> payload) {
        var telegramApi = getTelegramApi(bot, method);
        var url = URI.create(telegramApi);
        var request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "multipart/form-data")
                .POST(toBodyPublisher(payload))
                .build();
        try {
            var body = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return Json.decodeValueAsMap(body);
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof IOException) {
                throw new UncheckedIOException((IOException) ex);
            }
            throw new RuntimeException(ex);
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
