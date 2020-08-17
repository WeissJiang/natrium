package nano.service;

import com.jayway.jsonpath.JsonPath;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nano.telegram.BotApi;
import nano.telegram.BotContext;
import nano.telegram.BotHandler;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {

    @NonNull
    private final BotApi botApi;

    @NonNull
    private final BotHandler botHandler;

    @SneakyThrows
    public void handleWebhook(Map<String, Object> parameters) {
        var context = new BotContext();
        context.setParameters(parameters);
        context.setDocumentContext(JsonPath.parse(parameters));
        this.botHandler.handle(context);
    }

    public void checkTelegramWebhookToken(String token) {
        if (!Objects.equals(token, this.botApi.getNanoTelegramWebhookToken())) {
            throw new IllegalArgumentException("Illegal webhook token");
        }
    }

    public Map<String, Object> setWebhook() {
        return this.botApi.setWebhook();
    }
}
