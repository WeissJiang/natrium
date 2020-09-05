package nano.web.telegram;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.ConfigVars;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {

    @NonNull
    private final BotApi botApi;

    @NonNull
    private final ConfigVars configVars;

    public Map<String, Object> setWebhook() {
        var nanoToken = this.configVars.getNanoApiToken();
        var nanoApi = this.configVars.getNanoApi();
        var url = "%s/api/telegram/webhook%s".formatted(nanoApi, nanoToken);
        return this.botApi.setWebhook(url);
    }

    public Map<String, Object> sendMessage(Number chatId, String text) {
        return this.botApi.sendMessage(chatId, text);
    }
}
