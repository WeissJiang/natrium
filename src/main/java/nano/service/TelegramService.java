package nano.service;

import com.jayway.jsonpath.JsonPath;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nano.constant.ConfigVars;
import nano.security.SecurityService;
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
        var context = new BotContext();
        context.setParameters(parameters);
        context.setDocumentContext(JsonPath.parse(parameters));
        this.botHandler.handle(context);
    }

    public Map<String, Object> setWebhook() {
        var token = this.env.getProperty(ConfigVars.NANO_TOKEN, "");
        var url = "https://nano-bot.herokuapp.com/api/telegram/" + token;
        return this.botApi.setWebhook(url);
    }

    public Map<String, Object> sendMessage(Integer chatId, String text) {
        return this.botApi.sendMessage(chatId, text);
    }
}
