package nano.service.nano;

import nano.service.nano.model.Bot;

import java.util.Map;
import java.util.Objects;

public record AppConfig(
        String nanoApi,
        String nanoApiKey,
        String baiduTranslationAppId,
        String baiduTranslationSecretKey,
        Map<String, Bot> bots
) {

    public Bot getBot(String name) {
        return Objects.requireNonNull(this.bots.get(name));
    }
}
