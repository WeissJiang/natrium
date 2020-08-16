package nano.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.component.HerokuConfigVars;
import nano.component.TelegramBotApi;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {

    @NonNull
    private final HerokuConfigVars herokuConfigVars;

    private final TelegramBotApi telegramBotApi;


    public void checkTgApiToken(String token) {
        if (!Objects.equals(token, this.herokuConfigVars.nanoTgWebhookToken())) {
            throw new IllegalArgumentException("Illegal webhook token");
        }
    }

    public Map<String, Object> handleRequest(Map<String, Object> request) {
        log.info("request: {}", request);
        return new HashMap<>();
    }

    public Map<String, Object> setWebhook() {
        final String token = this.herokuConfigVars.nanoTgWebhookToken();
        String url = "https://nano-bot.herokuapp.com/api/telegram/%s".formatted(token);
        return this.telegramBotApi.call("setWebhook", Map.of("url", url));
    }
}
