package nano.component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

/**
 * @see <a href="https://core.telegram.org/bots/api">Telegram Bot API</a>
 */
@Component
@RequiredArgsConstructor
public class TelegramBotApi {

    @NonNull
    private final RestTemplate restTemplate;

    public Map<String, Object> call(String method, Map<String, Object> parameters) {
        var url = URI.create(buildUrl(method));
        var request = RequestEntity.post(url).body(parameters);
        var typeReference = new ParameterizedTypeReference<Map<String, Object>>() {};
        var response = this.restTemplate.exchange(request, typeReference);
        return response.getBody();
    }

    private String buildUrl(String method) {
        var token = this.nanoTgApiToken();
        return String.format("https://api.telegram.org/bot%s/%s", token, method);
    }

    public Map<String, Object> setWebhook() {
        var token = this.nanoTgWebhookToken();
        var url = "https://nano-bot.herokuapp.com/api/telegram/" + token;
        return this.call("setWebhook", Map.of("url", url));
    }

    // API Token

    public String nanoTgApiToken() {
        var token = System.getenv("NANO_TG_API_TOKEN");
        return token != null ? token : "";
    }

    /**
     * 把API Token倒转一下作为Webhook token用
     */
    public String nanoTgWebhookToken() {
        var apiToken = this.nanoTgApiToken();
        return new StringBuilder(apiToken).reverse().toString();
    }

    public void checkTgWebhookToken(String token) {
        if (!Objects.equals(token, this.nanoTgWebhookToken())) {
            throw new IllegalArgumentException("Illegal webhook token");
        }
    }
}
