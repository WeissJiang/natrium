package nano.web.telegram.handler.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.mediawiki.WikiService;
import nano.web.telegram.BotContext;
import nano.web.telegram.handler.AbstractCommandHandler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 维基百科
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WikiHandler extends AbstractCommandHandler {

    // language list
    private static final List<String> LANGUAGE_LIST = List.of("zh", "en", "ja");

    @NonNull
    private final WikiService wikiService;

    @Override
    public void handle(BotContext context, String title) {
        var extract = this.fetchExtract(title);
        context.sendMessage(extract);
    }

    private String fetchExtract(String title) {
        for (var language : LANGUAGE_LIST) {
            var extract = this.wikiService.getPageExtract(title, language);
            if (extract != null) {
                return extract;
            }
        }
        return "nano没有找到：" + title;
    }

    @Override
    protected String command() {
        return "wiki";
    }

    @Override
    protected String help() {
        return """
                Usage: /wiki {title}
                """;
    }
}
