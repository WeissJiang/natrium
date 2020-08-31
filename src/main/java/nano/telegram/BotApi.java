package nano.telegram;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.constant.ConfigVars;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
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

    @NonNull
    private final Environment env;

    @NonNull
    private final RestTemplate restTemplate;

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
        var token = this.env.getProperty(ConfigVars.NANO_TELEGRAM_API_TOKEN, "");
        var endpoint = String.format("https://api.telegram.org/bot%s/%s", token, method);
        var url = URI.create(endpoint);
        var request = RequestEntity.post(url).body(parameters);
        var typeReference = new ParameterizedTypeReference<Map<String, Object>>() {
        };
        var response = this.restTemplate.exchange(request, typeReference);
        return response.getBody();
    }
}
