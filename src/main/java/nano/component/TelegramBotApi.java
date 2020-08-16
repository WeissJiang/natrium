package nano.component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
public class TelegramBotApi {

    @NonNull
    private final HerokuConfigVars herokuConfigVars;

    @NonNull
    private final RestTemplate restTemplate;

    public Map<String, Object> call(String method, Map<String, Object> parameters) {
        var url = URI.create(buildUrl(method));
        var request = RequestEntity.post(url).body(parameters);
        var typeReference = new ParameterizedTypeReference<Map<String, Object>>() { };
        var response = this.restTemplate.exchange(request, typeReference);
        return response.getBody();
    }

    private String buildUrl(String method) {
        var token = this.herokuConfigVars.nanoTgApiToken();
        return String.format("https://api.telegram.org/bot%s/%s", token, method);
    }
}
