package nano.web.telegram.handler.command;

import nano.web.baidu.TranslationService;
import nano.web.telegram.BotContext;
import nano.web.telegram.handler.AbstractCommandHandler;
import org.springframework.stereotype.Component;

/**
 * 中英互译
 */
@Component
public class BabelHandler extends AbstractCommandHandler {

    private final TranslationService translationService;

    public BabelHandler(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void handle(BotContext context, String text) {
        var translated = this.translationService.autoTranslate(text);
        context.replyMessage(translated);
    }

    @Override
    protected String command() {
        return "babel";
    }

    @Override
    protected String help() {
        return """
                Usage: /babel {text}
                """;
    }
}
