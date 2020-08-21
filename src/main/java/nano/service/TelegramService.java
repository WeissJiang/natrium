package nano.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nano.constant.ConfigVars;
import nano.telegram.BotApi;
import nano.telegram.BotContext;
import nano.telegram.BotHandler;
import org.springframework.core.env.Environment;
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
    private final Environment env;

    @SneakyThrows
    public void handleWebhook(Map<String, Object> parameters) {
        var context = new BotContext(parameters, this.botApi);
        this.botHandler.handle(context);
    }

    public Map<String, Object> setWebhook() {
        var token = this.env.getProperty(ConfigVars.NANO_TOKEN, "");
        var nanoApi = this.env.getProperty(ConfigVars.NANO_API, "");
        var url = nanoApi + "/api/telegram/" + token;
        return this.botApi.setWebhook(url);
    }

    public Map<String, Object> sendMessage(Integer chatId, String text) {
        return this.botApi.sendMessage(chatId, text);
    }
}
