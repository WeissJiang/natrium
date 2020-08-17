package nano.telegram;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
    public Map<String, Object> sendMessage(Integer chatId, String text) {
        Map<String, Object> parameters = Map.of("chat_id", chatId, "text", text);
        return this.call("sendMessage", parameters);
    }

    /**
     * Set webhook
     */
    public Map<String, Object> setWebhook() {
        var token = this.getNanoTelegramWebhookToken();
        var url = "https://nano-bot.herokuapp.com/api/telegram/" + token;
        return this.call("setWebhook", Map.of("url", url));
    }

    /**
     * Telegram API caller
     */
    public Map<String, Object> call(String method, Map<String, Object> parameters) {
        var token = this.getNanoTelegramApiToken();
        var endpoint = String.format("https://api.telegram.org/bot%s/%s", token, method);
        var url = URI.create(endpoint);
        var request = RequestEntity.post(url).body(parameters);
        var typeReference = new ParameterizedTypeReference<Map<String, Object>>() {
        };
        var response = this.restTemplate.exchange(request, typeReference);
        return response.getBody();
    }

    // API Token

    public String getNanoTelegramApiToken() {
        return this.env.getProperty("NANO_TELEGRAM_API_TOKEN", "");
    }

    /**
     * Webhook token: Just reverse API Token
     */
    public String getNanoTelegramWebhookToken() {
        var apiToken = this.getNanoTelegramApiToken();
        return new StringBuilder(apiToken).reverse().toString();
    }


}
