package nano.web.telegram;

import nano.web.nano.ConfigVars;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @see <a href="https://core.telegram.org/bots/api">Telegram Bot API</a>
 */
@Service
public class TelegramService {

    private static final ParameterizedTypeReference<Map<String, ?>> STRING_OBJECT_MAP_TYPE = new ParameterizedTypeReference<>() {
    };

    private static final String TELEGRAM_API = "https://api.telegram.org/bot%s/%s";

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
        for (var botKey : this.configVars.getBots().keySet()) {
            var url = "%s/api/telegram/webhook/%s/%s".formatted(nanoApi, botKey, apiKey);
            var r = this.call(botKey, "setWebhook", Map.of("url", url));
            result.put(botKey, r);
        }
        return result;
    }

    /**
     * Send text message
     */
    public Map<String, ?> sendMessage(@NotNull String botKey, @NotNull Number chatId, @NotNull String text) {
        var parameters = Map.of("chat_id", chatId, "text", text);
        return this.call(botKey, "sendMessage", parameters);
    }

    public Map<String, ?> replyMessage(@NotNull String botKey, @NotNull Number chatId,
                                       @NotNull Number replyToMessageId, @NotNull String text) {
        var parameters = Map.of(
                "chat_id", chatId,
                "reply_to_message_id", replyToMessageId,
                "text", text
        );
        return this.call(botKey, "sendMessage", parameters);
    }

    /**
     * Telegram API caller
     */
    public Map<String, ?> call(@NotNull String botKey, @NotNull String method, @NotNull Map<String, ?> parameters) {
        var token = this.getTokenByBotKey(botKey);
        var telegramApi = TELEGRAM_API.formatted(token, method);
        var url = URI.create(telegramApi);
        var request = RequestEntity.post(url).body(parameters);
        var response = this.restTemplate.exchange(request, STRING_OBJECT_MAP_TYPE);
        return response.getBody();
    }

    private String getTokenByBotKey(String botKey) {
        var bot = this.configVars.getBots().get(botKey);
        return Objects.requireNonNull(bot).getToken();
    }
}
