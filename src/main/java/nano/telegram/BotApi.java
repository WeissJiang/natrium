package nano.telegram;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.ConfigVars;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

/**
 * @see <a href="https://core.telegram.org/bots/api">Telegram Bot API</a>
 */
@Component
@RequiredArgsConstructor
public class BotApi {

    private static final ParameterizedTypeReference<Map<String, Object>> STRING_OBJECT_MAP_TYPE = new ParameterizedTypeReference<>() {
    };

    @NonNull
    private final RestTemplate restTemplate;

    @NonNull
    private final ConfigVars configVars;

    /**
     * Send text message
     */
    public Map<String, Object> sendMessage(@NonNull Number chatId, @NonNull String text) {
        Map<String, Object> parameters = Map.of("chat_id", chatId, "text", text);
        return this.call("sendMessage", parameters);
    }

    /**
     * Set webhook
     */
    public Map<String, Object> setWebhook(@NonNull String url) {
        return this.call("setWebhook", Map.of("url", url));
    }

    /**
     * Telegram API caller
     */
    public Map<String, Object> call(@NonNull String method, @NonNull Map<String, Object> parameters) {
        var token = this.configVars.getTelegramApiToken();
        var endpoint = "https://api.telegram.org/bot%s/%s".formatted(token, method);
        var url = URI.create(endpoint);
        var request = RequestEntity.post(url).body(parameters);
        var response = this.restTemplate.exchange(request, STRING_OBJECT_MAP_TYPE);
        return response.getBody();
    }
}
