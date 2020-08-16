package nano.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.component.HerokuConfigVars;
import nano.component.TelegramBotApi;
import nano.support.json.JsonObject;
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

    // request router

    public void handleRequest(Map<String, Object> request) {
        var jo = new JsonObject(request);
        log.info("request: {}", jo.encode());
        var message = jo.getJsonObject("message");
        var chatId = message.getJsonObject("chat").getInteger("id");
        var text = message.getString("text");
        // just echo
        var result = this.sendMessage(chatId, text);
        log.info("result: {}", new JsonObject(result).encode());
    }

    // -- logic

    public Map<String, Object> sendMessage(Integer chatId, String text) {
        Map<String, Object> parameters = Map.of(
                "chatId", chatId,
                "text", text
        );
        return this.telegramBotApi.call("sendMessage", parameters);

    }

    public Map<String, Object> setWebhook() {
        var token = this.herokuConfigVars.nanoTgWebhookToken();
        var url = "https://nano-bot.herokuapp.com/api/telegram/" + token;
        return this.telegramBotApi.call("setWebhook", Map.of("url", url));
    }
}
