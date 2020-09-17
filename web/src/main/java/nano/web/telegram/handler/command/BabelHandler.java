package nano.web.telegram.handler.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.baidu.TranslationService;
import nano.web.telegram.BotContext;
import nano.web.telegram.handler.AbstractCommandHandler;
import org.springframework.stereotype.Component;

/**
 * 中英互译
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BabelHandler extends AbstractCommandHandler {

    @NonNull
    private final TranslationService translationService;

    @Override
    public void handle(BotContext context, String text) {
        var translated = this.translationService.autoTranslate(text);
        context.sendMessage(translated);
    }

    @Override
    protected String command() {
        return "babel";
    }

    @Override
    protected String help() {
        return """
                Usage: /babel text
                """;
    }
}
