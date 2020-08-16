package nano.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.component.TelegramBotApi;
import nano.support.json.JsonObject;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {

    @NonNull
    private final TelegramBotApi telegramBotApi;

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
        var jo = new JsonObject()
                .put("chat_id", chatId)
                .put("text", text);
        return this.telegramBotApi.call("sendMessage", jo.getMap());
    }
}
