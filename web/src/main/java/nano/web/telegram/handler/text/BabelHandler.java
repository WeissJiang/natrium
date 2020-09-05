package nano.web.telegram.handler.text;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.service.baidu.TranslationService;
import nano.support.Onion;
import nano.web.telegram.BotContext;
import nano.web.telegram.BotUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class BabelHandler implements Onion.Middleware<BotContext> {

    private static final String COMMAND = "babel";

    @NonNull
    private final TranslationService translationService;

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var text = context.text();

        var content = BotUtils.parseCommand(COMMAND, text);
        if (StringUtils.isEmpty(content)) {
            next.next();
            return;
        }

        var translated = this.translationService.autoTranslate(content);
        context.sendMessage(translated);
    }
}