package nano.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nano.component.ConfigVars;
import nano.security.SecurityService;
import nano.telegram.BotApi;
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

    @NonNull
    private final ConfigVars configVars;

    public void handleWebhook(Map<String, Object> parameters) {
        var context = new BotContext(parameters, this.botApi);
        this.botHandler.handle(context);
    }

    public Map<String, Object> setWebhook() {
        var nanoToken = this.configVars.getNanoApiToken();
        var nanoApi = this.configVars.getNanoApi();
        var url = "%s/api/telegram/webhook%s".formatted(nanoApi, nanoToken);
        return this.botApi.setWebhook(url);
    }

    public Map<String, Object> sendMessage(Integer chatId, String text) {
        return this.botApi.sendMessage(chatId, text);
    }
}
