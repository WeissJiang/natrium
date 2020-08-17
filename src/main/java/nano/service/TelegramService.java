package nano.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nano.telegram.BotApi;
import nano.support.json.JsonObject;
import nano.telegram.BotContext;
import nano.telegram.BotHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {

    @NonNull
    private final BotApi botApi;

    @NonNull
    private final BotHandler botHandler;

    @SneakyThrows
    public void handleWebhook(Map<String, Object> request) {
        var parameters = new JsonObject(request);
        var context = BotContext.create(parameters);
        this.botHandler.handle(context);
    }

    public void checkTelegramWebhookToken(String token) {
        this.botApi.checkTelegramWebhookToken(token);
    }

    public Map<String, Object> setWebhook() {
        return this.botApi.setWebhook();
    }
}
