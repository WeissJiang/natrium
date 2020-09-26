package nano.web.telegram;

import nano.web.nano.ConfigVars;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

/**
 * @see <a href="https://core.telegram.org/bots/api">Telegram Bot API</a>
 */
@Service
public class TelegramService {

    private static final ParameterizedTypeReference<Map<String, Object>> STRING_OBJECT_MAP_TYPE = new ParameterizedTypeReference<>() { };

    private static final String TELEGRAM_API = "https://api.telegram.org/bot%s/%s";

    private final RestTemplate restTemplate;

    private final ConfigVars configVars;

    public TelegramService(RestTemplate restTemplate, ConfigVars configVars) {
        this.restTemplate = restTemplate;
        this.configVars = configVars;
    }

    public Map<String, Object> setWebhook() {
        var apiKey = this.configVars.getNanoApiKey();
        var nanoApi = this.configVars.getNanoApi();
        var url = "%s/api/telegram/webhook/%s".formatted(nanoApi, apiKey);
        return this.call("setWebhook", Map.of("url", url));
    }

    /**
     * Send text message
     */
    public Map<String, Object> sendMessage(@NotNull Number chatId, @NotNull String text) {
        Map<String, Object> parameters = Map.of("chat_id", chatId, "text", text);
        return this.call("sendMessage", parameters);
    }

    /**
     * Telegram API caller
     */
    public Map<String, Object> call(@NotNull String method, @NotNull Map<String, Object> parameters) {
        var token = this.configVars.getTelegramBotToken();
        var telegramApi = TELEGRAM_API.formatted(token, method);
        var url = URI.create(telegramApi);
        var request = RequestEntity.post(url).body(parameters);
        var response = this.restTemplate.exchange(request, STRING_OBJECT_MAP_TYPE);
        return response.getBody();
    }
}
