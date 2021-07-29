package nano.service.nano;

import nano.service.nano.model.Bot;

import java.util.List;
import java.util.Objects;

public record AppConfig(
        String nanoApi,
        String nanoApiKey,
        String baiduTranslationAppId,
        String baiduTranslationSecretKey,
        List<Bot> botList
) {

    public Bot getBot(String name) {
        return this.botList.stream()
                .filter(it -> Objects.equals(it.name(), name))
                .findFirst()
                .orElseThrow();
    }
}
